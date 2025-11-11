package com.anf.proyecto.backend.modules.analisis.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categoria_ratio")
@Data
public class CategoriaRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria_ratio")
    private Integer idCategoriaRatio;

    @Column(name = "nombre_tipo", nullable = false, length = 50)
    private String nombreTipo;

    @Column(length = 256)
    private String descripcion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ratio", nullable = false)
    private TipoRatio tipoRatio;

}