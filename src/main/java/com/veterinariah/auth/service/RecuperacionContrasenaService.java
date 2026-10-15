package com.veterinariah.auth.service;

import com.veterinariah.admin.entity.Admin;
import com.veterinariah.admin.repository.AdminRepository;
import com.veterinariah.auth.dto.RestablecerContrasenaDto;
import com.veterinariah.auth.dto.SolicitarRecuperacionDto;
import com.veterinariah.auth.entity.CodigoRecuperacion;
import com.veterinariah.auth.exception.CodigoRecuperacionInvalidoException;
import com.veterinariah.auth.repository.CodigoRecuperacionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RecuperacionContrasenaService {

    private static final int MINUTOS_VIGENCIA = 10;
    private static final int MAX_INTENTOS = 5;
    private static final int SEGUNDOS_ENTRE_SOLICITUDES = 60;

    private final AdminRepository adminRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("\u0024{spring.mail.username:}")
    private String correoRemitente;

    @Value("\u0024{MAIL_FROM:}")
    private String correoFrom;

    @Transactional
    public void solicitar(SolicitarRecuperacionDto dto) {
        String correo = normalizarCorreo(dto.correo());
        Admin admin = adminRepository.findByCorreoIgnoreCase(correo).orElse(null);

        // La respuesta del controlador es igual exista o no la cuenta.
        if (admin == null) return;

        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);
        CodigoRecuperacion anterior = codigoRepository
                .findFirstByAdmin_IdOrderByCreadoEnDesc(admin.getId())
                .orElse(null);

        if (anterior != null
                && Duration.between(anterior.getCreadoEn(), ahora).getSeconds()
                < SEGUNDOS_ENTRE_SOLICITUDES) {
            return;
        }

        String codigo = String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));
        CodigoRecuperacion recuperacion = new CodigoRecuperacion();
        recuperacion.setAdmin(admin);
        recuperacion.setCodigoHash(passwordEncoder.encode(codigo));
        recuperacion.setCreadoEn(ahora);
        recuperacion.setExpiraEn(ahora.plusMinutes(MINUTOS_VIGENCIA));
        recuperacion.setIntentos((short) 0);
        recuperacion.setUsado(false);
        codigoRepository.save(recuperacion);

        enviarCodigo(correo, codigo);
    }

    @Transactional(noRollbackFor = CodigoRecuperacionInvalidoException.class)
    public void restablecer(RestablecerContrasenaDto dto) {
        String correo = normalizarCorreo(dto.correo());
        Admin admin = adminRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(CodigoRecuperacionInvalidoException::new);

        CodigoRecuperacion recuperacion = codigoRepository
                .findFirstByAdmin_IdOrderByCreadoEnDesc(admin.getId())
                .orElseThrow(CodigoRecuperacionInvalidoException::new);

        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);
        if (recuperacion.isUsado()
                || !ahora.isBefore(recuperacion.getExpiraEn())
                || recuperacion.getIntentos() >= MAX_INTENTOS) {
            recuperacion.setUsado(true);
            codigoRepository.save(recuperacion);
            throw new CodigoRecuperacionInvalidoException();
        }

        if (!passwordEncoder.matches(dto.codigo(), recuperacion.getCodigoHash())) {
            recuperacion.setIntentos((short) (recuperacion.getIntentos() + 1));
            if (recuperacion.getIntentos() >= MAX_INTENTOS) {
                recuperacion.setUsado(true);
            }
            codigoRepository.save(recuperacion);
            throw new CodigoRecuperacionInvalidoException();
        }

        admin.setPassword(passwordEncoder.encode(dto.nuevaContrasena()));
        adminRepository.save(admin);
        recuperacion.setUsado(true);
        codigoRepository.save(recuperacion);
    }

    private void enviarCodigo(String destino, String codigo) {
        if (correoRemitente == null || correoRemitente.isBlank()) {
            throw new IllegalStateException("No está configurado MAIL_USERNAME para enviar correos.");
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(correoFrom == null || correoFrom.isBlank() ? correoRemitente : correoFrom);
            helper.setTo(destino);
            helper.setSubject("Código para recuperar tu contraseña - VeterinariaH");
            helper.setText(
                    "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto'>"
                            + "<h2>Recuperación de contraseña</h2>"
                            + "<p>Usa este código para crear una nueva contraseña:</p>"
                            + "<p style='font-size:30px;font-weight:bold;letter-spacing:8px'>" + codigo + "</p>"
                            + "<p>El código vence en 10 minutos y solo puede usarse una vez.</p>"
                            + "<p>Si no solicitaste este cambio, ignora este mensaje.</p>"
                            + "</div>",
                    true
            );
            mailSender.send(mensaje);
        } catch (MessagingException ex) {
            throw new IllegalStateException("No fue posible preparar el correo de recuperación.", ex);
        }
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
