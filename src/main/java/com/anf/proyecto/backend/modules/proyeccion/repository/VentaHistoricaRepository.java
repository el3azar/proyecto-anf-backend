package com.anf.proyecto.backend.modules.proyeccion.repository;

import com.anf.proyecto.backend.modules.proyeccion.entity.VentaHistorica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaHistoricaRepository extends JpaRepository<VentaHistorica, Long> {

    // CORREGIDO: Busca todas las ventas de una empresa, ordenadas por fecha
    List<VentaHistorica> findByEmpresa_EmpresaIdOrderByFechaVentaAsc(Integer empresaId);

    // NUEVO MÉTODO: Busca ventas de una empresa dentro de un rango de fechas
    List<VentaHistorica> findByEmpresa_EmpresaIdAndFechaVentaBetween(Integer empresaId, LocalDate startDate, LocalDate endDate);

    // NUEVO MÉTODO: Borra ventas de una empresa dentro de un rango de fechas
    void deleteByEmpresa_EmpresaIdAndFechaVentaBetween(Integer empresaId, LocalDate startDate, LocalDate endDate);
}