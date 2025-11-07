package com.anf.proyecto.backend.modules.empresa.dto.sector;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectorSaveDTO {

    @NotEmpty(message = "El nombre del sector no puede estar vacío.")
    @Size(max = 100, message = "El nombre del sector no debe exceder los 100 caracteres.")
    private String nombreSector;

    @Size(max = 256, message = "La descripción no debe exceder los 256 caracteres.")
    private String descripcion;

    @Size(max = 100, message = "El país de referencia no debe exceder los 100 caracteres.")
    private String paisReferencia;

    @Size(max = 256, message = "La fuente de datos no debe exceder los 256 caracteres.")
    private String fuenteDatos;
}