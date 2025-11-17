package com.anf.proyecto.backend.modules.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoCuentaAnioDTO {
    private BigDecimal saldo;
    private int anio;

}