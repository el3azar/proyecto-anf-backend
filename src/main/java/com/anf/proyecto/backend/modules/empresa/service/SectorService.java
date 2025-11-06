package com.anf.proyecto.backend.modules.empresa.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.empresa.dto.SectorDTO;
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

    public SectorDTO createSector(SectorDTO sectorDTO) {
        // CORREGIDO: Usa el getter camelCase
        if (sectorRepository.existsByNombreSector(sectorDTO.getNombreSector())) {
            throw new BusinessRuleException("Ya existe un sector con el nombre: " + sectorDTO.getNombreSector());
        }

        Sector sector = modelMapper.map(sectorDTO, Sector.class);
        Sector savedSector = sectorRepository.save(sector);
        return modelMapper.map(savedSector, SectorDTO.class);
    }

    public SectorDTO updateSector(Integer id, SectorDTO sectorDTO) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sector no encontrado con id: " + id));

        // CORREGIDO: Usa los getters camelCase
        sectorRepository.findByNombreSector(sectorDTO.getNombreSector()).ifPresent(s -> {
            if (!s.getIdSector().equals(id)) {
                throw new BusinessRuleException("El nombre '" + sectorDTO.getNombreSector() + "' ya está en uso por otro sector.");
            }
        });

        // CORREGIDO: Usa los getters y setters camelCase
        sector.setNombreSector(sectorDTO.getNombreSector());
        sector.setDescripcion(sectorDTO.getDescripcion());
        sector.setPaisReferencia(sectorDTO.getPaisReferencia());
        sector.setFuenteDatos(sectorDTO.getFuenteDatos());

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