package com.anf.proyecto.backend.modules.usuario.repository;

import com.anf.proyecto.backend.modules.usuario.entity.OpcionForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpcionFormRepository extends JpaRepository<OpcionForm, String> {
    // Opcional: métodos personalizados si los necesitas
}
