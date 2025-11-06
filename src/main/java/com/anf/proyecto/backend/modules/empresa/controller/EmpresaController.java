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
}