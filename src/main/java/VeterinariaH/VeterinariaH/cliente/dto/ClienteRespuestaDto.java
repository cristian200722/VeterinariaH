package VeterinariaH.VeterinariaH.cliente.dto;
import lombok.Getter;
import lombok.AllArgsConstructor;
@Getter
@AllArgsConstructor
public class ClienteRespuestaDto {
    private long id_cliente;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;
}
