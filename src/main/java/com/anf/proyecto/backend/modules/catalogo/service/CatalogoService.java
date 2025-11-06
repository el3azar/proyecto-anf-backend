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

    // Método para obtener todas las cuentas activas de una empresa específica
    public List<Catalogo> getCatalogoByEmpresa(Integer empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }
        return catalogoRepository.findByEmpresa_EmpresaIdAndActivo(empresaId, true);
    }

    @Transactional
    public void activarCuentas(ActivacionRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        List<Catalogo> catalogosParaGuardar = new ArrayList<>();

        for (Integer cuentaId : requestDTO.getCuentaIds()) {
            // 1. Verificar que la cuenta maestra exista
            Cuenta cuenta = cuentaRepository.findById(cuentaId)
                    .orElseThrow(() -> new NotFoundException("Cuenta maestra no encontrada con id: " + cuentaId));

            // 2. Verificar si ya existe una entrada para esta empresa y cuenta
            catalogoRepository.findByEmpresa_EmpresaIdAndCuenta_CuentaId(empresa.getEmpresaId(), cuenta.getCuentaId())
                    .ifPresentOrElse(
                            // Si ya existe, la reactivamos si estaba inactiva
                            catalogoExistente -> {
                                if (!catalogoExistente.getActivo()) {
                                    catalogoExistente.setActivo(true);
                                    catalogosParaGuardar.add(catalogoExistente);
                                }
                            },
                            // Si no existe, creamos una nueva entrada
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

    public List<CatalogoResponseDTO> getCatalogoActivoPorEmpresa(Integer empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }

        List<Catalogo> catalogoActivo = catalogoRepository.findByEmpresa_EmpresaIdAndActivo(empresaId, true);

        return catalogoActivo.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private CatalogoResponseDTO mapToResponseDTO(Catalogo catalogo) {
        CatalogoResponseDTO dto = new CatalogoResponseDTO();

        // Datos del registro 'Catalogo', usando getters camelCase
        dto.setIdCatalogo(catalogo.getIdCatalogo());
        dto.setActivo(catalogo.getActivo());
        dto.setEmpresaId(catalogo.getEmpresa().getEmpresaId());

        // Datos de la 'Cuenta' maestra asociada, usando getters camelCase
        Cuenta cuenta = catalogo.getCuenta();
        if (cuenta != null) {
            dto.setCuentaId(cuenta.getCuentaId());
            dto.setCodigoCuenta(cuenta.getCodigoCuenta());
            dto.setNombreCuenta(cuenta.getNombreCuenta());
            dto.setTipoCuenta(cuenta.getTipoCuenta());
            dto.setEsMovimiento(cuenta.isEsMovimiento()); // Asegúrate de que este campo exista y se llame así en tu entidad Cuenta
        }

        return dto;
    }

    @Transactional
    public void desactivarCuentas(DesactivacionRequestDTO requestDTO) {
        // Buscamos todos los registros del catálogo que coincidan con los IDs proporcionados.
        List<Catalogo> catalogosParaDesactivar = catalogoRepository.findAllById(requestDTO.getCatalogoIds());

        if (catalogosParaDesactivar.isEmpty()) {
            // Opcional: puedes lanzar una excepción si no se encontró ninguno de los IDs.
            return;
        }

        // Cambiamos el estado de cada uno a 'false'.
        for (Catalogo catalogo : catalogosParaDesactivar) {
            catalogo.setActivo(false);
        }

        // Guardamos todos los cambios en una sola transacción.
        catalogoRepository.saveAll(catalogosParaDesactivar);
    }
}