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
        // Buscar el usuario por nombre de usuario
        // Funciona de la siguiente manera:
        // Primero usamos usuarioRepository que es la interfaz que conecta con la base de datos.
        // Después filtra la petición hecha a la base de datos por medio de usuarioRepository,
        // buscándolo por el nombre de usuario traído con getUserName del request.
        // Esto es una clase DTO (Data Transfer Object), que solo tiene atributos y sus métodos get/set,
        // generados automáticamente con la librería Lombok.
        // Si no encuentra el usuario, orElseThrow lanza una excepción con el mensaje "Usuario no encontrado".
        Usuario usuario = usuarioRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar la contraseña
        // passwordEncoder es una interfaz de Spring Security que se utiliza para encriptar y desencriptar contraseñas
        // matches compara el password que viene en el request (texto plano) con el password encriptado del usuario encontrado.
        // Si no coinciden, lanza una excepción con el mensaje "Contraseña incorrecta".
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }


        // En esta parte puedes generar un token JWT o simplemente devolver un mensaje
        return new LoginResponse("FAKE_TOKEN_123", usuario.getUserName());
    }
}
