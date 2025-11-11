package com.anf.proyecto.backend.modules.analisis.dto.Ratio;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RatioUpdateDTO {

    // El ID del Ratio a actualizar se pasará por la URL (ej: /api/ratios/{id})

    private Integer anio_ratio;

    private String periodo_ratio;

    private BigDecimal valor_calculado;

    // Se permite actualizar la interpretación manualmente si es necesario
    private String interpretacion;

    // Generalmente, no se permite cambiar las relaciones fundamentales de un ratio ya calculado,
    // pero se incluyen aquí por si tu lógica de negocio lo requiere.
    private Integer empresa_id;
    private Integer id_parametro_sector;
}