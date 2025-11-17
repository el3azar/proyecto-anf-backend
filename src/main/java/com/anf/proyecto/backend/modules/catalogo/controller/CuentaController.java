package com.anf.proyecto.backend.modules.catalogo.controller;

import com.anf.proyecto.backend.modules.catalogo.dto.CuentaNodeDTO; // Importa el nuevo DTO
import com.anf.proyecto.backend.modules.catalogo.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas-maestro")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/tree")
    public ResponseEntity<List<CuentaNodeDTO>> getCatalogoMaestroTree(
            @RequestParam(required = false) String nombreCuenta) {
        return ResponseEntity.ok(cuentaService.getCatalogoMaestroTree(nombreCuenta));
    }
}