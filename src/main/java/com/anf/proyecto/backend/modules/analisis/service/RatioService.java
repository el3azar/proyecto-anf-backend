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

}
