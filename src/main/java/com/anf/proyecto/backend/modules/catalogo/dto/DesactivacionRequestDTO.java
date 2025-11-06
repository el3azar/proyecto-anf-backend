package com.anf.proyecto.backend.modules.catalogo.dto;

import lombok.Data;
import java.util.List;

@Data
public class DesactivacionRequestDTO {
    // Una lista de los IDs de la tabla 'catalogo' que se van a desactivar.
    private List<Integer> catalogoIds;
}