package com.anf.proyecto.backend.modules.analisis.service;

import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.TipoRatio.TipoRatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.entity.TipoRatio;
import com.anf.proyecto.backend.modules.analisis.repository.CategoriaRatioRepository;
import com.anf.proyecto.backend.modules.analisis.repository.TipoRatioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoRatioService {

    @Autowired
    private TipoRatioRepository tipoRatioRepository;

    @Autowired
    private CategoriaRatioRepository categoriaRatioRepository; // Necesario para asociar la categoría

    /**
     * Obtiene todos los Tipos de Ratio.
     * @return Una lista de DTOs de respuesta.
     */
    @Transactional(readOnly = true)
    public List<TipoRatioResponseDTO> findAll() {
        List<TipoRatio> tipoRatios = tipoRatioRepository.findAll();
        return tipoRatios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un Tipo de Ratio por su ID.
     * @param id El ID del Tipo de Ratio.
     * @return Un Optional que contiene el DTO si se encuentra.
     */
    @Transactional(readOnly = true)
    public Optional<TipoRatioResponseDTO> findById(Integer id) {
        return tipoRatioRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    /**
     * Guarda un nuevo Tipo de Ratio en la base de datos.
     * @param saveDTO El DTO con la información para crear.
     * @return El DTO del Tipo de Ratio guardado.
     */
    @Transactional
    public TipoRatioResponseDTO save(TipoRatioSaveDTO saveDTO) {
        TipoRatio tipoRatio = convertToEntity(saveDTO);
        TipoRatio savedTipoRatio = tipoRatioRepository.save(tipoRatio);
        return convertToResponseDTO(savedTipoRatio);
    }

    /**
     * Actualiza un Tipo de Ratio existente.
     * @param id El ID del Tipo de Ratio a actualizar.
     * @param updateDTO El DTO con la nueva información.
     * @return Un Optional que contiene el DTO actualizado si el ID existe.
     */
    @Transactional
    public Optional<TipoRatioResponseDTO> update(Integer id, TipoRatioUpdateDTO updateDTO) {
        return tipoRatioRepository.findById(id)
                .map(existingTipoRatio -> {
                    updateEntityFromDTO(existingTipoRatio, updateDTO);
                    TipoRatio updatedTipoRatio = tipoRatioRepository.save(existingTipoRatio);
                    return convertToResponseDTO(updatedTipoRatio);
                });
    }

    /**
     * Elimina un Tipo de Ratio por su ID.
     * @param id El ID del Tipo de Ratio a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    @Transactional
    public boolean deleteById(Integer id) {
        if (tipoRatioRepository.existsById(id)) {
            tipoRatioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS PRIVADOS DE MAPEO ---

    /**
     * Convierte una entidad TipoRatio a su DTO de respuesta.
     */
    private TipoRatioResponseDTO convertToResponseDTO(TipoRatio tipoRatio) {
        TipoRatioResponseDTO dto = new TipoRatioResponseDTO();
        dto.setId_tipo_ratio(tipoRatio.getId_tipo_ratio());
        dto.setNombre_ratio(tipoRatio.getNombre_ratio());
        dto.setCodigo_ratio(tipoRatio.getCodigo_ratio());
        dto.setDescripcion(tipoRatio.getDescripcion());
        dto.setUnidad_ratio(tipoRatio.getUnidad_ratio());

        return dto;
    }

    /**
     * Convierte un DTO de guardado a una entidad TipoRatio.
     */
    private TipoRatio convertToEntity(TipoRatioSaveDTO saveDTO) {
        TipoRatio tipoRatio = new TipoRatio();
        tipoRatio.setNombre_ratio(saveDTO.getNombre_ratio());
        tipoRatio.setCodigo_ratio(saveDTO.getCodigo_ratio());
        tipoRatio.setDescripcion(saveDTO.getDescripcion());
        tipoRatio.setUnidad_ratio(saveDTO.getUnidad_ratio());

        return tipoRatio;
    }

    /**
     * Actualiza una entidad existente a partir de un DTO de actualización.
     */
    private void updateEntityFromDTO(TipoRatio tipoRatio, TipoRatioUpdateDTO updateDTO) {
        tipoRatio.setNombre_ratio(updateDTO.getNombre_ratio());
        tipoRatio.setCodigo_ratio(updateDTO.getCodigo_ratio());
        tipoRatio.setDescripcion(updateDTO.getDescripcion());
        tipoRatio.setUnidad_ratio(updateDTO.getUnidad_ratio());
    }
}