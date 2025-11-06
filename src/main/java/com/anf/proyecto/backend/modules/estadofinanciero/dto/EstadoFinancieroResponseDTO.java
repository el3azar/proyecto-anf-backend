package com.anf.proyecto.backend.modules.estadofinanciero.dto;

import lombok.Data;
import java.util.List;

@Data
public class EstadoFinancieroResponseDTO {
    private Long id;
    private int anio;
    private String tipoReporte;
    private Integer empresaId;
    private String nombreEmpresa;
    private List<LineaEstadoFinancieroResponseDTO> lineas;
}