package com.anf.proyecto.backend.modules.analisis.controller; // Asegúrate que el paquete es correcto

import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.service.CategoriaRatioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias-ratio")
public class CategoriaRatioController {

    @Autowired
    private CategoriaRatioService categoriaRatioService;

    @GetMapping
    public ResponseEntity<List<CategoriaRatioResponseDTO>> getAllCategorias() {
        return ResponseEntity.ok(categoriaRatioService.getAllCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaRatioResponseDTO> getCategoriaById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaRatioService.getCategoriaById(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaRatioResponseDTO> createCategoria(@Valid @RequestBody CategoriaRatioSaveDTO saveDTO) {
        CategoriaRatioResponseDTO createdCategoria = categoriaRatioService.createCategoria(saveDTO);
        return new ResponseEntity<>(createdCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaRatioResponseDTO> updateCategoria(@PathVariable Integer id, @Valid @RequestBody CategoriaRatioUpdateDTO updateDTO) {

        return ResponseEntity.ok(categoriaRatioService.updateCategoria(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id) {
        categoriaRatioService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}