package com.anf.proyecto.backend.modules.empresa.service;

import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorResponseDTO;
import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorSaveDTO;
import com.anf.proyecto.backend.modules.empresa.dto.ParametroSector.ParametroSectorUpdateDTO;
import com.anf.proyecto.backend.modules.empresa.entity.ParametroSector;
import com.anf.proyecto.backend.modules.empresa.entity.Sector;
import com.anf.proyecto.backend.modules.analisis.entity.CategoriaRatio; // Asegúrate que la ruta sea correcta
import com.anf.proyecto.backend.modules.empresa.repository.ParametroSectorRepository;
import com.anf.proyecto.backend.modules.empresa.repository.SectorRepository;
import com.anf.proyecto.backend.modules.analisis.repository.CategoriaRatioRepository; // Asegúrate que la ruta sea correcta

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParametroSectorService {

    @Autowired
    private ParametroSectorRepository parametroSectorRepository;

    @Autowired
    private SectorRepository sectorRepository; // Repositorio para la entidad Sector

    @Autowired
    private CategoriaRatioRepository categoriaRatioRepository; // Repositorio para CategoriaRatio

    /**
     * Obtiene todos los Parámetros de Sector.
     */
    @Transactional(readOnly = true)
    public List<ParametroSectorResponseDTO> findAll() {
        return parametroSectorRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un Parámetro de Sector por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<ParametroSectorResponseDTO> findById(Integer id) {
        return parametroSectorRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    /**
     * Guarda un nuevo Parámetro de Sector.
     */
    @Transactional
    public ParametroSectorResponseDTO save(ParametroSectorSaveDTO saveDTO) {
        ParametroSector parametroSector = new ParametroSector();

        // Asignar entidades relacionadas
        Sector sector = sectorRepository.findById(saveDTO.getId_sector())
                .orElseThrow(() -> new EntityNotFoundException("Sector no encontrado con id: " + saveDTO.getId_sector()));
        CategoriaRatio categoriaRatio = categoriaRatioRepository.findById(saveDTO.getId_categoria_ratio())
                .orElseThrow(() -> new EntityNotFoundException("CategoriaRatio no encontrada con id: " + saveDTO.getId_categoria_ratio()));

        parametroSector.setNombreRatio(saveDTO.getNombreRatio());
        parametroSector.setValorReferencia(saveDTO.getValorReferencia());
        parametroSector.setFuente(saveDTO.getFuente());
        parametroSector.setAnioReferencia(saveDTO.getAnioReferencia());
        parametroSector.setSector(sector);
        parametroSector.setCategoriaRatio(categoriaRatio);

        ParametroSector savedParametro = parametroSectorRepository.save(parametroSector);
        return convertToResponseDTO(savedParametro);
    }

    /**
     * Actualiza un Parámetro de Sector existente.
     */
    @Transactional
    public Optional<ParametroSectorResponseDTO> update(Integer id, ParametroSectorUpdateDTO updateDTO) {
        return parametroSectorRepository.findById(id)
                .map(existingParametro -> {
                    updateEntityFromDTO(existingParametro, updateDTO);
                    ParametroSector updatedParametro = parametroSectorRepository.save(existingParametro);
                    return convertToResponseDTO(updatedParametro);
                });
    }

    /**
     * Elimina un Parámetro de Sector por su ID.
     */
    @Transactional
    public boolean deleteById(Integer id) {
        if (parametroSectorRepository.existsById(id)) {
            parametroSectorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS PRIVADOS DE MAPEO ---

    private void updateEntityFromDTO(ParametroSector entity, ParametroSectorUpdateDTO dto) {
        entity.setNombreRatio(dto.getNombreRatio());
        entity.setValorReferencia(dto.getValorReferencia());
        entity.setFuente(dto.getFuente());
        entity.setAnioReferencia(dto.getAnioReferencia());

        // Actualizar relaciones si los IDs son proporcionados
        if (dto.getId_sector() != null) {
            Sector sector = sectorRepository.findById(dto.getId_sector())
                    .orElseThrow(() -> new EntityNotFoundException("Sector no encontrado con id: " + dto.getId_sector()));
            entity.setSector(sector);
        }
        if (dto.getId_categoria_ratio() != null) {
            CategoriaRatio categoriaRatio = categoriaRatioRepository.findById(dto.getId_categoria_ratio())
                    .orElseThrow(() -> new EntityNotFoundException("CategoriaRatio no encontrada con id: " + dto.getId_categoria_ratio()));
            entity.setCategoriaRatio(categoriaRatio);
        }
    }

    private ParametroSectorResponseDTO convertToResponseDTO(ParametroSector entity) {
        ParametroSectorResponseDTO dto = new ParametroSectorResponseDTO();
        dto.setIdParametroSector(entity.getIdParametroSector());
        dto.setNombreRatio(entity.getNombreRatio());
        dto.setValorReferencia(entity.getValorReferencia());
        dto.setFuente(entity.getFuente());
        dto.setAnioReferencia(entity.getAnioReferencia());

        if (entity.getSector() != null) {
            ParametroSectorResponseDTO.SectorDTO sectorDTO = new ParametroSectorResponseDTO.SectorDTO();
            sectorDTO.setId_sector(entity.getSector().getIdSector()); // Asume que Sector tiene getId_sector()
            sectorDTO.setNombre_sector(entity.getSector().getNombreSector()); // Asume que Sector tiene getNombre_sector()
            dto.setSector(sectorDTO);
        }

        if (entity.getCategoriaRatio() != null) {
            ParametroSectorResponseDTO.CategoriaRatioDTO categoriaDTO = new ParametroSectorResponseDTO.CategoriaRatioDTO();
            categoriaDTO.setId_categoria_ratio(entity.getCategoriaRatio().getIdCategoriaRatio()); // Asume que CategoriaRatio tiene getId_categoria_ratio()
            categoriaDTO.setNombre_categoria(entity.getCategoriaRatio().getNombreTipo()); // Asume que CategoriaRatio tiene getNombre_categoria()
            dto.setCategoriaRatio(categoriaDTO);
        }

        return dto;
    }
}
