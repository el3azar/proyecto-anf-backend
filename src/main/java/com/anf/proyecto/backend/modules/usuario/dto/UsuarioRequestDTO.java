package com.anf.proyecto.backend.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nombreUsuario;
    private String apellidoUsuario;
    private String userName;
    private String contrasena;
    private String rol;
}