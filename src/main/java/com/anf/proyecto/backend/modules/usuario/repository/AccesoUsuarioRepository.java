package com.anf.proyecto.backend.modules.usuario.repository;

import com.anf.proyecto.backend.modules.usuario.entity.AccesoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccesoUsuarioRepository extends JpaRepository<AccesoUsuario, Integer> {
    // Opcional: métodos personalizados si los necesitas
}
