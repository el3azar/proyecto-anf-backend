package com.anf.proyecto.backend.modules.estadofinanciero.service;

import com.anf.proyecto.backend.exception.NotFoundException;
import com.anf.proyecto.backend.modules.catalogo.entity.Cuenta;
import com.anf.proyecto.backend.modules.catalogo.repository.CuentaRepository;
import com.anf.proyecto.backend.modules.empresa.entity.Empresa;
import com.anf.proyecto.backend.modules.empresa.repository.EmpresaRepository;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroRequestDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.EstadoFinanciero;
import com.anf.proyecto.backend.modules.estadofinanciero.entity.LineaEstadoFinanciero;
import com.anf.proyecto.backend.modules.estadofinanciero.repository.EstadoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.anf.proyecto.backend.exception.BadRequestException;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.LineaEstadoFinancieroDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.anf.proyecto.backend.modules.estadofinanciero.dto.EstadoFinancieroResponseDTO;
import com.anf.proyecto.backend.modules.estadofinanciero.dto.LineaEstadoFinancieroResponseDTO;
@Service
public class EstadoFinancieroService {

    @Autowired
    private EstadoFinancieroRepository estadoFinancieroRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional
    public void saveEstadoFinanciero(EstadoFinancieroRequestDTO requestDTO) {
        // 1. Validar que la empresa exista
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        // 2. Crear y guardar el encabezado (EstadoFinanciero)
        EstadoFinanciero estadoFinanciero = new EstadoFinanciero();
        estadoFinanciero.setEmpresa(empresa);
        estadoFinanciero.setAnio(requestDTO.getAnio());
        estadoFinanciero.setTipoReporte(requestDTO.getTipoReporte());

        List<LineaEstadoFinanciero> lineas = new ArrayList<>();

        // 3. Iterar sobre las líneas del DTO y crear las entidades de detalle
        for (var lineaDto : requestDTO.getLineas()) {
            // Validar que la cuenta del catálogo exista
            Cuenta cuenta = cuentaRepository.findById(lineaDto.getCuentaId())
                    .orElseThrow(() -> new NotFoundException("Cuenta no encontrada con id: " + lineaDto.getCuentaId()));

            LineaEstadoFinanciero linea = new LineaEstadoFinanciero();
            linea.setCuenta(cuenta);
            linea.setSaldo(lineaDto.getSaldo());
            linea.setEstadoFinanciero(estadoFinanciero); // Vincular la línea al encabezado
            lineas.add(linea);
        }

        // 4. Asignar la lista de líneas al encabezado y guardar
        // Gracias a `cascade = CascadeType.ALL`, al guardar el encabezado se guardarán todas las líneas
        estadoFinanciero.setLineas(lineas);
        estadoFinancieroRepository.save(estadoFinanciero);
    }

    // ¡NUEVO MÉTODO COMPLETO!
    @Transactional
    public void saveFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // --- 1. Leer la Hoja de Metadatos ---
            Sheet metadataSheet = workbook.getSheet("metadata");
            if (metadataSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'metadata'.");
            }

            Integer empresaId = (int) metadataSheet.getRow(1).getCell(1).getNumericCellValue();
            int anio = (int) metadataSheet.getRow(2).getCell(1).getNumericCellValue();
            String tipoReporte = metadataSheet.getRow(3).getCell(1).getStringCellValue();

            // --- 2. Leer la Hoja de Líneas ---
            Sheet lineasSheet = workbook.getSheet("lineas");
            if (lineasSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'lineas'.");
            }

            List<LineaEstadoFinancieroDTO> lineasDto = new ArrayList<>();
            Iterator<Row> rowIterator = lineasSheet.iterator();

            // Omitir la fila del encabezado
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell codigoCell = row.getCell(0);
                Cell saldoCell = row.getCell(1);

                if (codigoCell == null || saldoCell == null) continue;

                String codigoCuenta = codigoCell.getStringCellValue();
                BigDecimal saldo = new BigDecimal(saldoCell.getNumericCellValue());

                // Encontrar la cuenta por su código
                Cuenta cuenta = cuentaRepository.findByCodigoCuenta(codigoCuenta)
                        .orElseThrow(() -> new NotFoundException("No se encontró una cuenta con el código: " + codigoCuenta));

                LineaEstadoFinancieroDTO lineaDto = new LineaEstadoFinancieroDTO();
                lineaDto.setCuentaId(cuenta.getCuentaId()); // Usamos el ID de la cuenta encontrada
                lineaDto.setSaldo(saldo);
                lineasDto.add(lineaDto);
            }

            // --- 3. Construir el DTO y reutilizar la lógica existente ---
            if (lineasDto.isEmpty()) {
                throw new BadRequestException("La hoja 'lineas' no contiene datos válidos.");
            }

            EstadoFinancieroRequestDTO requestDTO = new EstadoFinancieroRequestDTO();
            requestDTO.setEmpresaId(empresaId);
            requestDTO.setAnio(anio);
            requestDTO.setTipoReporte(tipoReporte);
            requestDTO.setLineas(lineasDto);

            // Reutilizamos el método que ya habíamos creado para guardar desde un DTO
            this.saveEstadoFinanciero(requestDTO);

        } catch (Exception e) {
            // Lanza una excepción más genérica para errores de lectura o formato
            throw new RuntimeException("Error al procesar el archivo de Excel: " + e.getMessage());
        }
    }
    public EstadoFinancieroResponseDTO getEstadoFinancieroById(Long id) {
        EstadoFinanciero estadoFinanciero = estadoFinancieroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estado Financiero no encontrado con id: " + id));
        return mapToResponseDTO(estadoFinanciero);
    }

    // ¡NUEVO MÉTODO PARA LEER TODOS! (Podría ser pesado, se puede mejorar con paginación en el futuro)
    public List<EstadoFinancieroResponseDTO> getAllEstadosFinancieros() {
        return estadoFinancieroRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // ¡NUEVO MÉTODO DE MAPEADO!
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
}