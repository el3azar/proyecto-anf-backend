package com.anf.proyecto.backend.modules.catalogo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActivacionRequestDTO {
    private Integer empresaId;
    private List<Integer> cuentaIds;
}