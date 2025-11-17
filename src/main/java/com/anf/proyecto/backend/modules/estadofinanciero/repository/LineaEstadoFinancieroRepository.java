package com.anf.proyecto.backend.modules.estadofinanciero.repository;

import com.anf.proyecto.backend.modules.catalogo.dto.SaldoCuentaAnioDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigDecimal;

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

    @Query("SELECT DISTINCT l.estadoFinanciero.anio FROM LineaEstadoFinanciero l WHERE l.estadoFinanciero.empresa.id = :empresaId ORDER BY l.estadoFinanciero.anio DESC")
    List<Integer> findDistinctAniosByEmpresaId(@Param("empresaId") Long empresaId);

    List<LineaEstadoFinanciero> findByEstadoFinanciero_Empresa_EmpresaIdAndEstadoFinanciero_Anio(Integer empresaId, int anio);

    /**
     * Busca los saldos anuales de una cuenta específica para una empresa específica,
     * utilizando sus nombres para la búsqueda.
     *
     * @param nombreEmpresa El nombre exacto de la empresa.
     * @param nombreCuenta  El nombre exacto de la cuenta a buscar.
     * @return Una lista de DTOs con el saldo y el año para cada estado financiero encontrado.
     */
    @Query("SELECT new com.anf.proyecto.backend.modules.catalogo.dto.SaldoCuentaAnioDTO(lef.saldo, ef.anio) " +
            "FROM LineaEstadoFinanciero lef " +
            "JOIN lef.estadoFinanciero ef " +
            "JOIN lef.cuenta c " +
            "WHERE LOWER(ef.empresa.nombreEmpresa) = LOWER(:nombreEmpresa) " +
            "AND LOWER(c.nombreCuenta) = LOWER(:nombreCuenta) " +
            "ORDER BY ef.anio DESC")
    List<SaldoCuentaAnioDTO> findSaldosByNombreEmpresaAndNombreCuenta(
            @Param("nombreEmpresa") String nombreEmpresa,
            @Param("nombreCuenta") String nombreCuenta
    );

}


