package com.anf.proyecto.backend.modules.estadofinanciero.repository;

import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LineaEstadoFinancieroRepository extends JpaRepository<LineaEstadoFinanciero, Long> {

    @Query("SELECT COALESCE(SUM(lef.saldo), 0.00) " +
            "FROM LineaEstadoFinanciero lef " +
            "JOIN lef.estadoFinanciero ef " +
            "JOIN lef.cuenta c " +
            "WHERE ef.empresa.empresaId = :empresaId " +
            "AND ef.anio = :anio " +
            "AND ef.tipoReporte = :tipoReporte " +
            "AND c.codigoCuenta IN :codigosCuenta")
    BigDecimal sumSaldosByCriteria(
            @Param("empresaId") Integer empresaId,
            // --- INICIO DE LA CORRECCIÓN ---
            @Param("anio") Integer anio, // Cambiado de int a Integer
            // --- FIN DE LA CORRECCIÓN ---
            @Param("tipoReporte") String tipoReporte,
            @Param("codigosCuenta") List<String> codigosCuenta
    );
}