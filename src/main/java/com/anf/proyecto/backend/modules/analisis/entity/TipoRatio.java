package com.anf.proyecto.backend.modules.analisis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipo_ratio")
@Data
public class TipoRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_ratio") // Mapea a la columna con guion bajo
    private Integer idTipoRatio;    // Propiedad en camelCase

    @Column(name = "nombre_ratio", nullable = false, length = 100)
    private String nombreRatio;

    @Column(name = "codigo_ratio", length = 10)
    private String codigoRatio;

    @Column(length = 256)
    private String descripcion;

    @Column(name = "unidad_ratio", length = 20)
    private String unidadRatio;
}