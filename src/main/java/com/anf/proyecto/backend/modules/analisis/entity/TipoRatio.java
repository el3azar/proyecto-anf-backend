package com.anf.proyecto.backend.modules.analisis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipo_ratio")
@Data
public class TipoRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_tipo_ratio;

    @Column(nullable = false, length = 100)
    private String nombre_ratio;

    @Column(length = 10)
    private String codigo_ratio;

    @Column(length = 256)
    private String descripcion;

    @Column(length = 20)
    private String unidad_ratio;


}