package com.anf.proyecto.backend.modules.empresa.dto;

import lombok.Data;

@Data
public class EmpresaRequestDTO {
    private String nombreEmpresa;
    private String empresaDui;
    private String empresaNit;
    private String empresaNrc;
    private Integer usuarioId;
    private Integer idSector;
}