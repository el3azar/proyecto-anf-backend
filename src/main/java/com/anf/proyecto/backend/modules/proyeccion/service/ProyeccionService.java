package com.anf.proyecto.backend.modules.proyeccion.service;

import com.anf.proyecto.backend.exception.BadRequestException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;
import com.anf.proyecto.backend.modules.proyeccion.dto.VentaHistoricaDTO;
import com.anf.proyecto.backend.modules.proyeccion.dto.VentasRequestDTO;
import com.anf.proyecto.backend.modules.proyeccion.entity.VentaHistorica;
import com.anf.proyecto.backend.modules.proyeccion.repository.VentaHistoricaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProyeccionService {

    @Autowired
    private VentaHistoricaRepository ventaHistoricaRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private ModelMapper modelMapper;

    // --- MÉTODOS DE CREACIÓN ---

    @Transactional
    public void saveVentasManualmente(VentasRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        List<VentaHistorica> ventasParaGuardar = new ArrayList<>();

        for (VentaHistoricaDTO ventaDto : requestDTO.getVentas()) {
            VentaHistorica venta = new VentaHistorica();
            venta.setEmpresa(empresa);
            venta.setFechaVenta(ventaDto.getFechaVenta());
            venta.setMontoVenta(ventaDto.getMontoVenta());
            venta.setObservacion(ventaDto.getObservacion());
            ventasParaGuardar.add(venta);
        }

        if (ventasParaGuardar.isEmpty()) {
            throw new BadRequestException("La lista de ventas no puede estar vacía.");
        }

        ventaHistoricaRepository.saveAll(ventasParaGuardar);
    }

    @Transactional
    public void saveVentasFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // --- LECTURA DE METADATOS MEJORADA Y ROBUSTA ---
            Sheet metadataSheet = workbook.getSheet("metadata");
            if (metadataSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'metadata'.");
            }

            Integer empresaId = null;

            // Fila 1 (segunda fila) para empresaId
            Row empresaIdRow = metadataSheet.getRow(1);
            if (empresaIdRow != null) {
                Cell empresaIdCell = empresaIdRow.getCell(1);
                if (empresaIdCell != null && empresaIdCell.getCellType() == CellType.NUMERIC) {
                    empresaId = (int) empresaIdCell.getNumericCellValue();
                }
            }

            if (empresaId == null) {
                throw new BadRequestException("El 'empresaId' no se encontró o tiene un formato incorrecto en la celda B2 de la hoja 'metadata'.");
            }

            // Creamos una nueva variable final para usarla dentro de la lambda.
            final Integer finalEmpresaId = empresaId;

            Empresa empresa = empresaRepository.findById(finalEmpresaId) // Usamos la variable final
                    .orElseThrow(() -> new NotFoundException("La empresa con id " + finalEmpresaId + " especificada en el Excel no fue encontrada.")); // Y la usamos aquí también
            
            // --- LECTURA DE VENTAS (SIN CAMBIOS, YA ERA ROBUSTA) ---
            Sheet ventasSheet = workbook.getSheet("ventas");
            if (ventasSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'ventas'.");
            }

            List<VentaHistorica> ventasParaGuardar = new ArrayList<>();
            Iterator<Row> rowIterator = ventasSheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Omitir encabezado
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell fechaCell = row.getCell(0);
                Cell montoCell = row.getCell(1);
                Cell observacionCell = row.getCell(2);

                if (fechaCell == null || montoCell == null) continue;

                // Añadimos verificación de tipo de celda para robustez
                if (fechaCell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(fechaCell)) {
                    continue; // O lanza un error si la fecha no tiene el formato correcto
                }
                LocalDate fechaVenta = fechaCell.getLocalDateTimeCellValue().toLocalDate();

                if (montoCell.getCellType() != CellType.NUMERIC) {
                    continue; // O lanza un error si el monto no es numérico
                }
                BigDecimal montoVenta = new BigDecimal(montoCell.getNumericCellValue());

                String observacion = (observacionCell != null && observacionCell.getCellType() == CellType.STRING)
                        ? observacionCell.getStringCellValue() : null;

                VentaHistorica venta = new VentaHistorica();
                venta.setEmpresa(empresa);
                venta.setFechaVenta(fechaVenta);
                venta.setMontoVenta(montoVenta);
                venta.setObservacion(observacion);
                ventasParaGuardar.add(venta);
            }

            if (ventasParaGuardar.isEmpty()) {
                throw new BadRequestException("La hoja 'ventas' no contiene datos válidos o está vacía.");
            }

            ventaHistoricaRepository.saveAll(ventasParaGuardar);

        } catch (Exception e) {
            // Capturamos la excepción original para dar un mensaje de error más claro si es necesario
            throw new RuntimeException("Error al procesar el archivo de Excel de ventas: " + e.getMessage(), e);
        }
    }

    // --- MÉTODO DE LECTURA CORREGIDO ---
    @Transactional(readOnly = true)
    public List<VentaHistoricaDTO> getVentasByEmpresaAndAnio(Integer empresaId, int anio) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }

        // Creamos el rango de fechas para el año solicitado
        LocalDate startDate = LocalDate.of(anio, 1, 1);
        LocalDate endDate = LocalDate.of(anio, 12, 31);

        List<VentaHistorica> ventas = ventaHistoricaRepository.findByEmpresa_EmpresaIdAndFechaVentaBetween(empresaId, startDate, endDate);

        return ventas.stream()
                .map(venta -> modelMapper.map(venta, VentaHistoricaDTO.class))
                .collect(Collectors.toList());
    }

    // --- MÉTODO DE ELIMINACIÓN CORREGIDO ---
    // (Añadimos este método si queremos borrar por año)
    @Transactional
    public void deleteVentasByAnio(Integer empresaId, int anio) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }

        LocalDate startDate = LocalDate.of(anio, 1, 1);
        LocalDate endDate = LocalDate.of(anio, 12, 31);

        ventaHistoricaRepository.deleteByEmpresa_EmpresaIdAndFechaVentaBetween(empresaId, startDate, endDate);
    }

    // --- MÉTODOS DE LECTURA ---

    @Transactional(readOnly = true)
    public List<VentaHistoricaDTO> getVentasByEmpresa(Integer empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }
        List<VentaHistorica> ventas = ventaHistoricaRepository.findByEmpresa_EmpresaIdOrderByFechaVentaAsc(empresaId);
        return ventas.stream()
                .map(venta -> modelMapper.map(venta, VentaHistoricaDTO.class))
                .collect(Collectors.toList());
    }

    // --- MÉTODO DE ACTUALIZACIÓN ---

    @Transactional
    public VentaHistoricaDTO updateVenta(Long ventaId, VentaHistoricaDTO ventaDTO) {
        VentaHistorica venta = ventaHistoricaRepository.findById(ventaId)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada con id: " + ventaId));

        venta.setFechaVenta(ventaDTO.getFechaVenta());
        venta.setMontoVenta(ventaDTO.getMontoVenta());
        venta.setObservacion(ventaDTO.getObservacion());

        VentaHistorica updatedVenta = ventaHistoricaRepository.save(venta);
        return modelMapper.map(updatedVenta, VentaHistoricaDTO.class);
    }

    // --- MÉTODO DE ELIMINACIÓN ---

    @Transactional
    public void deleteVenta(Long ventaId) {
        if (!ventaHistoricaRepository.existsById(ventaId)) {
            throw new NotFoundException("Venta no encontrada con id: " + ventaId);
        }
        ventaHistoricaRepository.deleteById(ventaId);
    }


    // --- MÉTODO DE CÁLCULO (PARA EL FUTURO) ---
    public Object calcularProyecciones(Integer empresaId, int anio) {
        // ... (lógica futura)
        return "Lógica de proyección pendiente de implementación.";
    }
}