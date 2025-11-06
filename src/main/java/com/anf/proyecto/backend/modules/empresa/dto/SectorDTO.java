package com.anf.proyecto.backend.modules.empresa.dto;

import lombok.Data;

@Data
public class SectorDTO {
    private Integer idSector;
    private String nombreSector;
    private String descripcion;
    private String paisReferencia;
    private String fuenteDatos;
}