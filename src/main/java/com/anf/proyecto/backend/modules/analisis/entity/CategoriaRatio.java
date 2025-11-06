package com.anf.proyecto.backend.modules.analisis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "categoria_ratio")
@Data
public class CategoriaRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_categoria_ratio;

    @Column(nullable = false, length = 50)
    private String nombre_tipo;

    @Column(length = 256)
    private String descripcion;

    // Una categoría puede tener muchos tipos de ratio (Parece que la relación en el diagrama está invertida, esta es la forma correcta)
    // @OneToMany(mappedBy = "categoriaRatio")
    // private List<TipoRatio> tiposRatio;
}