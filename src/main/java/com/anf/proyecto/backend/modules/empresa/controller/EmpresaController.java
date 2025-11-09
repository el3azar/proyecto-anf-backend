package com.anf.proyecto.backend.modules.empresa.controller;

import com.anf.proyecto.backend.modules.empresa.dto.EmpresaRequestDTO;
import com.anf.proyecto.backend.modules.empresa.dto.EmpresaResponseDTO;
import com.anf.proyecto.backend.modules.empresa.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> createEmpresa(@RequestBody EmpresaRequestDTO requestDTO) {
        return new ResponseEntity<>(empresaService.createEmpresa(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> getAllEmpresas() {
        return ResponseEntity.ok(empresaService.getAllEmpresas());
    }

    // --- ENDPOINT FALTANTE AÑADIDO ---
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaById(@PathVariable Integer id) {
        return ResponseEntity.ok(empresaService.getEmpresaById(id));
    }

    // --- ENDPOINT FALTANTE AÑADIDO ---
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> updateEmpresa(@PathVariable Integer id, @RequestBody EmpresaRequestDTO requestDTO) {
        return ResponseEntity.ok(empresaService.updateEmpresa(id, requestDTO));
    }

    // --- ENDPOINT FALTANTE AÑADIDO ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Integer id) {
        empresaService.deleteEmpresa(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content, que es la mejor práctica para DELETE
    }
}