package com.anf.proyecto.backend.modules.analisis.controller;


import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.service.RatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratios")
public class RatioController {

    @Autowired
    private RatioService ratioService;


    @GetMapping("/{id}")
    public ResponseEntity<RatioResponseDTO> getRatioById(@PathVariable Integer id) {
        return ratioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RatioResponseDTO> createRatio(@RequestBody RatioSaveDTO saveDTO) {
        RatioResponseDTO createdRatio = ratioService.save(saveDTO);
        return new ResponseEntity<>(createdRatio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatioResponseDTO> updateRatio(@PathVariable Integer id, @RequestBody RatioUpdateDTO updateDTO) {
        return ratioService.update(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatio(@PathVariable Integer id) {
        if (ratioService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Endpoint para obtener ratios.
     * - Si no se provee el parámetro 'nombreEmpresa', devuelve todos los ratios.
     * - Si se provee el parámetro 'nombreEmpresa', filtra los ratios por ese nombre.
     *
     * @param nombreEmpresa (Opcional) El nombre de la empresa para filtrar.
     * @return ResponseEntity con la lista de RatioResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<RatioResponseDTO>> getAllRatios(
            @RequestParam(name = "nombreEmpresa", required = false) String nombreEmpresa) {

        List<RatioResponseDTO> result;

        if (nombreEmpresa != null && !nombreEmpresa.trim().isEmpty()) {
            // Si el parámetro existe y no está vacío, filtra.
            result = ratioService.findByNombreEmpresa(nombreEmpresa);
        } else {
            // De lo contrario, devuelve todos.
            result = ratioService.findAll();
        }

        return ResponseEntity.ok(result);
    }

    // --- ENDPOINT NUEVO PARA EL CÁLCULO ---
    /**
     * Calcula y actualiza los campos derivados de un ratio de liquidez específico.
     * Busca los saldos correspondientes en los estados financieros, realiza los cálculos
     * y persiste los resultados en el ratio.
     *
     * @param id El ID del Ratio a calcular.
     * @return El DTO del Ratio con los campos actualizados.
     */
    @PutMapping("/{id}/calcular-liquidez")
    public ResponseEntity<RatioResponseDTO> calculateLiquidezRatio(@PathVariable Integer id) {
        // Llama al nuevo método del servicio y maneja la respuesta
        return ratioService.calculateLiquidezRatio(id)
                .map(ResponseEntity::ok) // Si el ratio se encuentra y calcula, devuelve 200 OK con el DTO
                .orElse(ResponseEntity.notFound().build()); // Si no se encuentra un ratio con ese ID, devuelve 404 Not Found
    }

}
