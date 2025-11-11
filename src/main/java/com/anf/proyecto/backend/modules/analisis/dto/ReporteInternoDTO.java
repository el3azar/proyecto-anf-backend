package com.anf.proyecto.backend.modules.analisis.dto;

import java.util.List;

public class ReporteInternoDTO {
    private Integer empresaId;
    private int anio1;
    private int anio2;
    private List<LineaAnalisisDTO> analisisHorizontal;
    private List<LineaAnalisisDTO> analisisVertical;
    private List<RatioDTO> ratios;

    // 🔹 Constructor completo
    public ReporteInternoDTO(Integer empresaId, int anio1, int anio2,
                             List<LineaAnalisisDTO> analisisHorizontal,
                             List<LineaAnalisisDTO> analisisVertical,
                             List<RatioDTO> ratios) {
        this.empresaId = empresaId;
        this.anio1 = anio1;
        this.anio2 = anio2;
        this.analisisHorizontal = analisisHorizontal;
        this.analisisVertical = analisisVertical;
        this.ratios = ratios;
    }

    // 🔹 Constructor vacío
    public ReporteInternoDTO() {}

    // 🔹 Getters y Setters
    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }

    public int getAnio1() {
        return anio1;
    }

    public void setAnio1(int anio1) {
        this.anio1 = anio1;
    }

    public int getAnio2() {
        return anio2;
    }

    public void setAnio2(int anio2) {
        this.anio2 = anio2;
    }

    public List<LineaAnalisisDTO> getAnalisisHorizontal() {
        return analisisHorizontal;
    }

    public void setAnalisisHorizontal(List<LineaAnalisisDTO> analisisHorizontal) {
        this.analisisHorizontal = analisisHorizontal;
    }

    public List<LineaAnalisisDTO> getAnalisisVertical() {
        return analisisVertical;
    }

    public void setAnalisisVertical(List<LineaAnalisisDTO> analisisVertical) {
        this.analisisVertical = analisisVertical;
    }

    public List<RatioDTO> getRatios() {
        return ratios;
    }

    public void setRatios(List<RatioDTO> ratios) {
        this.ratios = ratios;
    }
}
