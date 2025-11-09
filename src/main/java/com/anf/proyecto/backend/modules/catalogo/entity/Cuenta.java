package com.anf.proyecto.backend.modules.catalogo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "cuenta")
@Data
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id") // Mapeo a la columna snake_case
    private Integer cuentaId;

    @Column(name = "cue_cuenta_id", nullable = false, length = 256)
    private String codigoCuenta;

    @Column(name = "nombre_cuenta", nullable = false, length = 256)
    private String nombreCuenta;

    @Column(name = "codigo_individual", length = 256)
    private String codigoIndividual;

    @Column(name = "codigo_generado", length = 256)
    private String codigoGenerado;

    @Column(name = "tipo_cuenta", length = 50)
    private String tipoCuenta;

    // La relación recursiva para la estructura de árbol
    // En la relación "hacia el padre", le decimos a Jackson que no la serialice para romper el bucle.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_padre")
    @JsonBackReference
    private Cuenta cuentaPadre;

    // En la relación "hacia los hijos", le decimos a Jackson que esta es la parte "principal" a serializar.
    @OneToMany(mappedBy = "cuentaPadre")
    @JsonManagedReference
    private Set<Cuenta> cuentasHijas;
    // Suponiendo que la tabla 'account' de tu script tiene la columna 'is_postable'
    @Column(name = "is_postable")
    private boolean esMovimiento;
}