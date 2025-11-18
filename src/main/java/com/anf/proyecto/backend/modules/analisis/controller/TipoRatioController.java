package com.anf.proyecto.backend.modules.analisis.controller;


import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.entity.TipoRatio;
import com.anf.proyecto.backend.modules.analisis.repository.TipoRatioRepository;
import com.anf.proyecto.backend.modules.analisis.service.TipoRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-ratio")
public class TipoRatioController {

    @Autowired
    private TipoRatioService tipoRatioService;


    @Autowired
    private TipoRatioRepository tipoRatioRepository;


    /**
     * Endpoint para obtener todos los tipos de ratio.
     * HTTP GET /api/v1/tipos-ratio
     */
    @GetMapping
    public ResponseEntity<List<TipoRatioResponseDTO>> getAllTipoRatios() {
        return ResponseEntity.ok(tipoRatioService.findAll());
    }

    /**
     * Endpoint para obtener un tipo de ratio por su ID.
     * HTTP GET /api/v1/tipos-ratio/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoRatioResponseDTO> getTipoRatioById(@PathVariable Integer id) {
        return tipoRatioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para crear un nuevo tipo de ratio.
     * HTTP POST /api/v1/tipos-ratio
     */
    @PostMapping
    public ResponseEntity<TipoRatioResponseDTO> createTipoRatio(@RequestBody TipoRatioSaveDTO saveDTO) {
        TipoRatioResponseDTO createdTipoRatio = tipoRatioService.save(saveDTO);
        return new ResponseEntity<>(createdTipoRatio, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un tipo de ratio existente.
     * HTTP PUT /api/v1/tipos-ratio/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoRatioResponseDTO> updateTipoRatio(@PathVariable Integer id, @RequestBody TipoRatioUpdateDTO updateDTO) {
        return tipoRatioService.update(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para eliminar un tipo de ratio.
     * HTTP DELETE /api/v1/tipos-ratio/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoRatio(@PathVariable Integer id) {
        if (tipoRatioService.deleteById(id)) {
            return ResponseEntity.noContent().build(); // Éxito, sin contenido
        } else {
            return ResponseEntity.notFound().build(); // No se encontró el recurso
        }
    }
}
