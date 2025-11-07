package com.anf.proyecto.backend.modules.empresa.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.empresa.dto.SectorDTO;
import com.anf.proyecto.backend.modules.empresa.dto.sector.SectorSaveDTO;
import com.anf.proyecto.backend.modules.empresa.dto.sector.SectorUpdateDTO;
import com.anf.proyecto.backend.modules.empresa.entity.Sector;
import com.anf.proyecto.backend.modules.empresa.repository.SectorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<SectorDTO> getAllSectores() {
        return sectorRepository.findAll()
                .stream()
                .map(sector -> modelMapper.map(sector, SectorDTO.class))
                .collect(Collectors.toList());
    }

    public SectorDTO getSectorById(Integer id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sector no encontrado con id: " + id));
        return modelMapper.map(sector, SectorDTO.class);
    }

    public SectorDTO createSector(SectorSaveDTO saveDTO) {
        if (sectorRepository.existsByNombreSector(saveDTO.getNombreSector())) {
            throw new BusinessRuleException("Ya existe un sector con el nombre: " + saveDTO.getNombreSector());
        }

        Sector sector = modelMapper.map(saveDTO, Sector.class);
        Sector savedSector = sectorRepository.save(sector);
        return modelMapper.map(savedSector, SectorDTO.class);
    }

    public SectorDTO updateSector(Integer id, SectorUpdateDTO updateDTO) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sector no encontrado con id: " + id));

        // Validación para evitar que el nuevo nombre colisione con otro sector existente
        sectorRepository.findByNombreSector(updateDTO.getNombreSector()).ifPresent(s -> {
            if (!s.getIdSector().equals(id)) {
                throw new BusinessRuleException("El nombre '" + updateDTO.getNombreSector() + "' ya está en uso por otro sector.");
            }
        });

        // Mapea los campos del DTO a la entidad existente
        sector.setNombreSector(updateDTO.getNombreSector());
        sector.setDescripcion(updateDTO.getDescripcion());
        sector.setPaisReferencia(updateDTO.getPaisReferencia());
        sector.setFuenteDatos(updateDTO.getFuenteDatos());

        Sector updatedSector = sectorRepository.save(sector);
        return modelMapper.map(updatedSector, SectorDTO.class);
    }

    public void deleteSector(Integer id) {
        if (!sectorRepository.existsById(id)) {
            throw new NotFoundException("Sector no encontrado con id: " + id);
        }
        sectorRepository.deleteById(id);
    }
}