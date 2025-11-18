package com.anf.proyecto.backend.modules.analisis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvolucionRatioDTO {
    private String ratio;
    private int anio;
    private Double valor;
}
