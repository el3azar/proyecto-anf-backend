package com.anf.proyecto.backend.modules.proyeccion.controller;

import com.anf.proyecto.backend.modules.proyeccion.dto.VentaHistoricaDTO;
import com.anf.proyecto.backend.modules.proyeccion.dto.VentasRequestDTO;
import com.anf.proyecto.backend.modules.proyeccion.service.ProyeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas-historicas")
public class ProyeccionController {

    @Autowired
    private ProyeccionService proyeccionService;

    // --- ENDPOINTS DE CREACIÓN ---

    @PostMapping
    public ResponseEntity<Void> createVentasManualmente(@RequestBody VentasRequestDTO requestDTO) {
        proyeccionService.saveVentasManualmente(requestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadVentasHistoricas(@RequestParam("file") MultipartFile file) {
        proyeccionService.saveVentasFromExcel(file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- ENDPOINTS DE LECTURA ---

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<VentaHistoricaDTO>> getVentasHistoricas(@PathVariable Integer empresaId) {
        return ResponseEntity.ok(proyeccionService.getVentasByEmpresa(empresaId));
    }

    // --- ENDPOINTS DE MODIFICACIÓN ---

    @PutMapping("/{ventaId}")
    public ResponseEntity<VentaHistoricaDTO> updateVenta(
            @PathVariable Long ventaId,
            @RequestBody VentaHistoricaDTO ventaDTO) {
        return ResponseEntity.ok(proyeccionService.updateVenta(ventaId, ventaDTO));
    }

    @DeleteMapping("/{ventaId}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long ventaId) {
        proyeccionService.deleteVenta(ventaId);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT DE CÁLCULO FUTURO ---

    @GetMapping("/empresa/{empresaId}/anio/{anio}")
    public ResponseEntity<List<VentaHistoricaDTO>> getVentasHistoricasPorAnio(
            @PathVariable Integer empresaId,
            @PathVariable int anio) {
        return ResponseEntity.ok(proyeccionService.getVentasByEmpresaAndAnio(empresaId, anio));
    }
}