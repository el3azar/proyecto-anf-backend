package com.anf.proyecto.backend.modules.empresa.controller;

import com.anf.proyecto.backend.modules.empresa.dto.SectorDTO;
import com.anf.proyecto.backend.modules.empresa.dto.sector.SectorSaveDTO;
import com.anf.proyecto.backend.modules.empresa.dto.sector.SectorUpdateDTO;
import com.anf.proyecto.backend.modules.empresa.service.SectorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sectores")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @GetMapping
    public ResponseEntity<List<SectorDTO>> getAllSectores() {
        return ResponseEntity.ok(sectorService.getAllSectores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorDTO> getSectorById(@PathVariable Integer id) {
        return ResponseEntity.ok(sectorService.getSectorById(id));
    }

    // Cambiado para recibir SectorSaveDTO y activar la validación con @Valid
    @PostMapping
    public ResponseEntity<SectorDTO> createSector(@Valid @RequestBody SectorSaveDTO saveDTO) {
        return new ResponseEntity<>(sectorService.createSector(saveDTO), HttpStatus.CREATED);
    }

    // Cambiado para recibir SectorUpdateDTO y activar la validación con @Valid
    @PutMapping("/{id}")
    public ResponseEntity<SectorDTO> updateSector(@PathVariable Integer id, @Valid @RequestBody SectorUpdateDTO updateDTO) {
        // Opcional: Asegurarse que el ID de la URL coincida con el del body
        if (!id.equals(updateDTO.getIdSector())) {
            // Puedes lanzar una excepción personalizada aquí (e.g., BadRequestException)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(sectorService.updateSector(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSector(@PathVariable Integer id) {
        sectorService.deleteSector(id);
        return ResponseEntity.noContent().build();
    }
}