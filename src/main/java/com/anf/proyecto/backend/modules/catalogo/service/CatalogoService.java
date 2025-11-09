package com.anf.proyecto.backend.modules.catalogo.service;

import com.anf.proyecto.backend.exception.BusinessRuleException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.catalogo.dto.ActivacionRequestDTO;
import com.anf.proyecto.backend.modules.catalogo.dto.CatalogoResponseDTO;
import com.anf.proyecto.backend.modules.catalogo.dto.DesactivacionRequestDTO;
import com.anf.proyecto.backend.modules.catalogo.entity.Catalogo;
import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import com.anf.proyecto.backend.modules.catalogo.repository.CatalogoRepository;
import com.anf.proyecto.backend.modules.catalogo.repository.CuentaRepository;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository catalogoRepository;
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    // --- SOLUCIÓN APLICADA AQUÍ ---
    @Transactional(readOnly = true)
    public List<CatalogoResponseDTO> getCatalogoActivoPorEmpresa(Integer empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }

        List<Catalogo> catalogoActivo = catalogoRepository.findByEmpresa_EmpresaIdAndActivo(empresaId, true);

        return catalogoActivo.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void activarCuentas(ActivacionRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        List<Catalogo> catalogosParaGuardar = new ArrayList<>();

        for (Integer cuentaId : requestDTO.getCuentaIds()) {
            Cuenta cuenta = cuentaRepository.findById(cuentaId)
                    .orElseThrow(() -> new NotFoundException("Cuenta maestra no encontrada con id: " + cuentaId));

            catalogoRepository.findByEmpresa_EmpresaIdAndCuenta_CuentaId(empresa.getEmpresaId(), cuenta.getCuentaId())
                    .ifPresentOrElse(
                            catalogoExistente -> {
                                if (!catalogoExistente.getActivo()) {
                                    catalogoExistente.setActivo(true);
                                    catalogosParaGuardar.add(catalogoExistente);
                                }
                            },
                            () -> {
                                Catalogo nuevoCatalogo = new Catalogo();
                                nuevoCatalogo.setEmpresa(empresa);
                                nuevoCatalogo.setCuenta(cuenta);
                                nuevoCatalogo.setActivo(true);
                                catalogosParaGuardar.add(nuevoCatalogo);
                            }
                    );
        }

        if (!catalogosParaGuardar.isEmpty()) {
            catalogoRepository.saveAll(catalogosParaGuardar);
        }
    }

    @Transactional
    public void desactivarCuentas(DesactivacionRequestDTO requestDTO) {
        List<Catalogo> catalogosParaDesactivar = catalogoRepository.findAllById(requestDTO.getCatalogoIds());

        if (catalogosParaDesactivar.isEmpty()) {
            return;
        }

        for (Catalogo catalogo : catalogosParaDesactivar) {
            catalogo.setActivo(false);
        }

        catalogoRepository.saveAll(catalogosParaDesactivar);
    }

    private CatalogoResponseDTO mapToResponseDTO(Catalogo catalogo) {
        CatalogoResponseDTO dto = new CatalogoResponseDTO();
        dto.setIdCatalogo(catalogo.getIdCatalogo());
        dto.setActivo(catalogo.getActivo());
        dto.setEmpresaId(catalogo.getEmpresa().getEmpresaId());

        Cuenta cuenta = catalogo.getCuenta();
        if (cuenta != null) {
            dto.setCuentaId(cuenta.getCuentaId());
            dto.setCodigoCuenta(cuenta.getCodigoCuenta());
            dto.setNombreCuenta(cuenta.getNombreCuenta());
            dto.setTipoCuenta(cuenta.getTipoCuenta());
            dto.setEsMovimiento(cuenta.isEsMovimiento());
        }

        return dto;
    }
}