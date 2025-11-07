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

    @GetMapping
    public ResponseEntity<List<RatioResponseDTO>> getAllRatios() {
        return ResponseEntity.ok(ratioService.findAll());
    }

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
}
