package com.anf.proyecto.backend.modules.estadofinanciero.entity;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "estado_financiero")
@Data
public class EstadoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_financiero")
    private Long id;

    @Column(nullable = false)
    private int anio;

    @Column(nullable = false, length = 20)
    private String tipoReporte; // Ej. "BALANCE_GENERAL", "ESTADO_RESULTADOS"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "estadoFinanciero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaEstadoFinanciero> lineas;
}