package com.anf.proyecto.backend.modules.analisis.dto.Ratio;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RatioResponseDTO {

    private Integer id_ratio;
    private Integer anio_ratio;
    private String periodo_ratio;
    private BigDecimal valor_calculado;
    private BigDecimal valor_sector_promedio;
    private BigDecimal diferencia_vs_sector;
    private Boolean cumple_sector;
    private String interpretacion;

    // --- Objetos DTO para las relaciones ---
    private EmpresaDTO empresa;
    private CategoriaRatioDTO categoriaRatio;
    private TipoRatioDTO tipoRatio;
    private ParametroSectorDTO parametroSector;


    // --- DTOs Anidados para representar las relaciones ---

    @Data
    @NoArgsConstructor
    public static class EmpresaDTO {
        private Integer empresa_id;
        private String nombre_empresa; // Ajusta el nombre del campo si es diferente
    }

    @Data
    @NoArgsConstructor
    public static class CategoriaRatioDTO {
        private Integer id_categoria_ratio;
        private String nombre_categoria; // Ajusta el nombre del campo si es diferente
    }

    @Data
    @NoArgsConstructor
    public static class TipoRatioDTO {
        private Integer id_tipo_ratio;
        private String nombre_ratio;
        private String codigo_ratio;
    }

    @Data
    @NoArgsConstructor
    public static class ParametroSectorDTO {
        private Integer id_parametro_sector;
        private BigDecimal valor_referencia;
        private Integer anio_referencia;
    }
}