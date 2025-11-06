package com.anf.proyecto.backend.modules.catalogo.controller;

import com.anf.proyecto.backend.modules.catalogo.dto.ActivacionRequestDTO;
import com.anf.proyecto.backend.modules.catalogo.dto.CatalogoResponseDTO;
import com.anf.proyecto.backend.modules.catalogo.dto.DesactivacionRequestDTO;
import com.anf.proyecto.backend.modules.catalogo.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo-empresa")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @PostMapping("/activar")
    public ResponseEntity<Void> activarCuentas(@RequestBody ActivacionRequestDTO requestDTO) {
        catalogoService.activarCuentas(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<CatalogoResponseDTO>> getCatalogoPorEmpresa(@PathVariable Integer empresaId) {
        return ResponseEntity.ok(catalogoService.getCatalogoActivoPorEmpresa(empresaId));
    }

    @PostMapping("/desactivar")
    public ResponseEntity<Void> desactivarCuentas(@RequestBody DesactivacionRequestDTO requestDTO) {
        catalogoService.desactivarCuentas(requestDTO);
        return ResponseEntity.ok().build();
    }
}