package com.anf.proyecto.backend.modules.empresa.entity;

import com.anf.proyecto.backend.modules.catalogo.entity.Catalogo;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "empresa")
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empresa_id")
    private Integer empresaId;

    @Column(name = "nombre_empresa", nullable = false, length = 50)
    private String nombreEmpresa;

    @Column(name = "empresa_dui", length = 10)
    private String empresaDui;

    @Column(name = "empresa_nit", length = 17)
    private String empresaNit;

    @Column(name = "empresa_nrc", length = 14)
    private String empresaNrc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sector")
    private Sector sector;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Catalogo> catalogo;
}