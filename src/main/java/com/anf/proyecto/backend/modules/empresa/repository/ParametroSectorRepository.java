package com.anf.proyecto.backend.modules.empresa.repository;
import com.anf.proyecto.backend.modules.empresa.entity.ParametroSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.anf.proyecto.backend.modules.empresa.entity.Sector;
import java.util.Optional;

@Repository
public interface ParametroSectorRepository extends JpaRepository<ParametroSector, Integer> {

    /**
     * Busca el primer parámetro de sector que coincida con un sector y un año de referencia.
     * Spring Data JPA implementará este método automáticamente.
     * @param sector El objeto Sector por el cual filtrar.
     * @param anioReferencia El año de referencia.
     * @return Un Optional que contiene el ParametroSector si se encuentra.
     */
    Optional<ParametroSector> findFirstBySectorAndAnioReferencia(Sector sector, Integer anioReferencia);

}