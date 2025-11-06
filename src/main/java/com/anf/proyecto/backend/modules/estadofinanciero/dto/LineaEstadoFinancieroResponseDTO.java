package com.anf.proyecto.backend.modules.estadofinanciero.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LineaEstadoFinancieroResponseDTO {
    private Long id;
    private BigDecimal saldo;
    private Integer cuentaId;
    private String codigoCuenta;
    private String nombreCuenta;
}