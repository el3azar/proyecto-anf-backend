package com.anf.proyecto.backend.modules.proyeccion.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentasRequestDTO {
    private Integer empresaId;
    private List<VentaHistoricaDTO> ventas;
}