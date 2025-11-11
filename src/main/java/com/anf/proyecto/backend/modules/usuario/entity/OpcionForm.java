package com.anf.proyecto.backend.modules.usuario.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "opcion_form")
@Data
public class OpcionForm {

    @Id
    @Column(name = "id_opcion", length = 10)
    private String idOpcion;

    @Column(name = "des_opcion", nullable = false, length = 100)
    private String desOpcion;

    @Column(name = "num_form", nullable = false)
    private int numForm;
}
