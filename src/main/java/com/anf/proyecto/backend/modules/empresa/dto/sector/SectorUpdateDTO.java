package com.anf.proyecto.backend.modules.empresa.dto.sector;

import lombok.Data;

/**
 * DTO para actualizar una entidad Sector existente.
 */
@Data
public class SectorUpdateDTO {

    private Integer idSector;

    private String nombreSector;

    private String descripcion;

    private String paisReferencia;

    private String fuenteDatos;
}