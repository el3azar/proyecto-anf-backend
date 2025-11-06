package com.anf.proyecto.backend.modules.usuario.entity;

import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "apellido_usuario", nullable = false, length = 50)
    private String apellidoUsuario;

    @Column(name = "user_name", nullable = false, length = 50, unique = true)
    private String userName;

    @Column(nullable = false, length = 256)
    private String contrasena;

    @Column(nullable = false, length = 15)
    private String rol;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Empresa> empresasAnalizadas;
}