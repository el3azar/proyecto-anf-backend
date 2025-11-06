package com.anf.proyecto.backend.modules.estadofinanciero.repository;

import com.anf.proyecto.backend.modules.estadofinanciero.entity.EstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoFinancieroRepository extends JpaRepository<EstadoFinanciero, Long> {
    // Podríamos añadir búsquedas personalizadas en el futuro si es necesario
    // Optional<EstadoFinanciero> findByEmpresaIdAndAnioAndTipoReporte(Integer empresaId, int anio, String tipoReporte);
}