package com.anf.proyecto.backend.modules.empresa.repository;
import com.anf.proyecto.backend.modules.empresa.entity.ParametroSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametroSectorRepository extends JpaRepository<ParametroSector, Integer> {}