package com.anf.proyecto.backend.modules.usuario.service;

import com.anf.proyecto.backend.modules.usuario.dto.LoginRequest;
import com.anf.proyecto.backend.modules.usuario.dto.LoginResponse;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import com.anf.proyecto.backend.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getUserName(), usuario.getNombreUsuario())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // En esta parte puedes generar un token JWT o simplemente devolver un mensaje
        return new LoginResponse("FAKE_TOKEN_123", usuario.getUserName());
    }
}