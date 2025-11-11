package com.anf.proyecto.backend.modules.analisis.controller;

import com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO;
import com.anf.proyecto.backend.modules.analisis.service.AnalisisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analisis")
public class AnalisisController {

    @Autowired
    private AnalisisService analisisService;

    @GetMapping("/reporte-interno")
    public ReporteInternoDTO generarReporteInterno(
            @RequestParam Integer empresaId,
            @RequestParam int anio1,
            @RequestParam int anio2) {
        return analisisService.generarReporteInterno(empresaId, anio1, anio2);
    }

    @GetMapping("/reporte-interno/mock")
    public ReporteInternoDTO generarReporteInternoMock(
            @RequestParam Integer empresaId,
            @RequestParam int anio1,
            @RequestParam int anio2) {

        // --- Análisis Horizontal (comparación entre años)
        var analisisHorizontal = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.1", "Efectivo y Equivalentes", 12000, 15000,
                        3000, 25.0, 0, 0
                ),
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.2", "Cuentas por Cobrar", 8000, 7000,
                        -1000, -12.5, 0, 0
                ),
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "4.1.1", "Ventas Netas", 50000, 60000,
                        10000, 20.0, 0, 0
                )
        );

        // --- Análisis Vertical (porcentajes dentro del total)
        var analisisVertical = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.1", "Efectivo y Equivalentes", 12000, 15000,
                        0, 0, 24.0, 25.0
                ),
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.2", "Cuentas por Cobrar", 8000, 7000,
                        0, 0, 16.0, 11.7
                ),
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "4.1.1", "Ventas Netas", 50000, 60000,
                        0, 0, 100.0, 100.0
                )
        );

        // --- Ratios (valores por año)
        var ratios = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.RatioDTO("Liquidez Corriente", 1.5, 1.8),
                new com.anf.proyecto.backend.modules.analisis.dto.RatioDTO("Endeudamiento", 0.45, 0.42),
                new com.anf.proyecto.backend.modules.analisis.dto.RatioDTO("Rentabilidad Neta", 0.12, 0.15)
        );

        // --- Armar DTO final
        return new com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO(
                empresaId, anio1, anio2,
                analisisHorizontal,
                analisisVertical,
                ratios
        );
    }

}

