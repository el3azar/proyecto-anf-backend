package com.anf.proyecto.backend.modules.estadofinanciero.repository;

import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaEstadoFinancieroRepository extends JpaRepository<LineaEstadoFinanciero, Long> {
}