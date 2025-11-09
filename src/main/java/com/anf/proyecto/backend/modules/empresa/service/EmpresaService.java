package com.anf.proyecto.backend.modules.empresa.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.empresa.dto.EmpresaRequestDTO;
import com.anf.proyecto.backend.modules.empresa.dto.EmpresaResponseDTO;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.entity.Sector;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;
import com.anf.proyecto.backend.modules.empresa.repository.SectorRepository;
import com.anf.proyecto.backend.modules.usuario.entity.Usuario;
import com.anf.proyecto.backend.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public EmpresaResponseDTO createEmpresa(EmpresaRequestDTO requestDTO) {
        // CORREGIDO: Usa los getters camelCase
        if (empresaRepository.existsByNombreEmpresa(requestDTO.getNombreEmpresa())) {
            throw new BusinessRuleException("Ya existe una empresa con el nombre: " + requestDTO.getNombreEmpresa());
        }
        if (requestDTO.getEmpresaNit() != null && empresaRepository.existsByEmpresaNit(requestDTO.getEmpresaNit())) {
            throw new BusinessRuleException("Ya existe una empresa con el NIT: " + requestDTO.getEmpresaNit());
        }

        Sector sector = sectorRepository.findById(requestDTO.getIdSector())
                .orElseThrow(() -> new NotFoundException("Sector no encontrado con id: " + requestDTO.getIdSector()));

        Usuario usuario = null;
        if (requestDTO.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + requestDTO.getUsuarioId()));
        }

        Empresa empresa = new Empresa();
        empresa.setNombreEmpresa(requestDTO.getNombreEmpresa());
        empresa.setEmpresaDui(requestDTO.getEmpresaDui());
        empresa.setEmpresaNit(requestDTO.getEmpresaNit());
        empresa.setEmpresaNrc(requestDTO.getEmpresaNrc());
        empresa.setSector(sector);
        empresa.setUsuario(usuario);

        Empresa savedEmpresa = empresaRepository.save(empresa);
        return mapToResponseDTO(savedEmpresa);
    }

    @Transactional(readOnly = true) // Mantiene la sesión abierta para permitir la carga LAZY
    public List<EmpresaResponseDTO> getAllEmpresas() {
        return empresaRepository.findAll().stream()
                .map(this::mapToResponseDTO) // Ahora getUsuario() funcionará aquí
                .collect(Collectors.toList());
    }

    // --- SOLUCIÓN APLICADA AQUÍ ---
    @Transactional(readOnly = true) // Mantiene la sesión abierta para permitir la carga LAZY
    public EmpresaResponseDTO getEmpresaById(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + id));
        return mapToResponseDTO(empresa); // Ahora getUsuario() funcionará aquí
    }

    @Transactional
    public EmpresaResponseDTO updateEmpresa(Integer id, EmpresaRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + id));

        empresaRepository.findByNombreEmpresa(requestDTO.getNombreEmpresa()).ifPresent(e -> {
            if (!e.getEmpresaId().equals(id)) {
                throw new BusinessRuleException("El nombre '" + requestDTO.getNombreEmpresa() + "' ya está en uso por otra empresa.");
            }
        });

        empresa.setNombreEmpresa(requestDTO.getNombreEmpresa());
        empresa.setEmpresaDui(requestDTO.getEmpresaDui());
        empresa.setEmpresaNit(requestDTO.getEmpresaNit());
        empresa.setEmpresaNrc(requestDTO.getEmpresaNrc());

        Empresa updatedEmpresa = empresaRepository.save(empresa);
        return mapToResponseDTO(updatedEmpresa);
    }

    @Transactional // Para operaciones de borrado
    public void deleteEmpresa(Integer id) {
        if (!empresaRepository.existsById(id)) {
            throw new NotFoundException("Empresa no encontrada con id: " + id);
        }
        empresaRepository.deleteById(id);
    }

    // CORREGIDO: Usa los setters y getters camelCase
    private EmpresaResponseDTO mapToResponseDTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setEmpresaId(empresa.getEmpresaId());
        dto.setNombreEmpresa(empresa.getNombreEmpresa());
        dto.setEmpresaDui(empresa.getEmpresaDui());
        dto.setEmpresaNit(empresa.getEmpresaNit());
        dto.setEmpresaNrc(empresa.getEmpresaNrc());
        if (empresa.getUsuario() != null) {
            dto.setUsuarioId(empresa.getUsuario().getUsuarioId());
        }
        if (empresa.getSector() != null) {
            dto.setIdSector(empresa.getSector().getIdSector());
            dto.setNombreSector(empresa.getSector().getNombreSector());
        }
        return dto;
    }
}