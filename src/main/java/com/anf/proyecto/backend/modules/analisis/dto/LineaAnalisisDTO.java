package com.anf.proyecto.backend.modules.analisis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineaAnalisisDTO {
    private String codigoCuenta;
    private String nombreCuenta;
    private double saldoAnio1;
    private double saldoAnio2;
    private double variacionAbsoluta;
    private double variacionRelativa;
    private double porcentajeVerticalAnio1;
    private double porcentajeVerticalAnio2;
}
