package com.anf.proyecto.backend.modules.catalogo.controller;

import com.anf.proyecto.backend.modules.catalogo.dto.CuentaNodeDTO; // Importa el nuevo DTO
import com.anf.proyecto.backend.modules.catalogo.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas-maestro")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/tree") // Cambiamos la ruta para que sea más descriptiva
    public ResponseEntity<List<CuentaNodeDTO>> getCatalogoMaestroTree() {
        return ResponseEntity.ok(cuentaService.getCatalogoMaestroTree());
    }
}