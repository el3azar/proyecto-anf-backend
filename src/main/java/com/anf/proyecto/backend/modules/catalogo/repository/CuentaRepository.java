package com.anf.proyecto.backend.modules.catalogo.repository;
import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    Optional<Cuenta> findByCodigoCuenta(String codigoCuenta);
}