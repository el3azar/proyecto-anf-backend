package com.anf.proyecto.backend.modules.analisis.repository;

import com.anf.proyecto.backend.modules.analisis.entity.Ratio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RatioRepository extends JpaRepository<Ratio, Integer> {

public interface EvolucionRatioData {
        Integer getAnio();
        Integer getIdRatio(); 
        Double getValorCalculado();
        Double getValorSectorPromedio();
        Boolean getCumpleSector();
    }

@Query("SELECT " +
           "r.anio_ratio as anio, " +
           "tr.idTipoRatio as idRatio, " + // <-- CAMBIO
           "r.valor_calculado as valorCalculado, " +
           "r.valor_sector_promedio as valorSectorPromedio, " +
           "r.cumple_sector as cumpleSector " +
           "FROM Ratio r JOIN r.tipoRatio tr " +
           "WHERE r.empresa.empresaId = :empresaId " +
           "AND tr.idTipoRatio IN :ratioIds " + // <-- CAMBIO
           "ORDER BY r.anio_ratio ASC")
    List<EvolucionRatioData> findEvolucionRatiosByIds( // <-- Renombramos el método
            @Param("empresaId") Integer empresaId,
            @Param("ratioIds") List<Integer> ratioIds // <-- CAMBIO
    );

    // Buscar ratios de una empresa por año y nombre de categoría (nombreTipo)
    @Query("SELECT r FROM Ratio r WHERE r.empresa.empresaId = :empresaId AND r.anio_ratio = :anio AND r.categoriaRatio.nombreTipo = :categoriaNombre")
    List<Ratio> findByEmpresaIdAndAnioAndCategoriaNombre(@Param("empresaId") Integer empresaId,
                                                         @Param("anio") Integer anio,
                                                         @Param("categoriaNombre") String categoriaNombre);

    // Promedio (AVG) del valor_calculado por sector (sector id), categoría (nombreTipo) y año
    @Query("SELECT AVG(r.valor_calculado) FROM Ratio r WHERE r.anio_ratio = :anio AND r.categoriaRatio.nombreTipo = :categoriaNombre AND r.empresa.sector.idSector = :sectorId")
    BigDecimal findAverageValorBySectorAndCategoriaAndAnio(@Param("sectorId") Integer sectorId,
                                                           @Param("categoriaNombre") String categoriaNombre,
                                                           @Param("anio") Integer anio);

    // Promedio general de todas las empresas (sin filtrar por sector) para una categoría y año
    @Query("SELECT AVG(r.valor_calculado) FROM Ratio r WHERE r.anio_ratio = :anio AND r.categoriaRatio.nombreTipo = :categoriaNombre")
    BigDecimal findAverageValorByCategoriaAndAnio(@Param("categoriaNombre") String categoriaNombre,
                                                  @Param("anio") Integer anio);


    List<Ratio> findByEmpresa_NombreEmpresaIgnoreCase(String nombreEmpresa);

}