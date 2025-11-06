package com.anf.proyecto.backend.modules.estadofinanciero.controller;

import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroRequestDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.service.EstadoFinancieroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroResponseDTO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@RestController
@RequestMapping("/api/v1/estados-financieros")
public class EstadoFinancieroController {

    @Autowired
    private EstadoFinancieroService estadoFinancieroService;

    @PostMapping
    public ResponseEntity<Void> createEstadoFinanciero(@RequestBody EstadoFinancieroRequestDTO requestDTO) {
        estadoFinancieroService.saveEstadoFinanciero(requestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/upload")
    public ResponseEntity<Void> createFromExcel(@RequestParam("file") MultipartFile file) {
        // Validación básica del archivo
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        estadoFinancieroService.saveFromExcel(file);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<EstadoFinancieroResponseDTO>> getAllEstadosFinancieros() {
        return ResponseEntity.ok(estadoFinancieroService.getAllEstadosFinancieros());
    }

    // ¡NUEVO ENDPOINT!
    @GetMapping("/{id}")
    public ResponseEntity<EstadoFinancieroResponseDTO> getEstadoFinancieroById(@PathVariable Long id) {
        return ResponseEntity.ok(estadoFinancieroService.getEstadoFinancieroById(id));
    }
}