package com.anf.proyecto.backend.modules.estadofinanciero.service;

import com.anf.proyecto.backend.exception.BadRequestException;
import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.catalogo.dto.SaldoCuentaAnioDTO;
import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import com.anf.proyecto.backend.modules.catalogo.repository.CuentaRepository;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroRequestDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroResponseDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.LineaEstadoFinancieroDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.LineaEstadoFinancieroResponseDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.EstadoFinanciero;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.EstadoFinancieroRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.LineaEstadoFinancieroRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoFinancieroService {

    @Autowired
    private EstadoFinancieroRepository estadoFinancieroRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired 
    private LineaEstadoFinancieroRepository lineaRepo;

    @Transactional
    public void saveEstadoFinanciero(EstadoFinancieroRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        EstadoFinanciero estadoFinanciero = new EstadoFinanciero();
        estadoFinanciero.setEmpresa(empresa);
        estadoFinanciero.setAnio(requestDTO.getAnio());
        estadoFinanciero.setTipoReporte(requestDTO.getTipoReporte());

        List<LineaEstadoFinanciero> lineas = new ArrayList<>();

        for (var lineaDto : requestDTO.getLineas()) {
            Cuenta cuenta = cuentaRepository.findById(lineaDto.getCuentaId())
                    .orElseThrow(() -> new NotFoundException("Cuenta no encontrada con id: " + lineaDto.getCuentaId()));

            LineaEstadoFinanciero linea = new LineaEstadoFinanciero();
            linea.setCuenta(cuenta);
            linea.setSaldo(lineaDto.getSaldo());
            linea.setEstadoFinanciero(estadoFinanciero);
            lineas.add(linea);
        }

        estadoFinanciero.setLineas(lineas);
        estadoFinancieroRepository.save(estadoFinanciero);
    }
    @Transactional(readOnly = true)
    public List<SaldoCuentaAnioDTO> getSaldosPorNombreCuentaYNombreEmpresa(String nombreEmpresa, String nombreCuenta) {
        // Validación para evitar consultas innecesarias a la BD
        if (nombreEmpresa == null || nombreEmpresa.trim().isEmpty() || nombreCuenta == null || nombreCuenta.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return lineaRepo.findSaldosByNombreEmpresaAndNombreCuenta(nombreEmpresa, nombreCuenta);
    }


    @Transactional
    public void saveFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet metadataSheet = workbook.getSheet("metadata");
            if (metadataSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'metadata'.");
            }

            Integer empresaId = (int) metadataSheet.getRow(1).getCell(1).getNumericCellValue();
            int anio = (int) metadataSheet.getRow(2).getCell(1).getNumericCellValue();
            String tipoReporte = metadataSheet.getRow(3).getCell(1).getStringCellValue();

            Sheet lineasSheet = workbook.getSheet("lineas");
            if (lineasSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'lineas'.");
            }

            List<LineaEstadoFinancieroDTO> lineasDto = new ArrayList<>();
            Iterator<Row> rowIterator = lineasSheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next(); // Omitir la fila del encabezado
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell codigoCell = row.getCell(0);
                Cell saldoCell = row.getCell(1);

                if (codigoCell == null || saldoCell == null) continue;

                // --- ¡SOLUCIÓN APLICADA AQUÍ! ---
                String codigoCuenta;
                switch (codigoCell.getCellType()) {
                    case STRING:
                        codigoCuenta = codigoCell.getStringCellValue();
                        break;
                    case NUMERIC:
                        codigoCuenta = String.valueOf((long) codigoCell.getNumericCellValue());
                        break;
                    default:
                        continue;
                }

                BigDecimal saldo = new BigDecimal(saldoCell.getNumericCellValue());

                Cuenta cuenta = cuentaRepository.findByCodigoCuenta(codigoCuenta)
                        .orElseThrow(() -> new NotFoundException("No se encontró una cuenta con el código: " + codigoCuenta));

                LineaEstadoFinancieroDTO lineaDto = new LineaEstadoFinancieroDTO();
                lineaDto.setCuentaId(cuenta.getCuentaId());
                lineaDto.setSaldo(saldo);
                lineasDto.add(lineaDto);
            }

            if (lineasDto.isEmpty()) {
                throw new BadRequestException("La hoja 'lineas' no contiene datos válidos.");
            }

            EstadoFinancieroRequestDTO requestDTO = new EstadoFinancieroRequestDTO();
            requestDTO.setEmpresaId(empresaId);
            requestDTO.setAnio(anio);
            requestDTO.setTipoReporte(tipoReporte);
            requestDTO.setLineas(lineasDto);

            this.saveEstadoFinanciero(requestDTO);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo de Excel: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public EstadoFinancieroResponseDTO getEstadoFinancieroById(Integer id) {
        EstadoFinanciero estadoFinanciero = estadoFinancieroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estado Financiero no encontrado con id: " + id));
        return mapToResponseDTO(estadoFinanciero);
    }

    @Transactional(readOnly = true)
    public List<EstadoFinancieroResponseDTO> getAllEstadosFinancieros() {
        return estadoFinancieroRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteEstadoFinanciero(Integer id) {
        // Primero, verificamos que el registro exista para poder dar un error 404 claro si no se encuentra.
        if (!estadoFinancieroRepository.existsById(id)) {
            throw new NotFoundException("Estado Financiero no encontrado con id: " + id);
        }

        // Si existe, lo eliminamos.
        // La eliminación en cascada (cascade = CascadeType.ALL) se encargará de borrar
        // también todas las 'LineaEstadoFinanciero' asociadas automáticamente.
        estadoFinancieroRepository.deleteById(id);
    }
    private EstadoFinancieroResponseDTO mapToResponseDTO(EstadoFinanciero estadoFinanciero) {
        EstadoFinancieroResponseDTO dto = new EstadoFinancieroResponseDTO();
        dto.setId(estadoFinanciero.getId());
        dto.setAnio(estadoFinanciero.getAnio());
        dto.setTipoReporte(estadoFinanciero.getTipoReporte());

        if (estadoFinanciero.getEmpresa() != null) {
            dto.setEmpresaId(estadoFinanciero.getEmpresa().getEmpresaId());
            dto.setNombreEmpresa(estadoFinanciero.getEmpresa().getNombreEmpresa());
        }

        if (estadoFinanciero.getLineas() != null) {
            List<LineaEstadoFinancieroResponseDTO> lineasDto = estadoFinanciero.getLineas().stream()
                    .map(linea -> {
                        LineaEstadoFinancieroResponseDTO lineaDto = new LineaEstadoFinancieroResponseDTO();
                        lineaDto.setId(linea.getId());
                        lineaDto.setSaldo(linea.getSaldo());
                        if (linea.getCuenta() != null) {
                            lineaDto.setCuentaId(linea.getCuenta().getCuentaId());
                            lineaDto.setCodigoCuenta(linea.getCuenta().getCodigoCuenta());
                            lineaDto.setNombreCuenta(linea.getCuenta().getNombreCuenta());
                        }
                        return lineaDto;
                    }).collect(Collectors.toList());
            dto.setLineas(lineasDto);
        }

        return dto;
    }

    public List<Integer> getAniosByEmpresa(Long empresaId) {
        return lineaRepo.findDistinctAniosByEmpresaId(empresaId);
    }

}