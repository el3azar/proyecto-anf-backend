package com.anf.proyecto.backend.modules.estadofinanciero.repository;

import com.anf.proyecto.backend.modules.estadofinanciero.entity.EstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoFinancieroRepository extends JpaRepository<EstadoFinanciero, Integer> {

    @Query("SELECT DISTINCT ef.anio FROM EstadoFinanciero ef WHERE ef.empresa.empresaId = :empresaId ORDER BY ef.anio ASC")
    List<Integer> findDistinctAniosByEmpresaIdOrderByAnio(@Param("empresaId") Integer empresaId);

}
