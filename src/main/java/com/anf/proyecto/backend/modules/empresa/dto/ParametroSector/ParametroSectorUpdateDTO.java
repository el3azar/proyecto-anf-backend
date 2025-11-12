package com.anf.proyecto.backend.modules.empresa.dto.ParametroSector;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ParametroSectorUpdateDTO {

    // No se incluye el ID aquí, ya que se pasará como parámetro en la URL (ej. PUT /parametros-sector/{id})

    private String nombreRatio;

    private BigDecimal valorReferencia;

    private String fuente;

    private Integer anioReferencia;

    private Integer id_sector; // Para permitir cambiar el sector

    private Integer id_categoria_ratio; // Para permitir cambiar la categoría del ratio

}