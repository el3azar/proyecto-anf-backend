package com.anf.proyecto.backend.modules.analisis.dto;

import lombok.Data;
import java.util.List;

@Data
public class EvolucionResponse {
    private List<EvolucionRatioDTO> datos;
}
