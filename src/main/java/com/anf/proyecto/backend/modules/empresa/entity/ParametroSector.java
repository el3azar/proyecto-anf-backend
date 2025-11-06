package com.anf.proyecto.backend.modules.empresa.entity;

import com.anf.proyecto.backend.modules.analisis.entity.CategoriaRatio;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "parametro_sector")
@Data
public class ParametroSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro_sector")
    private Integer idParametroSector;

    @Column(name = "nombre_ratio", nullable = false, length = 100)
    private String nombreRatio;

    @Column(name = "valor_referencia", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorReferencia;

    @Column(length = 256)
    private String fuente;

    @Column(name = "anio_referencia")
    private Integer anioReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sector")
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_ratio")
    private CategoriaRatio categoriaRatio;
}