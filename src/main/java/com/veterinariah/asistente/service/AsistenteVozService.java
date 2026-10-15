package com.veterinariah.asistente.service;

import com.veterinariah.asistente.dto.RespuestaVozResponse;
import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.mascota.entity.Mascota;
import com.veterinariah.mascota.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenteVozService {

    private static final int MAX_CONSULTAS_POR_MINUTO = 8;

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final GeminiIntentService geminiIntentService;
    // Límite por administrador; para varias instancias se debe mover a un almacén compartido.
    private final Map<Long, Deque<Instant>> consultasRecientes = new HashMap<>();

    public RespuestaVozResponse consultar(String preguntaOriginal, Long adminId) {
        limitarConsultas(adminId);

        String pregunta = normalizar(preguntaOriginal);
        List<Cliente> coincidencias = clienteRepository.findAll().stream()
                .filter(cliente -> contieneFrase(
                        pregunta,
                        normalizar(cliente.getNombre() + " " + cliente.getApellido())
                ))
                .toList();

        if (coincidencias.isEmpty()) {
            return new RespuestaVozResponse(
                    "No identifiqué al cliente. Di su nombre y apellido completos, tal como aparecen registrados."
            );
        }
        if (coincidencias.size() > 1) {
            return new RespuestaVozResponse(
                    "Encontré varios clientes con ese nombre. Di el nombre y apellido completos para distinguirlos."
            );
        }

        Cliente cliente = coincidencias.get(0);
        String nombreCompleto = normalizar(cliente.getNombre() + " " + cliente.getApellido());
        String preguntaAnonimizada = enmascararDatos(preguntaOriginal, nombreCompleto);

        if (!geminiIntentService.solicitaMascotasDeCliente(preguntaAnonimizada)) {
            return new RespuestaVozResponse(
                    "Por ahora puedo responder preguntas sobre las mascotas registradas de un cliente."
            );
        }

        List<Mascota> mascotas = mascotaRepository.findByClienteIdOrderByNombreAsc(cliente.getId());
        String nombreCliente = cliente.getNombre() + " " + cliente.getApellido();

        if (mascotas.isEmpty()) {
            return new RespuestaVozResponse(
                    "No hay mascotas registradas a nombre de " + nombreCliente + "."
            );
        }

        String listaMascotas = mascotas.stream()
                .map(mascota -> mascota.getNombre() + ", " + mascota.getEspecie())
                .collect(Collectors.joining("; "));

        String cantidad = mascotas.size() == 1 ? "una mascota" : mascotas.size() + " mascotas";
        return new RespuestaVozResponse(
                nombreCliente + " tiene " + cantidad + ": " + listaMascotas + "."
        );
    }

    private synchronized void limitarConsultas(Long adminId) {
        Instant ahora = Instant.now();
        Instant limite = ahora.minusSeconds(60);
        Deque<Instant> recientes = consultasRecientes.computeIfAbsent(adminId, id -> new ArrayDeque<>());

        while (!recientes.isEmpty() && recientes.peekFirst().isBefore(limite)) {
            recientes.removeFirst();
        }
        if (recientes.size() >= MAX_CONSULTAS_POR_MINUTO) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Se hicieron muchas consultas seguidas. Espera un minuto."
            );
        }
        recientes.addLast(ahora);
    }

    private String enmascararDatos(String preguntaOriginal, String nombreCompleto) {
        String sinCorreos = (preguntaOriginal == null ? "" : preguntaOriginal).replaceAll(
                "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}",
                " dato "
        );
        String sinNombre = normalizar(sinCorreos).replace(nombreCompleto, " cliente ");
        return sinNombre.replaceAll("\\b\\d{3,}\\b", " dato ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean contieneFrase(String texto, String frase) {
        if (frase.isBlank()) {
            return false;
        }
        return (" " + texto + " ").contains(" " + frase + " ");
    }

    private String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(
                        texto == null ? "" : texto,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}+", "");
        return sinAcentos.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }
}
