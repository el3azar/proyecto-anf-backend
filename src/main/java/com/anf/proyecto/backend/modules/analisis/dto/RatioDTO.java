package com.anf.proyecto.backend.modules.analisis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatioDTO {
    private String nombre;
    private double valorAnio1;
    private double valorAnio2;
}
