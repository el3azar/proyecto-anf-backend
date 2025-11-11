package com.anf.proyecto.backend.modules.usuario.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String contrasena;
}
