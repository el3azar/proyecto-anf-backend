package com.anf.proyecto.backend.modules.analisis.repository;

import com.anf.proyecto.backend.modules.analisis.entity.Ratio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatioRepository extends JpaRepository<Ratio, Integer> {}