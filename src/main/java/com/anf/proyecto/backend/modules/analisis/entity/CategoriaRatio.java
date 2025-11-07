package com.anf.proyecto.backend.modules.analisis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
// import jakarta.persistence.Column; // Asegúrate de tener este import

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categoria_ratio")
@Data
public class CategoriaRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria_ratio") // Mapea a la columna con guion bajo
    private Integer idCategoriaRatio;    // Propiedad en camelCase

    @Column(name = "nombre_tipo", nullable = false, length = 50)
    private String nombreTipo;

    @Column(length = 256)
    private String descripcion;
}