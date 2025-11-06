package com.anf.proyecto.backend.modules.empresa.controller;

import com.anf.proyecto.backend.modules.empresa.dto.SectorDTO;
import com.anf.proyecto.backend.modules.empresa.service.SectorService;
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

    @PostMapping
    public ResponseEntity<SectorDTO> createSector(@RequestBody SectorDTO sectorDTO) {
        return new ResponseEntity<>(sectorService.createSector(sectorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectorDTO> updateSector(@PathVariable Integer id, @RequestBody SectorDTO sectorDTO) {
        return ResponseEntity.ok(sectorService.updateSector(id, sectorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSector(@PathVariable Integer id) {
        sectorService.deleteSector(id);
        return ResponseEntity.noContent().build();
    }
}