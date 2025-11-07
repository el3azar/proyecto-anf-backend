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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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
        // 1. Obtener las entidades relacionadas
        Empresa empresa = empresaRepository.findById(saveDTO.getEmpresa_id())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + saveDTO.getEmpresa_id()));

        TipoRatio tipoRatio = tipoRatioRepository.findById(saveDTO.getId_tipo_ratio())
                .orElseThrow(() -> new EntityNotFoundException("TipoRatio no encontrado con id: " + saveDTO.getId_tipo_ratio()));

        // --- INICIO DE LA MODIFICACIÓN ---
        // Buscar la CategoriaRatio usando el nuevo ID del DTO.
        // Hacemos que sea opcional, ya que en tu entidad Ratio no es un campo obligatorio (nullable = true).
        CategoriaRatio categoriaRatio = null;
        if (saveDTO.getId_categoria_ratio() != null) {
            categoriaRatio = categoriaRatioRepository.findById(saveDTO.getId_categoria_ratio())
                    .orElseThrow(() -> new EntityNotFoundException("CategoriaRatio no encontrada con id: " + saveDTO.getId_categoria_ratio()));
        }
        // --- FIN DE LA MODIFICACIÓN ---

        // 2. Lógica de Negocio: Encontrar el parámetro de sector
        Optional<ParametroSector> parametroSectorOpt = parametroSectorRepository
                .findFirstBySectorAndAnioReferencia(empresa.getSector(), saveDTO.getAnio_ratio());

        // 3. Crear y poblar la nueva entidad Ratio
        Ratio ratio = new Ratio();
        ratio.setAnio_ratio(saveDTO.getAnio_ratio());
        ratio.setPeriodo_ratio(saveDTO.getPeriodo_ratio());
        ratio.setValor_calculado(saveDTO.getValor_calculado());
        ratio.setInterpretacion(saveDTO.getInterpretacion());

        // Asignar relaciones
        ratio.setEmpresa(empresa);
        ratio.setTipoRatio(tipoRatio);
        ratio.setCategoriaRatio(categoriaRatio); // <--- ASIGNAR LA CATEGORÍA AQUÍ
        parametroSectorOpt.ifPresent(ratio::setParametroSector);

        // ... (resto de la lógica de negocio para calcular campos derivados)
        if (parametroSectorOpt.isPresent()) {
            // ... (código existente)
        }

        // 5. Guardar y devolver DTO
        Ratio savedRatio = ratioRepository.save(ratio);
        return convertToResponseDTO(savedRatio);
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
        // ... (otros campos)
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

        if (ratio.getTipoRatio() != null) {
            RatioResponseDTO.TipoRatioDTO tipoDto = new RatioResponseDTO.TipoRatioDTO();
            tipoDto.setId_tipo_ratio(ratio.getTipoRatio().getId_tipo_ratio());
            tipoDto.setNombre_ratio(ratio.getTipoRatio().getNombre_ratio());
            tipoDto.setCodigo_ratio(ratio.getTipoRatio().getCodigo_ratio());
            dto.setTipoRatio(tipoDto);
        }

        if (ratio.getParametroSector() != null) {
            RatioResponseDTO.ParametroSectorDTO paramDto = new RatioResponseDTO.ParametroSectorDTO();
            paramDto.setId_parametro_sector(ratio.getParametroSector().getIdParametroSector());
            paramDto.setValor_referencia(ratio.getParametroSector().getValorReferencia());
            paramDto.setAnio_referencia(ratio.getParametroSector().getAnioReferencia());
            dto.setParametroSector(paramDto);
        }

        return dto;
    }
}
