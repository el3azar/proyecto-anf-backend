package com.anf.proyecto.backend.modules.proyeccion.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProyeccionDTO {
    private LocalDate fechaProyectada;
    private BigDecimal montoProyectado;
}
