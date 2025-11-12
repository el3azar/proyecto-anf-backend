package com.anf.proyecto.backend.modules.empresa.dto.ParametroSector;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ParametroSectorSaveDTO {

    private String nombreRatio;

    private BigDecimal valorReferencia;

    private String fuente;

    private Integer anioReferencia;

    private Integer id_sector; // ID de la entidad Sector relacionada

    private Integer id_categoria_ratio; // ID de la entidad CategoriaRatio relacionada

}