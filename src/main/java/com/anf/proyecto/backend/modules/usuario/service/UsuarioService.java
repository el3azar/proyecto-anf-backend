package com.anf.proyecto.backend.modules.usuario.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.usuario.dto.UsuarioRequestDTO;
import com.anf.proyecto.backend.modules.usuario.dto.UsuarioResponseDTO;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import com.anf.proyecto.backend.modules.usuario.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

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

        Usuario usuario = modelMapper.map(requestDTO, Usuario.class);
        // Aquí iría la lógica para encriptar la contraseña si se manejara
        // usuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));

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