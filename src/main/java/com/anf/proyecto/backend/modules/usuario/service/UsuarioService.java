package com.anf.proyecto.backend.modules.usuario.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.usuario.dto.UsuarioRequestDTO;
import com.anf.proyecto.backend.modules.usuario.dto.UsuarioResponseDTO;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import com.anf.proyecto.backend.modules.usuario.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Agregamos el PasswordEncoder (inyectado desde SecurityConfig)
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO getUsuarioById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.existsByUserName(requestDTO.getUserName())) {
            throw new BusinessRuleException("El nombre de usuario '" + requestDTO.getUserName() + "' ya está en uso.");
        }

        // Mapeamos el DTO a la entidad sirve para no tener que setear campo por campo
        // y asi evitar errores humanos
        //sin mapearlo asi, seria:
        // Usuario usuario = new Usuario();
        // usuario.setNombreUsuario(requestDTO.getNombreUsuario());
        // usuario.setApellidoUsuario(requestDTO.getApellidoUsuario());
        // usuario.setUserName(requestDTO.getUserName());
        // usuario.setContrasena(requestDTO.getContrasena());
        // usuario.setRol(requestDTO.getRol());
        // asi con modelMapper:
        Usuario usuario = modelMapper.map(requestDTO, Usuario.class);

        String encodedPassword = passwordEncoder.encode(requestDTO.getContrasena());
        System.out.println("Contraseña original: " + requestDTO.getContrasena());
        System.out.println("Contraseña encriptada: " + encodedPassword);
        usuario.setContrasena(encodedPassword);



        Usuario savedUsuario = usuarioRepository.save(usuario);
        return modelMapper.map(savedUsuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO updateUsuario(Integer id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        usuarioRepository.findByUserName(requestDTO.getUserName()).ifPresent(u -> {
            if (!u.getUsuarioId().equals(id)) {
                throw new BusinessRuleException("El nombre de usuario '" + requestDTO.getUserName() + "' ya está en uso por otro usuario.");
            }
        });

        usuario.setNombreUsuario(requestDTO.getNombreUsuario());
        usuario.setApellidoUsuario(requestDTO.getApellidoUsuario());
        usuario.setUserName(requestDTO.getUserName());
        usuario.setRol(requestDTO.getRol());

        // Si el usuario envía una nueva contraseña, también la encriptamos
        if (requestDTO.getContrasena() != null && !requestDTO.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return modelMapper.map(updatedUsuario, UsuarioResponseDTO.class);
    }

    public void deleteUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
