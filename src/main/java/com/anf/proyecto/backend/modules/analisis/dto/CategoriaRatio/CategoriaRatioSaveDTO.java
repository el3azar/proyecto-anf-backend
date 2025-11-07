package com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear una nueva entidad CategoriaRatio.
 */
@Data
public class CategoriaRatioSaveDTO {

    // --- CAMBIO AQUÍ ---
    @NotEmpty(message = "El nombre del tipo no puede estar vacío.")
    @Size(max = 50, message = "El nombre del tipo no debe exceder los 50 caracteres.")
    private String nombreTipo;

    @Size(max = 256, message = "La descripción no debe exceder los 256 caracteres.")
    private String descripcion;
}