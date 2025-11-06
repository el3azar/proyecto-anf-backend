package com.anf.proyecto.backend.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Integer usuarioId;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String userName;
    private String rol;
}