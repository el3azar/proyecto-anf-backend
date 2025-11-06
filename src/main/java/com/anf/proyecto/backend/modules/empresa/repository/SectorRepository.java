package com.anf.proyecto.backend.modules.empresa.repository;
import com.anf.proyecto.backend.modules.empresa.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Integer> {
    // Método para la validación de duplicados al crear
    boolean existsByNombreSector(String nombreSector);

    // Método para la validación de duplicados al actualizar
    Optional<Sector> findByNombreSector(String nombreSector);
}