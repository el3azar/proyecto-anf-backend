package com.anf.proyecto.backend.modules.empresa.controller;

import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorResponseDTO;
import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorSaveDTO;
import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorUpdateDTO;
import com.anf.proyecto.backend.modules.empresa.service.ParametroSectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/parametros-sector")
public class ParametroSectorController {

    @Autowired
    private ParametroSectorService parametroSectorService;

    /**
     * Endpoint para obtener todos los parámetros de sector.
     * HTTP GET /api/v1/parametros-sector
     */
    @GetMapping
    public ResponseEntity<List<ParametroSectorResponseDTO>> getAll() {
        return ResponseEntity.ok(parametroSectorService.findAll());
    }

    /**
     * Endpoint para obtener un parámetro por su ID.
     * HTTP GET /api/v1/parametros-sector/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParametroSectorResponseDTO> getById(@PathVariable Integer id) {
        return parametroSectorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para crear un nuevo parámetro de sector.
     * HTTP POST /api/v1/parametros-sector
     */
    @PostMapping
    public ResponseEntity<ParametroSectorResponseDTO> create(@RequestBody ParametroSectorSaveDTO saveDTO) {
        ParametroSectorResponseDTO createdParametro = parametroSectorService.save(saveDTO);
        return new ResponseEntity<>(createdParametro, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un parámetro existente.
     * HTTP PUT /api/v1/parametros-sector/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParametroSectorResponseDTO> update(@PathVariable Integer id, @RequestBody ParametroSectorUpdateDTO updateDTO) {
        return parametroSectorService.update(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para eliminar un parámetro.
     * HTTP DELETE /api/v1/parametros-sector/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (parametroSectorService.deleteById(id)) {
            return ResponseEntity.noContent().build(); // Éxito, sin contenido
        } else {
            return ResponseEntity.notFound().build(); // No se encontró el recurso
        }
    }
}
