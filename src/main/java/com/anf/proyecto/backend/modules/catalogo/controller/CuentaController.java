package com.anf.proyecto.backend.modules.catalogo.controller;

import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import com.anf.proyecto.backend.modules.catalogo.repository.CuentaRepository;
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
    private CuentaRepository cuentaRepository;

    @GetMapping
    public ResponseEntity<List<Cuenta>> getCatalogoMaestroCompleto() {
        // Por ahora, devolvemos la lista completa.
        // En el futuro, se puede implementar una lógica que devuelva la estructura de árbol.
        List<Cuenta> cuentas = cuentaRepository.findAll();
        return ResponseEntity.ok(cuentas);
    }
}