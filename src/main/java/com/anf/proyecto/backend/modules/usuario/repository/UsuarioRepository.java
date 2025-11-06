package com.anf.proyecto.backend.modules.usuario.repository;

import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByUserName(String userName);

    Optional<Usuario> findByUserName(String userName);
}