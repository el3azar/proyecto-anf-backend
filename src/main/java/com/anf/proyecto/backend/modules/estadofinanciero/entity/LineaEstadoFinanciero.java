package com.anf.proyecto.backend.modules.estadofinanciero.entity;

import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "linea_estado_financiero")
@Data
public class LineaEstadoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea")
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_financiero", nullable = false)
    private EstadoFinanciero estadoFinanciero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
}