package com.anf.proyecto.backend.modules.analisis.dto.TipoRatio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TipoRatioUpdateDTO {

    private Integer id_tipo_ratio;
    private String nombre_ratio;
    private String codigo_ratio;
    private String descripcion;
    private String unidad_ratio;

}