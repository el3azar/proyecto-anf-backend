package com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio;

import lombok.Data;

/**
 * DTO para devolver la información de una entidad CategoriaRatio.
 */
@Data
public class CategoriaRatioResponseDTO {

    private Integer idCategoriaRatio; // Propiedad en camelCase
    private String nombreTipo;
    private String descripcion;
}