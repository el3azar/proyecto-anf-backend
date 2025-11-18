package com.anf.proyecto.backend.modules.analisis.controller;

import com.anf.proyecto.backend.modules.analisis.dto.EvolucionResponse;
import com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO;
import com.anf.proyecto.backend.modules.analisis.service.AnalisisService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*;


import java.util.List; 
import java.util.Map;  

@RestController
@RequestMapping("/api/v1/analisis") 
@RequiredArgsConstructor
public class AnalisisController {

    @Autowired
    private AnalisisService analisisService;  


    @GetMapping("/reporte-interno")
    public ReporteInternoDTO generarReporteInterno(
            // --- CORREGIDO ---
            @RequestParam Integer empresaId, 
            @RequestParam int anio1,
            @RequestParam int anio2) {
        return analisisService.generarReporteInterno(empresaId, anio1, anio2);
    }

    @GetMapping("/reporte-interno/mock")
    public ReporteInternoDTO generarReporteInternoMock(
            // --- CORREGIDO ---
            @RequestParam Integer empresaId,
            @RequestParam int anio1,
            @RequestParam int anio2) {

        // ... (Tu código mock se queda igual)
        var analisisHorizontal = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.1", "Efectivo y Equivalentes", 12000, 15000,
                        3000, 25.0, 0, 0
                )
                // ... más lineas mock
        );
        var analisisVertical = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO(
                        "1.1.1", "Efectivo y Equivalentes", 12000, 15000,
                        0, 0, 24.0, 25.0
                )
                // ... más lineas mock
        );
        var ratios = java.util.List.of(
                new com.anf.proyecto.backend.modules.analisis.dto.RatioDTO("Liquidez Corriente", 1.5, 1.8)
                // ... más lineas mock
        );

        return new com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO(
                empresaId, anio1, anio2,
                analisisHorizontal,
                analisisVertical,
                ratios
        );
    }

    @PostMapping("/evolucion-ratios/{empresaId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerEvolucionRatios(
                    @PathVariable Integer empresaId,
                    @RequestBody List<Integer> ratiosSolicitados) {

            List<Map<String, Object>> data = analisisService.calcularEvolucionRatios(empresaId, ratiosSolicitados);

            return ResponseEntity.ok(data);
    }

    @GetMapping("/evolucion-ratios")
    public ResponseEntity<List<Map<String, Object>>> getEvolucionRatios(
                    @RequestParam Integer empresaId,
                    @RequestParam List<Integer> ratios) {

            List<Map<String, Object>> data = analisisService.calcularEvolucionRatios(empresaId, ratios);

            return ResponseEntity.ok(data);
    }
  

}