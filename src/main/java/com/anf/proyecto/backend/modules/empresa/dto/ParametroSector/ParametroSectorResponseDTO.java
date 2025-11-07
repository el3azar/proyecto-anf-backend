package com.anf.proyecto.backend.modules.empresa.dto.ParametroSector;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ParametroSectorResponseDTO {

    private Integer idParametroSector;

    private String nombreRatio;

    private BigDecimal valorReferencia;

    private String fuente;

    private Integer anioReferencia;

    private SectorDTO sector; // Objeto DTO para la información del Sector

    private CategoriaRatioDTO categoriaRatio; // Objeto DTO para la CategoriaRatio

    /**
     * DTO anidado para representar la información del Sector.
     * Asume que la entidad Sector tiene al menos un ID y un nombre.
     */
    @Data
    @NoArgsConstructor
    public static class SectorDTO {
        private Integer id_sector;
        private String nombre_sector; // Ajusta el nombre del campo si es diferente en tu entidad Sector
    }

    /**
     * DTO anidado para representar la información de CategoriaRatio.
     * Asume que la entidad CategoriaRatio tiene al menos un ID y un nombre.
     */
    @Data
    @NoArgsConstructor
    public static class CategoriaRatioDTO {
        private Integer id_categoria_ratio;
        private String nombre_categoria; // Ajusta el nombre del campo si es diferente
    }
}
