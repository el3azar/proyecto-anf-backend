package com.anf.proyecto.backend.modules.analisis.service;

import com.anf.proyecto.backend.modules.analisis.dto.LineaAnalisisDTO;
import com.anf.proyecto.backend.modules.analisis.dto.RatioDTO;
import com.anf.proyecto.backend.modules.analisis.dto.ReporteInternoDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
// --- [NUEVO] Importa el repositorio de EstadoFinanciero ---
import com.anf.proyecto.backend.modules.estadofinanciero.repository.EstadoFinancieroRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.LineaEstadoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalisisService {

    // --- Clase interna (se queda igual) ---
    private static class LineaData {
        double saldo;
        String nombre;

        LineaData(double saldo, String nombre) {
            this.saldo = saldo;
            this.nombre = nombre;
        }
    }

    @Autowired
    private LineaEstadoFinancieroRepository lineaRepo;

    // --- [NUEVO] Inyecta el repositorio para buscar Años ---
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


    public List<Map<String, Object>> calcularEvolucionRatios(Integer empresaId, List<String> ratios) {

        // 1. ENCONTRAR AÑOS
        // Llama a `estadoFinancieroRepo` para encontrar todos los años para esa empresa
        // (Debes crear este método en tu EstadoFinancieroRepository)
        // Ejemplo: List<Integer> anios = estadoFinancieroRepo.findDistinctAniosByEmpresa_EmpresaIdOrderByAnio(empresaId);
        
        // --- Simulación (reemplaza con tu consulta real) ---
        List<Integer> anios;
        if (empresaId == 1) { // Mapfre
             anios = List.of(2021, 2022); // Solo tienes 2 años por ahora
        } else { // Otra empresa
             anios = List.of(2022, 2023); // Solo tienes 2 años por ahora
        }
        // Cuando subas el 3er año, la consulta a la BD los encontrará todos.


        // 2. PREPARAR LISTA DE RESULTADOS
        List<Map<String, Object>> listaParaRecharts = new ArrayList<>();

        // 3. LOOP POR CADA AÑO Y CALCULAR RATIOS
        for (Integer anio : anios) {
            
            // 3.1. Traer todas las líneas de este año (¡REUTILIZANDO TU LÓGICA!)
            List<LineaEstadoFinanciero> lineasAnio =
                    lineaRepo.findByEstadoFinanciero_Empresa_EmpresaIdAndEstadoFinanciero_Anio(empresaId, anio);
            
            if(lineasAnio.isEmpty()) {
                continue; // Salta este año si no hay datos
            }

            // 3.2. Convertir a mapa (¡REUTILIZANDO TU LÓGICA!)
            Map<String, LineaData> mapaAnio = lineasAnio.stream()
                    .filter(l -> l.getSaldo() != null)
                    .collect(Collectors.toMap(
                            l -> String.valueOf(getCodigoCuentaSafe(l)),
                            l -> new LineaData(
                                    l.getSaldo().doubleValue(),
                                    getNombreCuentaSafe(l)
                            ),
                            (v1, v2) -> v1
                    ));

            // 3.3. Preparar el mapa para este año (formato Recharts)
            Map<String, Object> mapaAnioParaRecharts = new HashMap<>();
            mapaAnioParaRecharts.put("anio", String.valueOf(anio));

            // 3.4. Calcular los ratios solicitados para este año
            // (Esta lógica debe ser movida a un método privado)
            Map<String, Double> ratiosCalculados = calcularRatiosDesdeMapa(mapaAnio, ratios);

            // 3.5. Agregar los ratios calculados al mapa del año
            mapaAnioParaRecharts.putAll(ratiosCalculados);
            
            // 3.6. Agregar el mapa del año a la lista final
            listaParaRecharts.add(mapaAnioParaRecharts);
        }

        // 4. DEVOLVER LA LISTA FINAL
        return listaParaRecharts;
    }

    /**
     * [NUEVO] Método Helper privado para calcular ratios desde el mapa de cuentas.
     */
    private Map<String, Double> calcularRatiosDesdeMapa(Map<String, LineaData> mapaAnio, List<String> ratios) {
        
        Map<String, Double> resultados = new HashMap<>();

        // --- Lógica de ejemplo para extraer saldos (¡AJUSTA CÓDIGOS!) ---
        // Debes reemplazar "11" por el código de 'Activo Corriente' en tu catálogo
        double activoCorriente = mapaAnio.getOrDefault("11", new LineaData(0.0, "")).saldo;
        // Debes reemplazar "21" por el código de 'Pasivo Corriente'
        double pasivoCorriente = mapaAnio.getOrDefault("21", new LineaData(0.0, "")).saldo;
        // Debes reemplazar "510101" por el código de 'Ventas'
        double ventas = mapaAnio.getOrDefault("197", new LineaData(0.0, "")).saldo; // 197 = Ventas en tu script
        // Debes reemplazar "1" por 'Total Activo'
        double totalActivo = mapaAnio.getOrDefault("1", new LineaData(0.0, "")).saldo;
        // Debes reemplazar "3" por 'Total Patrimonio'
        double totalPatrimonio = mapaAnio.getOrDefault("138", new LineaData(0.0, "")).saldo; // 138 = Patrimonio
        // Debes reemplazar por 'Utilidad Neta' (Resultado del Ejercicio)
        double utilidadNeta = mapaAnio.getOrDefault("141", new LineaData(0.0, "")).saldo; // 141 = Resultado del Ejercicio
        // Debes reemplazar por 'Inventarios'
        double inventarios = mapaAnio.getOrDefault("11", new LineaData(0.0, "")).saldo; // 11 = INVENTARIOS
        // -----------------------------------------------------------------


        for (String ratioId : ratios) {
            double valorCalculado = 0.0;

            switch (ratioId) {
                case "LIQUIDEZ_CORRIENTE":
                    if (pasivoCorriente != 0) {
                        valorCalculado = activoCorriente / pasivoCorriente;
                    }
                    break;

                case "PRUEBA_ACIDA":
                    if (pasivoCorriente != 0) {
                        // (Activo Corriente - Inventarios) / Pasivo Corriente
                        valorCalculado = (activoCorriente - inventarios) / pasivoCorriente;
                    }
                    break;
                    
                case "ROE": // Rentabilidad sobre Patrimonio
                    if (totalPatrimonio != 0) {
                        valorCalculado = (utilidadNeta / totalPatrimonio) * 100.0;
                    }
                    break;
                
                case "ROA": // Rentabilidad sobre Activos
                    if (totalActivo != 0) {
                        valorCalculado = (utilidadNeta / totalActivo) * 100.0;
                    }
                    break;

                case "ENDEUDAMIENTO":
                     // (Pasivo Total / Activo Total) ... 
                     // Necesitarías "Pasivo Total" (código "85" en tu script)
                    double pasivoTotal = mapaAnio.getOrDefault("85", new LineaData(0.0, "")).saldo;
                    if (totalActivo != 0) {
                        valorCalculado = (pasivoTotal / totalActivo) * 100.0;
                    }
                    break;
                
                // ... (agrega los 'case' para los otros ratios) ...
            }
            
            // Guarda el valor redondeado a 2 decimales
            resultados.put(ratioId, Math.round(valorCalculado * 100.0) / 100.0);
        }
        
        return resultados;
    }


    // =================================================================
    // V V V MÉTODOS HELPER DE REFLEXIÓN (Se quedan igual) V V V
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