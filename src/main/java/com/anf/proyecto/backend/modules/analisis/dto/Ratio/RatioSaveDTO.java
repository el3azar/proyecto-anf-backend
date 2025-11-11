package com.anf.proyecto.backend.modules.analisis.dto.Ratio;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RatioSaveDTO {

    private Integer anio_ratio;
    private String periodo_ratio;
    private BigDecimal valor_calculado;
    private String interpretacion; // Opcional, podría ser generada automáticamente
    private Integer empresa_id; // ID de la empresa a la que pertenece el ratio
    private Integer id_categoria_ratio;
    private Integer id_parametro_sector; // Opcional, para enlazar a un parámetro específico
}