package VeterinariaH.VeterinariaH.cliente.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
public class ClienteCrearDto {
    @NotBlank (message = "el nombre no puede estar vacio")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    private String nombre;

    @NotBlank (message = "el apellido no puede estar vacio")
    @Size(max = 100, message = "El apellido no puede tener mas de 100 caracteres")
    private String apellido;

    @NotBlank (message = "el telefono no puede estar vacio")
    @Size(max = 30, message = "El telefono no puede tener mas de 30 caracteres")
    private String telefono;

    @NotBlank (message = "el correo no puede estar vacio")
    @Email (message = "el email debe tener un formato valido")
    @Size(max = 100, message = "El email no puede tener mas de 100 caracteres")
    private String email;

    @Size(max = 200, message = "La direccion no puede tener mas de 200 caracteres")
    private String direccion;

}

