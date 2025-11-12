package com.anf.proyecto.backend.modules.empresa.repository;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    // Métodos para validar duplicados (usando los nombres camelCase corregidos)
    boolean existsByNombreEmpresa(String nombreEmpresa);
    boolean existsByEmpresaNit(String nit);

    Optional<Empresa> findByNombreEmpresa(String nombreEmpresa);



}