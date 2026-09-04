package com.veterinariah.mascota.entity;

import com.veterinariah.cliente.entity.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa una mascota almacenada en la tabla {@code mascota} de PostgreSQL.
 *
 * <p>Una mascota siempre pertenece a un cliente. Por eso la relación es
 * muchos-a-uno: un cliente puede tener varias mascotas, pero cada mascota solo
 * tiene un dueño registrado.</p>
 */
@Entity
@Table(name = "mascota")
@Getter
@Setter
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Dueño de la mascota. Se guarda en la columna {@code cliente_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // la mascota no puede existir sin un cliente 
    @JoinColumn(name = "cliente_id", nullable = false) // @manyToOne hace q muchas mascotas pueden pertenecer a un cliente
    private Cliente cliente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String especie;

    /** Datos opcionales porque no todas las mascotas tienen raza definida. */
    @Column(length = 100)
    private String raza;

    @Column(precision = 10, scale = 2)
    private BigDecimal peso;

    /** Por ejemplo: corto, largo, rizado o sin pelo. */
    @Column(name = "tipo_pelo", length = 100)
    private String tipoPelo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
}
