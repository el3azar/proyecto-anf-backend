package com.anf.proyecto.backend.modules.analisis.service;

import com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO;
import com.anf.proyecto.backend.modules.analisis.dto.RatioDTO;
import com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO;
import com.anf.proyecto.backend.modules.analisis.entity.Ratio;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.analisis.repository.RatioRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;

import org.springframework.transaction.annotation.Transactional;

import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;

import com.anf.proyecto.backend.modules.estadofinanciero.repository.EstadoFinancieroRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.LineaEstadoFinancieroRepository;
import com.anf.proyecto.backend.modules.analisis.repository.RatioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class AnalisisService {

    private final EmpresaRepository empresaRepository;
        private final RatioRepository ratioRepository;
        private final EstadoFinancieroRepository estadoFinancieroRepository;
        private final LineaEstadoFinancieroRepository lineaEstadoFinancieroRepository;

        public AnalisisService(
                EmpresaRepository empresaRepository,
                RatioRepository ratioRepository,
                EstadoFinancieroRepository estadoFinancieroRepository,
                LineaEstadoFinancieroRepository lineaEstadoFinancieroRepository
        ) {
            this.empresaRepository = empresaRepository;
            this.ratioRepository = ratioRepository;
            this.estadoFinancieroRepository = estadoFinancieroRepository;
            this.lineaEstadoFinancieroRepository = lineaEstadoFinancieroRepository;
        }


    private static class LineaData {
        private final Double saldo;
        private final String nombre;
        LineaData(Double saldo, String nombre){ this.saldo = saldo; this.nombre = nombre; }
        public Double getSaldo(){ return saldo; }
        public String getNombre(){ return nombre; }
    }

    @Autowired
    private LineaEstadoFinancieroRepository lineaRepo;

    @Autowired
    private EstadoFinancieroRepository estadoFinancieroRepo;
    
    


    public ReporteInternoDTO generarReporteInterno(Integer empresaId, int anio1, int anio2) {
        // ... (Tu código para HU-001 se queda exactamente igual) ...
        // ... (todo el método generarReporteInterno va aquí) ...
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

        Map<String, LineaData> mapaAnio1 = lineasAnio1.stream()
                .filter(l -> l.getSaldo() != null)
                .collect(Collectors.toMap(
                        l -> String.valueOf(getCodigoCuentaSafe(l)),
                        l -> new LineaData( // <-- Se crea el objeto LineaData
                                l.getSaldo().doubleValue(),
                                getNombreCuentaSafe(l) // <-- Se obtiene el nombre
                        ),
                        (v1, v2) -> v1 // merge: si hay duplicados conserva el primero
                ));

        Map<String, LineaData> mapaAnio2 = lineasAnio2.stream()
                .filter(l -> l.getSaldo() != null)
                .collect(Collectors.toMap(
                        l -> String.valueOf(getCodigoCuentaSafe(l)),
                        l -> new LineaData( // <-- Se crea el objeto LineaData
                                l.getSaldo().doubleValue(),
                                getNombreCuentaSafe(l) // <-- Se obtiene el nombre
                        ),
                        (v1, v2) -> v1
                ));

        List<LineaAnalisisDTO> analisis = new ArrayList<>();
        Set<String> codigos = new HashSet<>();
        codigos.addAll(mapaAnio1.keySet());
        codigos.addAll(mapaAnio2.keySet());

        String nombreDefault = "Cuenta no encontrada";

        for (String codigo : codigos) {
            LineaData data1 = mapaAnio1.get(codigo);
            LineaData data2 = mapaAnio2.get(codigo);
            double saldo1 = (data1 != null) ? data1.saldo : 0.0;
            double saldo2 = (data2 != null) ? data2.saldo : 0.0;
            String nombre = (data2 != null) ? data2.nombre :
                    (data1 != null) ? data1.nombre :
                            nombreDefault;

            double variacionAbs = saldo2 - saldo1;
            double variacionRel = (saldo1 != 0) ? (variacionAbs / saldo1) * 100.0 : 0.0;
            double porcentajeV1 = (totalAnio1 != 0) ? (saldo1 / totalAnio1) * 100.0 : 0.0;
            double porcentajeV2 = (totalAnio2 != 0) ? (saldo2 / totalAnio2) * 100.0 : 0.0;

            analisis.add(new LineaAnalisisDTO(
                    codigo,
                    nombre, // <--- ¡SOLUCIONADO!
                    saldo1,
                    saldo2,
                    variacionAbs,
                    variacionRel,
                    porcentajeV1,
                    porcentajeV2
            ));
        }

        List<RatioDTO> ratios = Arrays.asList(
                new RatioDTO("Liquidez Corriente", 1.5, 1.7),
                new RatioDTO("Endeudamiento", 0.6, 0.55)
        );

        return new ReporteInternoDTO(empresaId, anio1, anio2, analisis, analisis, ratios);
    }


    @Transactional(readOnly = true) 
    public List<Map<String, Object>> calcularEvolucionRatios(Integer empresaId, List<Integer> ratioIds) {

        // 2. Llamar al nuevo método del repositorio
        List<RatioRepository.EvolucionRatioData> rawData = 
                ratioRepository.findEvolucionRatiosByIds(empresaId, ratioIds);

        // 3. "Pivotar" los datos para el formato de Recharts
        Map<Integer, Map<String, Object>> pivotMap = new TreeMap<>(); // TreeMap ordena por año

        for (RatioRepository.EvolucionRatioData dto : rawData) {
            
            Map<String, Object> anioData = pivotMap.computeIfAbsent(
                    dto.getAnio(), 
                    anio -> {
                        Map<String, Object> newMap = new HashMap<>();
                        newMap.put("anio", anio); 
                        return newMap;
                    }
            );

            // 4. Mapear los datos (Instrucción #3)
            // Convierte el ID (ej. 5) a un String (ej. "5")
            String idRatioComoString = String.valueOf(dto.getIdRatio());

            // Crea el JSON: {"anio": 2023, "5": 1.45, "5_sector": 1.2, ...}
            anioData.put(idRatioComoString, dto.getValorCalculado());
            anioData.put(idRatioComoString + "_sector", dto.getValorSectorPromedio());
            anioData.put(idRatioComoString + "_cumple_sector", dto.getCumpleSector());
        }

        // 5. Devolver la lista
        return new ArrayList<>(pivotMap.values());
    }


    private Map<String, Double> calcularRatiosDesdeMapa(Map<String, LineaData> cuentas, List<String> ratiosSolicitados) {
        Map<String, Double> salida = new HashMap<>();
        for (String nombre : ratiosSolicitados) {
            switch (nombre) {
                case "Liquidez Corriente":
                    Double activos = getSaldoByCuentaCodigoOrNull(cuentas, "1401"); // ajustar códigos según catálogo
                    Double pasivos = getSaldoByCuentaCodigoOrNull(cuentas, "2401");
                    salida.put(nombre, safeDiv(activos, pasivos));
                    break;
                case "Prueba Ácida":
                    // prueba ácida = (Activos Corrientes - Inventarios) / Pasivo Corriente
                    Double activosCorr = getSaldoByCuentaCodigoOrNull(cuentas, "1401");
                    Double inventarios = getSaldoByCuentaCodigoOrNull(cuentas, "1404");
                    Double pasivoCorr = getSaldoByCuentaCodigoOrNull(cuentas, "2401");
                    salida.put(nombre, safeDiv( activosCorr == null ? null : activosCorr - (inventarios==null?0:inventarios), pasivoCorr));
                    break;
                case "Rotación Cuentas por Cobrar":
                    Double ventas = getSaldoByCuentaCodigoOrNull(cuentas, "510201");
                    Double cxc = getSaldoByCuentaCodigoOrNull(cuentas, "1405");
                    salida.put(nombre, safeDiv(ventas, cxc));
                    break;
                case "Período Medio de Cobranza":
                    Double rotacion = null;
                    Double ventasDen = getSaldoByCuentaCodigoOrNull(cuentas, "510201");
                    Double cxcProm = getSaldoByCuentaCodigoOrNull(cuentas, "1405"); // si sólo un año, aproximamos
                    if (ventasDen != null && cxcProm != null && cxcProm != 0.0) {
                        rotacion = (cxcProm * 365.0) / ventasDen;
                    }
                    salida.put(nombre, rotacion);
                    break;
                case "Rotación Activos Totales":
                    Double ventasN = getSaldoByCuentaCodigoOrNull(cuentas, "510201");
                    Double activosTot = getSaldoByCuentaCodigoOrNull(cuentas, "1401"); // aproximación
                    salida.put(nombre, safeDiv(ventasN, activosTot));
                    break;
                default:
                    salida.put(nombre, null); // no implementado, dejar null
            }
        }
        return salida;
    }

    private Double getSaldoByCuentaCodigoOrNull(Map<String, LineaData> cuentas, String codigo) {
        LineaData ld = cuentas.get(codigo);
        return ld == null ? null : ld.getSaldo();
    }
    private Double safeDiv(Double a, Double b) {
        if (a == null || b == null || b == 0.0) return null;
        return a / b;
    }



    // =================================================================
    // V V V MÉTODOS HELPER DE REFLEXIÓN  V V V
    // =================================================================

    /**
     * Obtiene un "código de cuenta" seguro...
     */
    private Object getCodigoCuentaSafe(LineaEstadoFinanciero l) {
        // ... (Tu código de reflexión se queda igual) ...
        try {
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
                    if (hasMethod(cuenta, "getCodigoGenerado")) {
                        Object v = invokeGetter(cuenta, "getCodigoGenerado");
                        if (v != null) return v;
                    }
                    if (hasMethod(cuenta, "getCodigoIndividual")) {
                        Object v = invokeGetter(cuenta, "getCodigoIndividual");
                        if (v != null) return v;
                    }
                    if (hasMethod(cuenta, "getId")) {
                        Object v = invokeGetter(l, "getId");
                        if (v != null) return v;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return Optional.ofNullable(l)
                .map(LineaEstadoFinanciero::getId)
                .map(Object::toString)
                .orElse("unknown");
    }

    /**
     * Obtiene un "nombre de cuenta" seguro...
     */
    private String getNombreCuentaSafe(LineaEstadoFinanciero l) {
        // ... (Tu código de reflexión se queda igual) ...
        try {
            if (hasMethod(l, "getCuenta")) {
                Object cuenta = invokeGetter(l, "getCuenta");
                if (cuenta != null) {
                    if (hasMethod(cuenta, "getNombreCuenta")) {
                        Object v = invokeGetter(cuenta, "getNombreCuenta");
                        if (v != null) return v.toString();
                    }
                    if (hasMethod(cuenta, "getNombre")) {
                        Object v = invokeGetter(cuenta, "getNombre");
                        if (v != null) return v.toString();
                    }
                    if (hasMethod(cuenta, "getDescripcion")) {
                        Object v = invokeGetter(cuenta, "getDescripcion");
                        if (v != null) return v.toString();
                    }
                }
            }
            if (hasMethod(l, "getNombreCuenta")) {
                Object v = invokeGetter(l, "getNombreCuenta");
                if (v != null) return v.toString();
            }

        } catch (Exception ex) {
        }
        return "Cuenta " + String.valueOf(getCodigoCuentaSafe(l));
    }

    // --- Métodos utilitarios (se quedan igual) ---
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