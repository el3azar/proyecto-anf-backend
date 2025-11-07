package com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para actualizar una entidad CategoriaRatio existente.
 */

@Data
public class CategoriaRatioUpdateDTO {

    @NotNull(message = "El ID de la categoría es obligatorio para actualizar.")
    private Integer idCategoriaRatio; // Propiedad en camelCase

    @NotEmpty(message = "El nombre del tipo no puede estar vacío.")
    @Size(max = 50, message = "El nombre del tipo no debe exceder los 50 caracteres.")
    private String nombreTipo;

    @Size(max = 256, message = "La descripción no debe exceder los 256 caracteres.")
    private String descripcion;
}