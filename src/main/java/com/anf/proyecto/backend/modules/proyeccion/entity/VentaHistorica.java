package com.anf.proyecto.backend.modules.proyeccion.entity;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "venta") // Nombre de la tabla según tu diagrama
@Data
public class VentaHistorica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long id;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "monto_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoVenta;

    @Column(length = 256)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}