package com.anf.proyecto.backend.modules.analisis.service;

import com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO;
import com.anf.proyecto.backend.modules.analisis.dto.RatioDTO;
import com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.LineaEstadoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalisisService {

    @Autowired
    private LineaEstadoFinancieroRepository lineaRepo;

    public ReporteInternoDTO generarReporteInterno(Integer empresaId, int anio1, int anio2){
        // Traer todas las líneas de ambos años
     List<LineaEstadoFinanciero> lineasAnio1 = 
        lineaRepo.findByEstadoFinanciero_Empresa_EmpresaIdAndEstadoFinanciero_Anio(empresaId, anio1);

    List<LineaEstadoFinanciero> lineasAnio2 = 
        lineaRepo.findByEstadoFinanciero_Empresa_EmpresaIdAndEstadoFinanciero_Anio(empresaId, anio2);


     if (lineasAnio1.isEmpty() || lineasAnio2.isEmpty()) {
        return new ReporteInternoDTO(empresaId, anio1, anio2, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
    
        // Calcular totales (convierte BigDecimal a double con seguridad)
        double totalAnio1 = lineasAnio1.stream()
                .filter(l -> l.getSaldo() != null)
                .mapToDouble(l -> l.getSaldo().doubleValue())
                .sum();

        double totalAnio2 = lineasAnio2.stream()
                .filter(l -> l.getSaldo() != null)
                .mapToDouble(l -> l.getSaldo().doubleValue())
                .sum();

        // Convertir a mapa para comparación rápida
        // Usamos la clave como String para evitar problemas si la cuenta es Integer.
        Map<String, Double> mapaAnio1 = lineasAnio1.stream()
                .filter(l -> l.getSaldo() != null)
                .collect(Collectors.toMap(
                        l -> String.valueOf(getCodigoCuentaSafe(l)),
                        l -> l.getSaldo().doubleValue(),
                        (v1, v2) -> v1 // merge: si hay duplicados conserva el primero
                ));

        Map<String, Double> mapaAnio2 = lineasAnio2.stream()
                .filter(l -> l.getSaldo() != null)
                .collect(Collectors.toMap(
                        l -> String.valueOf(getCodigoCuentaSafe(l)),
                        l -> l.getSaldo().doubleValue(),
                        (v1, v2) -> v1
                ));

        // Calcular análisis por línea
        List<LineaAnalisisDTO> analisis = new ArrayList<>();
        Set<String> codigos = new HashSet<>();
        codigos.addAll(mapaAnio1.keySet());
        codigos.addAll(mapaAnio2.keySet());

        for (String codigo : codigos) {
            double saldo1 = mapaAnio1.getOrDefault(codigo, 0.0);
            double saldo2 = mapaAnio2.getOrDefault(codigo, 0.0);
            double variacionAbs = saldo2 - saldo1;
            double variacionRel = (saldo1 != 0) ? (variacionAbs / saldo1) * 100.0 : 0.0;

            double porcentajeV1 = (totalAnio1 != 0) ? (saldo1 / totalAnio1) * 100.0 : 0.0;
            double porcentajeV2 = (totalAnio2 != 0) ? (saldo2 / totalAnio2) * 100.0 : 0.0;

            analisis.add(new LineaAnalisisDTO(
                    codigo,
                    "Cuenta " + codigo,
                    saldo1,
                    saldo2,
                    variacionAbs,
                    variacionRel,
                    porcentajeV1,
                    porcentajeV2
            ));
        }

        // Ratios ejemplo (ajústalos según tus reglas)
        List<RatioDTO> ratios = Arrays.asList(
                new RatioDTO("Liquidez Corriente", 1.5, 1.7),
                new RatioDTO("Endeudamiento", 0.6, 0.55)
        );

        return new ReporteInternoDTO(empresaId, anio1, anio2, analisis, analisis, ratios);
    }

    /**
     * Obtiene un "código de cuenta" seguro desde la entidad LineaEstadoFinanciero.
     * Ajusta este método si tu entidad tiene otra estructura (por ejemplo getCuenta().getCodigoGenerado()).
     */
    private Object getCodigoCuentaSafe(LineaEstadoFinanciero l) {
        try {
            // Intentamos varios getters comunes. Cambia o añade según la estructura real de tu entidad.
            // 1) Si existe getCodigoCuenta()
            // 2) Si existe getCuentaId()
            // 3) Si existe getCuenta() -> entidad Cuenta -> getCodigoGenerado() o getCodigoIndividual()
            // Retornamos el primer valor disponible.
            // (Si tu entidad usa nombres distintos, reemplaza aquí)
            // -- Aquí hacemos comprobaciones por reflexión segura:
            if (hasMethod(l, "getCodigoCuenta")) {
                Object v = invokeGetter(l, "getCodigoCuenta");
                if (v != null) return v;
            }
            if (hasMethod(l, "getCuentaId")) {
                Object v = invokeGetter(l, "getCuentaId");
                if (v != null) return v;
            }
            if (hasMethod(l, "getCuenta")) {
                Object cuenta = invokeGetter(l, "getCuenta");
                if (cuenta != null) {
                    // prueba getters comunes en Cuenta
                    if (hasMethod(cuenta, "getCodigoGenerado")) {
                        Object v = invokeGetter(cuenta, "getCodigoGenerado");
                        if (v != null) return v;
                    }
                    if (hasMethod(cuenta, "getCodigoIndividual")) {
                        Object v = invokeGetter(cuenta, "getCodigoIndividual");
                        if (v != null) return v;
                    }
                    if (hasMethod(cuenta, "getId")) {
                        Object v = invokeGetter(cuenta, "getId");
                        if (v != null) return v;
                    }
                }
            }
        } catch (Exception ex) {
            // si algo falla, devolvemos el id de la línea como fallback
        }
        // fallback al id de la línea si nada más
        return Optional.ofNullable(l)
        .map(LineaEstadoFinanciero::getId)
        .map(Object::toString)
        .orElse("unknown");

    }

    // Métodos utilitarios simples (reflexión ligera)
    private boolean hasMethod(Object obj, String methodName) {
        try {
            obj.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Object invokeGetter(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
