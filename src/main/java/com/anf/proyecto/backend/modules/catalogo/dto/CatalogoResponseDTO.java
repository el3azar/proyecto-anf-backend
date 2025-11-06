package com.anf.proyecto.backend.modules.catalogo.dto;

import lombok.Data;

@Data
public class CatalogoResponseDTO {
    // Datos del registro de enlace 'Catalogo'
    private Integer idCatalogo;
    private Boolean activo;
    private Integer empresaId;

    // --- DATOS AÑADIDOS DE LA CUENTA MAESTRA ORIGINAL ---
    private Integer cuentaId;      // El ID de la cuenta maestra
    private String codigoCuenta;   // El código de la cuenta maestra (ej. "110101")
    private String nombreCuenta;   // El nombre de la cuenta maestra (ej. "Caja General")
    private String tipoCuenta;     // Ej. "ACTIVO"
    private boolean esMovimiento;  // 'isPostable'
}