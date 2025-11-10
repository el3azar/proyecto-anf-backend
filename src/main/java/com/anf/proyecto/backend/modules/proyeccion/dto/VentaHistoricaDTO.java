package com.anf.proyecto.backend.modules.proyeccion.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaHistoricaDTO {
    private Long id;
    private LocalDate fechaVenta;
    private BigDecimal montoVenta;
    private String observacion;
    private Integer empresaId;
}