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
    public List<CuentaNodeDTO> getCatalogoMaestroTree() {
        // 1. Obtenemos todas las cuentas de la base de datos en una sola consulta.
        List<Cuenta> allCuentas = cuentaRepository.findAll();

        // 2. Usamos un mapa para un acceso rápido a cada cuenta por su ID.
        Map<Integer, CuentaNodeDTO> nodeMap = new HashMap<>();

        // 3. Convertimos cada entidad 'Cuenta' a un 'CuentaNodeDTO'.
        allCuentas.forEach(cuenta -> {
            CuentaNodeDTO node = new CuentaNodeDTO();
            node.setCuentaId(cuenta.getCuentaId());
            node.setCodigoCuenta(cuenta.getCodigoCuenta());
            node.setNombreCuenta(cuenta.getNombreCuenta());
            node.setEsMovimiento(cuenta.isEsMovimiento());
            nodeMap.put(cuenta.getCuentaId(), node);
        });

        // 4. Construimos la jerarquía del árbol.
        List<CuentaNodeDTO> rootNodes = new ArrayList<>();
        allCuentas.forEach(cuenta -> {
            CuentaNodeDTO currentNode = nodeMap.get(cuenta.getCuentaId());
            if (cuenta.getCuentaPadre() != null) {
                // Si la cuenta tiene un padre, la añadimos a la lista de hijos de ese padre.
                Integer parentId = cuenta.getCuentaPadre().getCuentaId();
                CuentaNodeDTO parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                }
            } else {
                // Si la cuenta no tiene padre, es un nodo raíz.
                rootNodes.add(currentNode);
            }
        });

        // 5. Devolvemos solo los nodos raíz. El resto del árbol está anidado dentro de ellos.
        return rootNodes;
    }
}