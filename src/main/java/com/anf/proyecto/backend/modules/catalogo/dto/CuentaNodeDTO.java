package com.anf.proyecto.backend.modules.catalogo.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CuentaNodeDTO {
    // Datos de la cuenta que el frontend necesita
    private Integer cuentaId;
    private String codigoCuenta;
    private String nombreCuenta;
    private boolean esMovimiento;

    // La lista de hijos, que también son nodos
    private List<CuentaNodeDTO> children = new ArrayList<>();
}