package com.anf.proyecto.backend.modules.empresa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "sector")
@Data
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sector")
    private Integer idSector;

    @Column(name = "nombre_sector", nullable = false, length = 100)
    private String nombreSector;

    @Column(length = 256)
    private String descripcion;

    @Column(name = "pais_referencia", length = 100)
    private String paisReferencia;

    @Column(name = "fuente_datos", length = 256)
    private String fuenteDatos;

    @OneToMany(mappedBy = "sector", fetch = FetchType.LAZY)
    private List<Empresa> empresas;

    @OneToMany(mappedBy = "sector", fetch = FetchType.LAZY)
    private List<ParametroSector> parametros;
}