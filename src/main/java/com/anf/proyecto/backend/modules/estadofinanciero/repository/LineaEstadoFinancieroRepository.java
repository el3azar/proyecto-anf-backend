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


    /**
     * Suma los saldos de una lista de códigos de cuenta para una empresa, año y tipo de reporte específicos.
     * La consulta navega desde LineaEstadoFinanciero a través de EstadoFinanciero y Cuenta para filtrar
     * por todos los criterios necesarios de forma eficiente.
     *
     * @param empresaId     El ID de la empresa.
     * @param anio          El año del reporte financiero.
     * @param tipoReporte   El tipo de reporte (ej. "BALANCE_GENERAL").
     * @param codigosCuenta La lista de 'cue_cuenta_id' a sumar.
     * @return La suma de los saldos en un BigDecimal. Devuelve BigDecimal.ZERO si no se encuentran coincidencias.
     */
    @Query("SELECT COALESCE(SUM(lef.saldo), 0.00) " +
            "FROM LineaEstadoFinanciero lef " +
            "JOIN lef.estadoFinanciero ef " +
            "JOIN lef.cuenta c " +
            "WHERE ef.empresa.id = :empresaId " +
            "AND ef.anio = :anio " +
            "AND ef.tipoReporte = :tipoReporte " +
            "AND c.codigoCuenta IN :codigosCuenta")
    BigDecimal sumSaldosByCriteria(
            @Param("empresaId") Integer empresaId,
            @Param("anio") int anio,
            @Param("tipoReporte") String tipoReporte,
            @Param("codigosCuenta") List<String> codigosCuenta
    );
}