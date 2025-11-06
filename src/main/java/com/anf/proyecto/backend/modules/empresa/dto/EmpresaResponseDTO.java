package com.anf.proyecto.backend.modules.empresa.dto;

import lombok.Data;

@Data
public class EmpresaResponseDTO {
    private Integer empresaId;
    private String nombreEmpresa;
    private String empresaDui;
    private String empresaNit;
    private String empresaNrc;
    private Integer usuarioId;
    private Integer idSector;
    private String nombreSector; // Incluimos el nombre para facilidad del frontend
}