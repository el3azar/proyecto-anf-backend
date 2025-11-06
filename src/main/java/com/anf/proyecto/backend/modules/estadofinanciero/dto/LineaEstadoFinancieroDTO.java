package com.anf.proyecto.backend.modules.estadofinanciero.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LineaEstadoFinancieroDTO {
    private Integer cuentaId; // ID de la cuenta del catálogo maestro
    private BigDecimal saldo;
}