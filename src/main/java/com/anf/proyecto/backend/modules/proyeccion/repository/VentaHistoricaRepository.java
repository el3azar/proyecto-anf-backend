package com.anf.proyecto.backend.modules.proyeccion.repository;

import com.anf.proyecto.backend.modules.proyeccion.entity.VentaHistorica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaHistoricaRepository extends JpaRepository<VentaHistorica, Long> {

    @Query("SELECT COUNT(v) > 0 FROM VentaHistorica v WHERE FUNCTION('MONTH', v.fechaVenta) = :mes AND FUNCTION('YEAR', v.fechaVenta) = :anio AND v.empresa.empresaId = :empresaId")
    boolean existsByMesAndAnioAndEmpresa(@Param("mes") int mes, @Param("anio") int anio, @Param("empresaId") Integer empresaId);

    // CORREGIDO: Busca todas las ventas de una empresa, ordenadas por fecha
    List<VentaHistorica> findByEmpresa_EmpresaIdOrderByFechaVentaAsc(Integer empresaId);

    // NUEVO MÉTODO: Busca ventas de una empresa dentro de un rango de fechas
    List<VentaHistorica> findByEmpresa_EmpresaIdAndFechaVentaBetween(Integer empresaId, LocalDate startDate, LocalDate endDate);

    // NUEVO MÉTODO: Borra ventas de una empresa dentro de un rango de fechas
    void deleteByEmpresa_EmpresaIdAndFechaVentaBetween(Integer empresaId, LocalDate startDate, LocalDate endDate);
}