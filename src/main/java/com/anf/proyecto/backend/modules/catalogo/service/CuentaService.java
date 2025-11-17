package com.anf.proyecto.backend.modules.catalogo.service;

import com.anf.proyecto.backend.modules.catalogo.dto.CuentaNodeDTO;
import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import com.anf.proyecto.backend.modules.catalogo.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    public List<CuentaNodeDTO> getCatalogoMaestroTree(String nombreCuenta) {
        List<Cuenta> cuentas;

        if (nombreCuenta != null && !nombreCuenta.trim().isEmpty()) {
            // Si se proporciona un nombre, filtra las cuentas
            cuentas = cuentaRepository.findByNombreCuentaContainingIgnoreCase(nombreCuenta);
        } else {
            // De lo contrario, obtiene todas las cuentas
            cuentas = cuentaRepository.findAll();
        }

        Map<Integer, CuentaNodeDTO> nodeMap = new HashMap<>();
        cuentas.forEach(cuenta -> {
            CuentaNodeDTO node = new CuentaNodeDTO();
            node.setCuentaId(cuenta.getCuentaId());
            node.setCodigoCuenta(cuenta.getCodigoCuenta());
            node.setNombreCuenta(cuenta.getNombreCuenta());
            node.setEsMovimiento(cuenta.isEsMovimiento());
            nodeMap.put(cuenta.getCuentaId(), node);
        });

        // Si no se está filtrando, construye la estructura de árbol
        if (nombreCuenta == null || nombreCuenta.trim().isEmpty()) {
            List<CuentaNodeDTO> rootNodes = new ArrayList<>();
            cuentas.forEach(cuenta -> {
                CuentaNodeDTO currentNode = nodeMap.get(cuenta.getCuentaId());
                if (cuenta.getCuentaPadre() != null) {
                    CuentaNodeDTO parentNode = nodeMap.get(cuenta.getCuentaPadre().getCuentaId());
                    if (parentNode != null) {
                        parentNode.getChildren().add(currentNode);
                    }
                } else {
                    rootNodes.add(currentNode);
                }
            });
            return rootNodes;
        }

        // Si se está filtrando, devuelve una lista plana con los resultados
        return new ArrayList<>(nodeMap.values());
    }
}