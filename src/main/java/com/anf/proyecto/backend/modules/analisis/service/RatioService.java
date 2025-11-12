package com.anf.proyecto.backend.modules.analisis.service;


import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.Ratio.RatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.entity.*;
import com.anf.proyecto.backend.modules.analisis.repository.*;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa; // Asegúrate que la ruta sea correcta
import com.anf.proyecto.backend.modules.empresa.entity.ParametroSector;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository; // Asegúrate que la ruta sea correcta
import com.anf.proyecto.backend.modules.empresa.repository.ParametroSectorRepository; // Asegúrate que la ruta sea correcta
import com.anf.proyecto.backend.modules.estadofinanciero.repository.LineaEstadoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatioService {

    @Autowired
    private RatioRepository ratioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TipoRatioRepository tipoRatioRepository;

    @Autowired
    private ParametroSectorRepository parametroSectorRepository;
    private static final String TIPO_REPORTE_BALANCE = "BALANCE_GENERAL";
    private static final String TIPO_REPORTE_RESULTADOS = "ESTADO_RESULTADOS";
    @Autowired
    private LineaEstadoFinancieroRepository lineaEstadoFinancieroRepository;

    // Repositorio para CategoriaRatio, si fuera necesario
    @Autowired
    private CategoriaRatioRepository categoriaRatioRepository;


    @Transactional(readOnly = true)
    public List<RatioResponseDTO> findAll() {
        return ratioRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RatioResponseDTO> findById(Integer id) {
        return ratioRepository.findById(id).map(this::convertToResponseDTO);
    }

    @Transactional
    public RatioResponseDTO save(RatioSaveDTO saveDTO) {
        // 1. Obtener las entidades relacionadas (esto no cambia)
        Empresa empresa = empresaRepository.findById(saveDTO.getEmpresa_id())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + saveDTO.getEmpresa_id()));



        CategoriaRatio categoriaRatio = null;
        if (saveDTO.getId_categoria_ratio() != null) {
            categoriaRatio = categoriaRatioRepository.findById(saveDTO.getId_categoria_ratio())
                    .orElseThrow(() -> new EntityNotFoundException("CategoriaRatio no encontrada con id: " + saveDTO.getId_categoria_ratio()));
        }

        // Buscas el Parámetro Sector correctamente...
        ParametroSector parametroSector = null;
        if (saveDTO.getId_parametro_sector() != null) {
            parametroSector = parametroSectorRepository.findById(saveDTO.getId_parametro_sector())
                    .orElseThrow(() -> new EntityNotFoundException("ParametroSector no encontrado con id: " + saveDTO.getId_parametro_sector()));
        }

        // 3. Crear y poblar la nueva entidad Ratio
        Ratio ratio = new Ratio();
        ratio.setAnio_ratio(saveDTO.getAnio_ratio());
        ratio.setPeriodo_ratio(saveDTO.getPeriodo_ratio());
        ratio.setValor_calculado(saveDTO.getValor_calculado());
        ratio.setInterpretacion(saveDTO.getInterpretacion());
        // Asignar relaciones
        ratio.setEmpresa(empresa);
        ratio.setCategoriaRatio(categoriaRatio);

        // --- INICIO DE LA CORRECCIÓN ---
        // ¡Esta es la línea que faltaba!
        // Asigna el objeto ParametroSector que encontraste al nuevo Ratio.
        ratio.setParametroSector(parametroSector);
        // --- FIN DE LA CORRECCIÓN ---

        // 5. Guardar y devolver DTO
        Ratio savedRatio = ratioRepository.save(ratio);
        return convertToResponseDTO(savedRatio);
    }

    @Transactional(readOnly = true)
    public List<RatioResponseDTO> findByNombreEmpresa(String nombreEmpresa) {
        // Llama al nuevo método del repositorio
        List<Ratio> ratios = ratioRepository.findByEmpresa_NombreEmpresaIgnoreCase(nombreEmpresa);

        // Convierte la lista de entidades a una lista de DTOs usando el método existente
        return ratios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<RatioResponseDTO> update(Integer id, RatioUpdateDTO updateDTO) {
        return ratioRepository.findById(id)
                .map(existingRatio -> {
                    // Actualizar campos básicos
                    existingRatio.setAnio_ratio(updateDTO.getAnio_ratio());
                    existingRatio.setPeriodo_ratio(updateDTO.getPeriodo_ratio());
                    existingRatio.setValor_calculado(updateDTO.getValor_calculado());
                    existingRatio.setInterpretacion(updateDTO.getInterpretacion());

                    // Aquí podrías añadir lógica para recalcular los campos derivados si es necesario
                    // ...

                    Ratio updatedRatio = ratioRepository.save(existingRatio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        if (ratioRepository.existsById(id)) {
            ratioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODO PRIVADO DE MAPEO ---
    private RatioResponseDTO convertToResponseDTO(Ratio ratio) {
        RatioResponseDTO dto = new RatioResponseDTO();
        dto.setId_ratio(ratio.getId_ratio());

        dto.setAnio_ratio(ratio.getAnio_ratio());
        dto.setPeriodo_ratio(ratio.getPeriodo_ratio());
        dto.setValor_calculado(ratio.getValor_calculado());
        dto.setValor_sector_promedio(ratio.getValor_sector_promedio());
        dto.setDiferencia_vs_sector(ratio.getDiferencia_vs_sector());
        dto.setCumple_sector(ratio.getCumple_sector());


        dto.setInterpretacion(ratio.getInterpretacion());

        if (ratio.getEmpresa() != null) {
            RatioResponseDTO.EmpresaDTO empresaDTO = new RatioResponseDTO.EmpresaDTO();
            // Asegúrate que tu entidad Empresa tenga estos getters
            empresaDTO.setEmpresa_id(ratio.getEmpresa().getEmpresaId());
            empresaDTO.setNombre_empresa(ratio.getEmpresa().getNombreEmpresa());
            dto.setEmpresa(empresaDTO);
        }

        if (ratio.getCategoriaRatio() != null) {
            RatioResponseDTO.CategoriaRatioDTO catDto = new RatioResponseDTO.CategoriaRatioDTO();
            // Usando los nombres de campos de tu entidad CategoriaRatio
            catDto.setId_categoria_ratio(ratio.getCategoriaRatio().getIdCategoriaRatio());
            catDto.setNombre_categoria(ratio.getCategoriaRatio().getNombreTipo());
            dto.setCategoriaRatio(catDto);
        }



        if (ratio.getParametroSector() != null) {
            RatioResponseDTO.ParametroSectorDTO paramDto = new RatioResponseDTO.ParametroSectorDTO();
            paramDto.setId_parametro_sector(ratio.getParametroSector().getIdParametroSector());
            paramDto.setValor_referencia(ratio.getParametroSector().getValorReferencia());
            paramDto.setAnio_referencia(ratio.getParametroSector().getAnioReferencia());
            paramDto.setNombreRatio(ratio.getParametroSector().getNombreRatio());
            dto.setParametroSector(paramDto);
        }

        return dto;
    }
    // --- MÉTODO NUEVO PARA EL CÁLCULO ---
    @Transactional
    public Optional<RatioResponseDTO> calculateLiquidezRatio(Integer ratioId) {
        // 1. Encontrar el Ratio a calcular
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 2. Validar que es el Ratio de "Razón de circulante o liquidez corriente"
                    if (ratio.getCategoriaRatio() == null || !"Razón de circulante o liquidez corriente".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo de liquidez solo aplica para la categoría 'Razón de circulante o liquidez corriente'.");
                    }

                    // 3. Obtener los parámetros necesarios para la consulta de saldos
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anio = ratio.getAnio_ratio();
                    final String tipoReporte = "BALANCE_GENERAL"; // Asumimos que es para el Balance General

                    // 4. Definir las listas de códigos de cuenta
                    List<String> cuentasActivoCorriente = Arrays.asList("1401", "1402", "1403", "1404", "1405", "1406");
                    List<String> cuentasPasivoCorriente = Arrays.asList("2401", "2402", "2403");

                    // 5. Calcular los totales usando el nuevo método del repositorio
                    BigDecimal totalActivos = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasActivoCorriente);
                    BigDecimal totalPasivos = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasPasivoCorriente);

                    // 6. Calcular el "valor_calculado" (con manejo de división por cero)
                    BigDecimal valorCalculado;
                    if (totalPasivos.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Si el pasivo es cero, el ratio es 0 para evitar errores.
                    } else {
                        // Usamos una escala y un modo de redondeo para la división
                        valorCalculado = totalActivos.divide(totalPasivos, 4, RoundingMode.HALF_UP);
                    }

                    // 7. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 8. Generar la interpretación del resultado
                    String interpretacion = generarInterpretacionLiquidez(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 9. Actualizar la entidad Ratio con los valores calculados
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 10. Guardar la entidad actualizada en la base de datos
                    Ratio updatedRatio = ratioRepository.save(ratio);

                    // 11. Convertir a DTO y devolver el resultado
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación textual del ratio de liquidez.
     */
    private String generarInterpretacionLiquidez(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String comparacion = cumple ? "por encima" : "por debajo";
        String resultado = String.format("%.2f", valorCalculado);

        return String.format("La empresa dispone de %.2f USD de activo corriente por cada dólar de deuda a corto plazo. " +
                        "Este valor se encuentra %s del benchmark del sector, que es de %.2f.",
                valorCalculado, comparacion, valorReferencia);
    }
    @Transactional
    public Optional<RatioResponseDTO> calculateCapitalTrabajoRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Razón de capital de trabajo".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Razón de capital de trabajo'.");
                    }

                    // 2. Obtener parámetros comunes para la consulta
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anio = ratio.getAnio_ratio();
                    final String tipoReporte = "BALANCE_GENERAL";

                    // 3. Definir las listas de códigos de cuenta según tu fórmula
                    List<String> cuentasActivoCorriente = Arrays.asList("1401", "1402", "1403", "1404", "1405", "1406");
                    List<String> cuentasPasivoCorriente = Arrays.asList("2401", "2402", "2403", "240401", "240402", "240403");
                    List<String> cuentasActivosTotales = Arrays.asList("1401", "1402", "1403", "1404", "1405", "1406", "120401", "1407", "120106");

                    // 4. Calcular los totales de cada grupo de cuentas
                    BigDecimal totalActivoCorriente = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasActivoCorriente);
                    BigDecimal totalPasivoCorriente = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasPasivoCorriente);
                    BigDecimal totalActivos = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasActivosTotales);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: (Activo Corriente - Pasivo Corriente) / Activos Totales
                    BigDecimal valorCalculado;
                    BigDecimal capitalDeTrabajo = totalActivoCorriente.subtract(totalPasivoCorriente);

                    if (totalActivos.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = capitalDeTrabajo.divide(totalActivos, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionCapitalTrabajo(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para el Capital de Trabajo.
     */
    private String generarInterpretacionCapitalTrabajo(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String comparacion = cumple ? "por encima" : "por debajo";
        return String.format(
                "Por cada dólar de activo total, la empresa genera %.2f centavos de capital de trabajo neto para operar. " +
                        "Este valor se encuentra %s del benchmark del sector (%.2f).",
                valorCalculado, comparacion, valorReferencia
        );
    }

    @Transactional
    public Optional<RatioResponseDTO> calculateEfectivoRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Razón de efectivo".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Razón de efectivo'.");
                    }

                    // 2. Obtener parámetros comunes para la consulta
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anio = ratio.getAnio_ratio();
                    final String tipoReporte = "BALANCE_GENERAL";

                    // 3. Definir las listas de códigos de cuenta según tu fórmula
                    List<String> cuentasEfectivo = Arrays.asList("1401", "1403");
                    List<String> cuentasPasivoCorriente = Arrays.asList("2401", "2402", "2403", "240401", "240402", "240403");

                    // 4. Calcular los totales de cada grupo de cuentas
                    BigDecimal totalEfectivo = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasEfectivo);
                    BigDecimal totalPasivoCorriente = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anio, tipoReporte, cuentasPasivoCorriente);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: (Efectivo y Equivalentes) / Pasivo Corriente
                    BigDecimal valorCalculado;
                    if (totalPasivoCorriente.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = totalEfectivo.divide(totalPasivoCorriente, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionEfectivo(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para la Razón de Efectivo.
     */
    private String generarInterpretacionEfectivo(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String comparacion = cumple ? "saludable y por encima" : "reducido y por debajo";
        return String.format(
                "La empresa cuenta con %.2f centavos en efectivo y equivalentes para cubrir cada dólar de sus deudas a corto plazo. " +
                        "Este nivel de liquidez inmediata se considera %s del benchmark del sector (%.2f).",
                valorCalculado, comparacion, valorReferencia
        );
    }

    // --- NUEVO MÉTODO PARA CÁLCULO DE ROTACIÓN DE CUENTAS POR COBRAR ---
    @Transactional
    public Optional<RatioResponseDTO> calculateRotacionCuentasPorCobrarRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Razón de rotación de cuentas por cobrar".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Razón de rotación de cuentas por cobrar'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();
                    final Integer anioAnterior = anioActual - 1;

                    // 3. Calcular el NUMERADOR (Ventas Netas del Estado de Resultados)
                    List<String> cuentaVentasNetas = Arrays.asList("510201");
                    BigDecimal ventasNetas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaVentasNetas);

                    // 4. Calcular el DENOMINADOR (Promedio de Cuentas por Cobrar de dos años)
                    List<String> cuentaPorCobrar = Arrays.asList("1405");

                    // Saldo del año actual (del Balance General)
                    BigDecimal cxcAnioActual = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_BALANCE, cuentaPorCobrar);
                    // Saldo del año anterior (del Balance General)
                    BigDecimal cxcAnioAnterior = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioAnterior, TIPO_REPORTE_BALANCE, cuentaPorCobrar);

                    BigDecimal promedioCxc = (cxcAnioActual.add(cxcAnioAnterior)).divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: Ventas Netas / Promedio de Cuentas por Cobrar
                    BigDecimal valorCalculado;
                    if (promedioCxc.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = ventasNetas.divide(promedioCxc, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionRotacionCuentasPorCobrar(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para la Rotación de Cuentas por Cobrar.
     */
    private String generarInterpretacionRotacionCuentasPorCobrar(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String eficiencia = cumple ? "eficiente" : "lenta";
        return String.format(
                "La empresa convirtió su saldo promedio de cuentas por cobrar en ventas (efectivo) %.2f veces durante el año. " +
                        "Una rotación más alta indica una gestión de cobro más %s en comparación con el benchmark del sector (%.2f).",
                valorCalculado, eficiencia, valorReferencia
        );
    }


    // --- NUEVO MÉTODO PARA CÁLCULO DE PERÍODO MEDIO DE COBRANZA ---
    @Transactional
    public Optional<RatioResponseDTO> calculatePeriodoCobranzaRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Período medio de cobranza".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Período medio de cobranza'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();
                    final Integer anioAnterior = anioActual - 1;

                    // 3. Obtener el DENOMINADOR (Ventas Netas del Estado de Resultados)
                    List<String> cuentaVentasNetas = Arrays.asList("510201");
                    BigDecimal ventasNetas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaVentasNetas);

                    // 4. Obtener el NUMERADOR (Promedio de Cuentas por Cobrar de dos años)
                    List<String> cuentaPorCobrar = Arrays.asList("1405");
                    BigDecimal cxcAnioActual = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_BALANCE, cuentaPorCobrar);
                    BigDecimal cxcAnioAnterior = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioAnterior, TIPO_REPORTE_BALANCE, cuentaPorCobrar);
                    BigDecimal promedioCxc = (cxcAnioActual.add(cxcAnioAnterior)).divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: (Promedio de Cuentas por Cobrar * 365) / Ventas Netas
                    BigDecimal valorCalculado;
                    BigDecimal numerador = promedioCxc.multiply(new BigDecimal("365"));

                    if (ventasNetas.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Si no hay ventas, no hay período de cobro.
                    } else {
                        valorCalculado = numerador.divide(ventasNetas, 2, RoundingMode.HALF_UP); // El resultado es en días, 2 decimales es suficiente.
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);

                    // --- ¡LÓGICA INVERTIDA! --- Un período de cobranza más bajo es MEJOR.
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) <= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionPeriodoCobranza(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para el Período Medio de Cobranza.
     */
    private String generarInterpretacionPeriodoCobranza(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String eficiencia = cumple ? "eficiente (más rápido)" : "lento (más demorado)";
        return String.format(
                "En promedio, la empresa tarda %.2f días en cobrar sus ventas a crédito. " +
                        "Este período de cobranza es considerado %s en comparación con el benchmark del sector, que es de %.2f días.",
                valorCalculado, eficiencia, valorReferencia
        );
    }

    // --- NUEVO MÉTODO PARA ÍNDICE DE ROTACIÓN DE ACTIVOS TOTALES ---
    @Transactional
    public Optional<RatioResponseDTO> calculateRotacionActivosTotalesRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Índice de rotación de activos totales".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Índice de rotación de activos totales'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();
                    final Integer anioAnterior = anioActual - 1;

                    // 3. Calcular el NUMERADOR (Ventas Netas del Estado de Resultados)
                    List<String> cuentaVentasNetas = Arrays.asList("510201");
                    BigDecimal ventasNetas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaVentasNetas);

                    // 4. Calcular el DENOMINADOR (Promedio de Activos Totales de dos años)
                    List<String> cuentasActivosTotales = Arrays.asList("1401", "1402", "1403", "1404", "1405", "1406", "120401", "1407", "120106");

                    // Saldo de activos del año actual (del Balance General)
                    BigDecimal activosAnioActual = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_BALANCE, cuentasActivosTotales);
                    // Saldo de activos del año anterior (del Balance General)
                    BigDecimal activosAnioAnterior = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioAnterior, TIPO_REPORTE_BALANCE, cuentasActivosTotales);

                    BigDecimal promedioActivos = (activosAnioActual.add(activosAnioAnterior)).divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: Ventas Netas / Promedio de Activos Totales
                    BigDecimal valorCalculado;
                    if (promedioActivos.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = ventasNetas.divide(promedioActivos, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);

                    // Para este ratio, un valor más alto es mejor (mayor eficiencia)
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionRotacionActivosTotales(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para la Rotación de Activos Totales.
     */
    private String generarInterpretacionRotacionActivosTotales(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String eficiencia = cumple ? "eficiente" : "poco eficiente";
        return String.format(
                "La empresa generó %.2f USD en ventas por cada dólar invertido en activos totales. " +
                        "Esto indica una gestión de activos %s en comparación con el benchmark del sector (%.2f).",
                valorCalculado, eficiencia, valorReferencia
        );
    }

    // --- NUEVO MÉTODO PARA ÍNDICE DE ROTACIÓN DE ACTIVOS FIJOS ---
    @Transactional
    public Optional<RatioResponseDTO> calculateRotacionActivosFijosRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Índice de rotación de activos fijos".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Índice de rotación de activos fijos'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();

                    // 3. Calcular el NUMERADOR (Ventas Netas del Estado de Resultados)
                    List<String> cuentaVentasNetas = Arrays.asList("510201");
                    BigDecimal ventasNetas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaVentasNetas);

                    // 4. Calcular el DENOMINADOR (Activos Fijos del Balance General del año actual)
                    List<String> cuentaActivosFijos = Arrays.asList("120106");
                    BigDecimal activosFijos = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_BALANCE, cuentaActivosFijos);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: Ventas Netas / Activos Fijos
                    BigDecimal valorCalculado;
                    if (activosFijos.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = ventasNetas.divide(activosFijos, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);

                    // Para este ratio, un valor más alto es mejor (mayor eficiencia)
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionRotacionActivosFijos(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para la Rotación de Activos Fijos.
     */
    private String generarInterpretacionRotacionActivosFijos(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String eficiencia = cumple ? "eficiente" : "ineficiente";
        return String.format(
                "La empresa generó %.2f USD en ventas por cada dólar invertido en activos fijos (propiedad, planta y equipo). " +
                        "Esto sugiere una gestión %s de sus activos productivos en comparación con el benchmark del sector (%.2f).",
                valorCalculado, eficiencia, valorReferencia
        );
    }


    @Transactional
    public Optional<RatioResponseDTO> calculateMargenBrutoRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Índice de margen bruto".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Índice de margen bruto'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();

                    // 3. Definir las listas de cuentas (todas del Estado de Resultados)
                    List<String> cuentasVentasBrutas = Arrays.asList("510201", "510202", "510203", "510204", "510205");
                    List<String> cuentasCostoVentas = Arrays.asList("410201", "410202", "410203", "410204", "410303");
                    List<String> cuentaDenominador = Arrays.asList("510201");

                    // 4. Calcular los totales de cada grupo
                    BigDecimal totalVentasBrutas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentasVentasBrutas);
                    BigDecimal totalCostoVentas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentasCostoVentas);
                    BigDecimal ventasDenominador = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaDenominador);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: (Ventas Brutas - Costo de Ventas) / Ventas Netas (cuenta 510201)
                    BigDecimal utilidadBruta = totalVentasBrutas.subtract(totalCostoVentas);

                    BigDecimal valorCalculado;
                    if (ventasDenominador.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        // El resultado es un porcentaje, por lo que 4 decimales son adecuados.
                        valorCalculado = utilidadBruta.divide(ventasDenominador, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);

                    // Para este ratio, un margen más alto es mejor
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionMargenBruto(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para el Índice de Margen Bruto.
     */
    private String generarInterpretacionMargenBruto(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String rentabilidad = cumple ? "saludable" : "reducida";
        // Multiplicamos por 100 para mostrarlo como porcentaje en el texto
        BigDecimal margenPorcentual = valorCalculado.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

        return String.format(
                "La empresa retiene un %.2f%% de sus ingresos después de cubrir el costo de los bienes vendidos. " +
                        "Esto indica una rentabilidad bruta %s en comparación con el benchmark del sector (%.2f%%).",
                margenPorcentual, rentabilidad, valorReferencia.multiply(new BigDecimal("100"))
        );
    }


    // --- NUEVO MÉTODO PARA ÍNDICE DE MARGEN OPERATIVO ---
    @Transactional
    public Optional<RatioResponseDTO> calculateMargenOperativoRatio(Integer ratioId) {
        return ratioRepository.findById(ratioId)
                .map(ratio -> {
                    // 1. Validar que es la categoría correcta
                    if (ratio.getCategoriaRatio() == null || !"Índice de margen operativo".equals(ratio.getCategoriaRatio().getNombreTipo())) {
                        throw new IllegalStateException("El cálculo solo aplica para la categoría 'Índice de margen operativo'.");
                    }

                    // 2. Obtener parámetros comunes
                    final Integer empresaId = ratio.getEmpresa().getEmpresaId();
                    final Integer anioActual = ratio.getAnio_ratio();

                    // 3. Definir las listas de cuentas (todas del Estado de Resultados)
                    List<String> cuentasVentasBrutas = Arrays.asList("510201", "510202", "510203", "510204", "510205");
                    List<String> cuentasCostoVentas = Arrays.asList("410201", "410202", "410203", "410204", "410303");
                    List<String> cuentasGastosOperativos = Arrays.asList("430101", "420212");
                    List<String> cuentaDenominador = Arrays.asList("510201");

                    // 4. Calcular los totales de cada grupo
                    BigDecimal totalVentasBrutas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentasVentasBrutas);
                    BigDecimal totalCostoVentas = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentasCostoVentas);
                    BigDecimal totalGastosOperativos = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentasGastosOperativos);
                    BigDecimal ventasDenominador = lineaEstadoFinancieroRepository.sumSaldosByCriteria(empresaId, anioActual, TIPO_REPORTE_RESULTADOS, cuentaDenominador);

                    // 5. Aplicar la fórmula para "valor_calculado"
                    // Formula: (Ventas Brutas - Costo Ventas - Gastos Operativos) / Ventas Netas (510201)
                    BigDecimal utilidadOperativa = totalVentasBrutas.subtract(totalCostoVentas).subtract(totalGastosOperativos);

                    BigDecimal valorCalculado;
                    if (ventasDenominador.compareTo(BigDecimal.ZERO) == 0) {
                        valorCalculado = BigDecimal.ZERO; // Evitar división por cero
                    } else {
                        valorCalculado = utilidadOperativa.divide(ventasDenominador, 4, RoundingMode.HALF_UP);
                    }

                    // 6. Obtener valor de referencia y realizar los cálculos restantes
                    BigDecimal valorReferenciaSector = ratio.getParametroSector().getValorReferencia();
                    BigDecimal diferenciaVsSector = valorCalculado.subtract(valorReferenciaSector);

                    // Para este ratio, un margen más alto es mejor
                    boolean cumpleSector = valorCalculado.compareTo(valorReferenciaSector) >= 0;

                    // 7. Generar la interpretación específica para este ratio
                    String interpretacion = generarInterpretacionMargenOperativo(valorCalculado, valorReferenciaSector, cumpleSector);

                    // 8. Actualizar la entidad Ratio con los nuevos valores
                    ratio.setValor_calculado(valorCalculado);
                    ratio.setValor_sector_promedio(valorReferenciaSector);
                    ratio.setDiferencia_vs_sector(diferenciaVsSector);
                    ratio.setCumple_sector(cumpleSector);
                    ratio.setInterpretacion(interpretacion);

                    // 9. Guardar y devolver el DTO
                    Ratio updatedRatio = ratioRepository.save(ratio);
                    return convertToResponseDTO(updatedRatio);
                });
    }

    /**
     * Método auxiliar para generar una interpretación para el Índice de Margen Operativo.
     */
    private String generarInterpretacionMargenOperativo(BigDecimal valorCalculado, BigDecimal valorReferencia, boolean cumple) {
        String eficiencia = cumple ? "eficiente" : "comprometida";
        // Multiplicamos por 100 para mostrarlo como porcentaje en el texto
        BigDecimal margenPorcentual = valorCalculado.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

        return String.format(
                "Después de cubrir costos y gastos operativos, la empresa retiene un %.2f%% de sus ingresos como utilidad operativa. " +
                        "Esto indica una eficiencia operativa %s en comparación con el benchmark del sector (%.2f%%).",
                margenPorcentual, eficiencia, valorReferencia.multiply(new BigDecimal("100"))
        );
    }


}
