package com.anf.proyecto.backend.modules.usuario.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "acceso_usuario")
@Data
public class AccesoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acceso_id")
    private Integer accesoId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_opcion", nullable = false)
    private OpcionForm opcionForm;

    @Column(name = "empresa_id")
    private Integer empresaId; // Si quieres relacionarlo con la empresa
}
