package com.anf.proyecto.backend.modules.catalogo.entity;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogo")
@Data
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo") // Mapeo a la columna snake_case
    private Integer idCatalogo;

    @Column(nullable = false)
    private Boolean activo;

    // Relación: Muchas entradas de catálogo pertenecen a una empresa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Relación: Muchas entradas de catálogo apuntan a una cuenta maestra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
}