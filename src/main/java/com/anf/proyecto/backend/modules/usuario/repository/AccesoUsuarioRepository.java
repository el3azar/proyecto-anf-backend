package com.anf.proyecto.backend.modules.usuario.repository;

import com.anf.proyecto.backend.modules.usuario.entity.AccesoUsuario;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccesoUsuarioRepository extends JpaRepository<AccesoUsuario, Integer> {
    // Método para traer todos los accesos de un usuario
    List<AccesoUsuario> findByUsuario(Usuario usuario);
}
