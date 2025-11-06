package com.anf.proyecto.backend.modules.estadofinanciero.dto;

import lombok.Data;
import java.util.List;

@Data
public class EstadoFinancieroRequestDTO {
    private Integer empresaId;
    private int anio;
    private String tipoReporte; // "BALANCE_GENERAL" o "ESTADO_RESULTADOS"
    private List<LineaEstadoFinancieroDTO> lineas;
}