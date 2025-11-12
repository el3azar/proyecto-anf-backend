package com.anf.proyecto.backend.modules.analisis.service;


// package com.anf.proyecto.backend.modules.analisis.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.*;
import com.anf.proyecto.backend.modules.analisis.entity.CategoriaRatio;
import com.anf.proyecto.backend.modules.analisis.entity.TipoRatio;
import com.anf.proyecto.backend.modules.analisis.repository.CategoriaRatioRepository;
import com.anf.proyecto.backend.modules.analisis.repository.TipoRatioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaRatioService {

    @Autowired
    private CategoriaRatioRepository categoriaRatioRepository;

    // --- INICIO DE CAMBIOS ---
    @Autowired
    private TipoRatioRepository tipoRatioRepository; // Inyectar el nuevo repositorio

    // Helper para convertir la entidad a DTO de respuesta
    private CategoriaRatioResponseDTO mapToResponseDTO(CategoriaRatio categoria) {
        CategoriaRatioResponseDTO dto = new CategoriaRatioResponseDTO();
        dto.setIdCategoriaRatio(categoria.getIdCategoriaRatio());
        dto.setNombreTipo(categoria.getNombreTipo());
        dto.setDescripcion(categoria.getDescripcion());
        // Mapear datos del TipoRatio asociado
        if (categoria.getTipoRatio() != null) {

            dto.setNombreTipoRatio(categoria.getTipoRatio().getNombreRatio());
        }
        return dto;
    }
    // --- FIN DE CAMBIOS ---

    public List<CategoriaRatioResponseDTO> getAllCategorias() {
        return categoriaRatioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO) // Usar el helper
                .collect(Collectors.toList());
    }

    public CategoriaRatioResponseDTO getCategoriaById(Integer id) {
        CategoriaRatio categoria = categoriaRatioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría de ratio no encontrada con id: " + id));
        return mapToResponseDTO(categoria); // Usar el helper
    }

    // --- MÉTODO CREATE MODIFICADO ---
    public CategoriaRatioResponseDTO createCategoria(CategoriaRatioSaveDTO saveDTO) {
        if (categoriaRatioRepository.existsByNombreTipo(saveDTO.getNombreTipo())) {
            throw new BusinessRuleException("Ya existe una categoría con el nombre: " + saveDTO.getNombreTipo());
        }

        // 1. Buscar el TipoRatio por el ID proporcionado en el DTO
        TipoRatio tipoRatio = tipoRatioRepository.findById(saveDTO.getIdTipoRatio())
                .orElseThrow(() -> new NotFoundException("Tipo de ratio no encontrado con id: " + saveDTO.getIdTipoRatio()));

        // 2. Mapear DTO a la entidad y establecer la relación
        CategoriaRatio categoria = new CategoriaRatio();
        categoria.setNombreTipo(saveDTO.getNombreTipo());
        categoria.setDescripcion(saveDTO.getDescripcion());
        categoria.setTipoRatio(tipoRatio); // 3. Asignar el objeto TipoRatio encontrado

        CategoriaRatio savedCategoria = categoriaRatioRepository.save(categoria);
        return mapToResponseDTO(savedCategoria);
    }

    // --- MÉTODO UPDATE MODIFICADO ---
    public CategoriaRatioResponseDTO updateCategoria(Integer id, CategoriaRatioUpdateDTO updateDTO) {
        CategoriaRatio categoriaExistente = categoriaRatioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría de ratio no encontrada con id: " + id));

        categoriaRatioRepository.findByNombreTipo(updateDTO.getNombreTipo()).ifPresent(cat -> {
            if (!cat.getIdCategoriaRatio().equals(id)) {
                throw new BusinessRuleException("El nombre '" + updateDTO.getNombreTipo() + "' ya está en uso por otra categoría.");
            }
        });

        // 1. Buscar el nuevo TipoRatio por el ID proporcionado
        TipoRatio tipoRatio = tipoRatioRepository.findById(updateDTO.getIdTipoRatio())
                .orElseThrow(() -> new NotFoundException("Tipo de ratio no encontrado con id: " + updateDTO.getIdTipoRatio()));

        // 2. Actualizar propiedades y la relación
        categoriaExistente.setNombreTipo(updateDTO.getNombreTipo());
        categoriaExistente.setDescripcion(updateDTO.getDescripcion());
        categoriaExistente.setTipoRatio(tipoRatio); // 3. Actualizar la referencia

        CategoriaRatio updatedCategoria = categoriaRatioRepository.save(categoriaExistente);
        return mapToResponseDTO(updatedCategoria);
    }

    public void deleteCategoria(Integer id) {
        if (!categoriaRatioRepository.existsById(id)) {
            throw new NotFoundException("No se puede eliminar. Categoría de ratio no encontrada con id: " + id);
        }
        categoriaRatioRepository.deleteById(id);
    }
}