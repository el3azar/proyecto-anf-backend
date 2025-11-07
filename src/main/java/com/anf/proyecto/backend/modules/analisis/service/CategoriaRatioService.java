package com.anf.proyecto.backend.modules.analisis.service;


import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioResponseDTO;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioSaveDTO;
import com.anf.proyecto.backend.modules.analisis.dto.CategoriaRatio.CategoriaRatioUpdateDTO;
import com.anf.proyecto.backend.modules.analisis.entity.CategoriaRatio;
import com.anf.proyecto.backend.modules.analisis.repository.CategoriaRatioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaRatioService {

    @Autowired
    private CategoriaRatioRepository categoriaRatioRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Obtiene todas las categorías de ratio.
     */
    public List<CategoriaRatioResponseDTO> getAllCategorias() {
        return categoriaRatioRepository.findAll()
                .stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaRatioResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una categoría por su ID.
     */
    public CategoriaRatioResponseDTO getCategoriaById(Integer id) {
        CategoriaRatio categoria = categoriaRatioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría de ratio no encontrada con id: " + id));
        return modelMapper.map(categoria, CategoriaRatioResponseDTO.class);
    }

    /**
     * Crea una nueva categoría de ratio.
     */
    public CategoriaRatioResponseDTO createCategoria(CategoriaRatioSaveDTO saveDTO) {
        // Se usa la nueva nomenclatura camelCase
        if (categoriaRatioRepository.existsByNombreTipo(saveDTO.getNombreTipo())) {
            throw new BusinessRuleException("Ya existe una categoría con el nombre: " + saveDTO.getNombreTipo());
        }

        // El código de mapeo y guardado ahora está DENTRO del método
        CategoriaRatio categoria = modelMapper.map(saveDTO, CategoriaRatio.class);
        CategoriaRatio savedCategoria = categoriaRatioRepository.save(categoria);
        return modelMapper.map(savedCategoria, CategoriaRatioResponseDTO.class);
    }

    /**
     * Actualiza una categoría de ratio existente.
     */
    public CategoriaRatioResponseDTO updateCategoria(Integer id, CategoriaRatioUpdateDTO updateDTO) {
        CategoriaRatio categoriaExistente = categoriaRatioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría de ratio no encontrada con id: " + id));

        categoriaRatioRepository.findByNombreTipo(updateDTO.getNombreTipo()).ifPresent(cat -> {
            // --- CAMBIO CLAVE AQUÍ ---
            // Usamos el getter camelCase de la entidad
            if (!cat.getIdCategoriaRatio().equals(id)) {
                throw new BusinessRuleException("El nombre '" + updateDTO.getNombreTipo() + "' ya está en uso por otra categoría.");
            }
        });

        categoriaExistente.setNombreTipo(updateDTO.getNombreTipo());
        categoriaExistente.setDescripcion(updateDTO.getDescripcion());

        CategoriaRatio updatedCategoria = categoriaRatioRepository.save(categoriaExistente);
        return modelMapper.map(updatedCategoria, CategoriaRatioResponseDTO.class);
    }

    /**
     * Elimina una categoría de ratio por su ID.
     */
    public void deleteCategoria(Integer id) {
        if (!categoriaRatioRepository.existsById(id)) {
            throw new NotFoundException("No se puede eliminar. Categoría de ratio no encontrada con id: " + id);
        }
        categoriaRatioRepository.deleteById(id);
    }
}