package VeterinariaH.VeterinariaH.cliente.entity;
 import jakarta.persistence.*;
 import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, length = 100 )
    private String nombre;

    @Column(nullable = false, length = 100 )
    private String apellido;

    @Column(length = 30 )
    private String telefono;

    @Column (nullable = false, unique = true, length = 100)
    private String email;

    @Column (length = 200 )
    private String direccion;


}
