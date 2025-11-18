package com.anf.proyecto.backend.modules.analisis.entity;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.entity.ParametroSector;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


@Entity
@Table(name = "ratio")
@Data
public class Ratio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ratio;

    @Column
    private Integer anio_ratio;

    @Column(length = 10)
    private String periodo_ratio; // Ej. "Anual", "Q1", "Q2"

    @Column(precision = 15, scale = 2)
    private BigDecimal valor_calculado;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor_sector_promedio;

    @Column(precision = 15, scale = 2)
    private BigDecimal diferencia_vs_sector;

    @Column
    private Boolean cumple_sector;

    @Column(length = 256)
    private String interpretacion;

    // --- Relaciones (Claves Foráneas) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Relación: Un Tipo de Ratio pertenece a una Categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_ratio") // Suponiendo que esta FK debería estar aquí
    private CategoriaRatio categoriaRatio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ratio", nullable = false)
    private TipoRatio tipoRatio;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parametro_sector")
    private ParametroSector parametroSector;
}