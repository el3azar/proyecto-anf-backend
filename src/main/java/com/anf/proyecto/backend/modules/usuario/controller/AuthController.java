package com.anf.proyecto.backend.modules.usuario.controller;

import com.anf.proyecto.backend.modules.usuario.dto.LoginRequest;
import com.anf.proyecto.backend.modules.usuario.dto.LoginResponse;
import com.anf.proyecto.backend.modules.usuario.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}