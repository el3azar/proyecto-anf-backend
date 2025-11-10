package com.anf.proyecto.backend.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String userName;
    private Integer id;
    private List<Integer> accesos;
}