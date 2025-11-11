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
import com.anf.proyecto.backend.modules.proyeccion.dto.ProyeccionDTO;
import com.anf.proyecto.backend.modules.proyeccion.enums.MetodoProyeccion;
import java.util.Map;

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


    public ProyeccionService(EmpresaRepository empresaRepository, VentaHistoricaRepository ventaHistoricaRepository) {
        this.empresaRepository = empresaRepository;
        this.ventaHistoricaRepository = ventaHistoricaRepository;
    }


    // --- MÉTODOS DE CREACIÓN ---

    @Transactional
    public void saveVentasManualmente(VentasRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada con id: " + requestDTO.getEmpresaId()));

        List<VentaHistorica> ventasParaGuardar = new ArrayList<>();

        for (VentaHistoricaDTO ventaDto : requestDTO.getVentas()) {
            LocalDate fecha = ventaDto.getFechaVenta();
            int mes = fecha.getMonthValue();
            int anio = fecha.getYear();

            boolean existe = ventaHistoricaRepository.existsByMesAndAnioAndEmpresa(mes, anio, empresa.getEmpresaId());

            if (existe) {
                throw new BadRequestException("Ya existe una venta registrada para " +
                        fecha.getMonth() + " de " + anio + " en esta empresa.");
            }

            VentaHistorica venta = new VentaHistorica();
            venta.setEmpresa(empresa);
            venta.setFechaVenta(fecha);
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

            // --- LECTURA DE METADATOS ---
            Sheet metadataSheet = workbook.getSheet("metadata");
            if (metadataSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'metadata'.");
            }

            Integer empresaId = null;
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

            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new NotFoundException("La empresa con id " + " especificada en el Excel no fue encontrada."));

            // --- LECTURA DE VENTAS ---
            Sheet ventasSheet = workbook.getSheet("ventas");
            if (ventasSheet == null) {
                throw new BadRequestException("El archivo de Excel debe contener una hoja llamada 'ventas'.");
            }

            List<VentaHistorica> ventasParaGuardar = new ArrayList<>();
            Iterator<Row> rowIterator = ventasSheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // Omitir encabezado

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell fechaCell = row.getCell(0);
                Cell montoCell = row.getCell(1);
                Cell observacionCell = row.getCell(2);

                if (fechaCell == null || montoCell == null) continue;

                if (fechaCell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(fechaCell)) {
                    continue;
                }

                LocalDate fechaVenta = fechaCell.getLocalDateTimeCellValue().toLocalDate();
                int mes = fechaVenta.getMonthValue();
                int anio = fechaVenta.getYear();

                // Verificar duplicados
                boolean existe = ventaHistoricaRepository.existsByMesAndAnioAndEmpresa(mes, anio, empresa.getEmpresaId());
                if (existe) {
                    throw new BadRequestException("Ya existe una venta registrada para " +
                            fechaVenta.getMonth() + " de " + anio + " en esta empresa.");
                }

                if (montoCell.getCellType() != CellType.NUMERIC) continue;

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
    @Transactional
    public List<ProyeccionDTO> calcularProyeccion(Integer empresaId, int mesesFuturos, MetodoProyeccion metodo) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new NotFoundException("Empresa no encontrada con id: " + empresaId);
        }

        List<VentaHistorica> ventas = ventaHistoricaRepository
                .findByEmpresa_EmpresaIdOrderByFechaVentaAsc(empresaId);

        if (ventas.isEmpty()) {
            throw new NotFoundException("No hay ventas históricas registradas para esta empresa.");
        }

        // Validación nueva por cantidad de meses
        LocalDate fechaInicio = ventas.get(0).getFechaVenta();
        LocalDate fechaFin = ventas.get(ventas.size() - 1).getFechaVenta();

        long mesesDeDatos = java.time.temporal.ChronoUnit.MONTHS.between(
                fechaInicio.withDayOfMonth(1),
                fechaFin.withDayOfMonth(1)
        ) + 1;

        if (mesesDeDatos < 11) {
            throw new BadRequestException("Se necesitan al menos 11 meses de datos históricos para proyectar.");
        }

        // Agrupar por mes (YYYY-MM)
        Map<String, BigDecimal> ventasPorMes = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getFechaVenta().getYear() + "-" + String.format("%02d", v.getFechaVenta().getMonthValue()),
                        Collectors.reducing(BigDecimal.ZERO, VentaHistorica::getMontoVenta, BigDecimal::add)
                ));

        List<String> mesesHistoricos = ventasPorMes.keySet().stream().sorted().toList();
        List<BigDecimal> montos = mesesHistoricos.stream().map(ventasPorMes::get).toList();

        // --- Ajustar a 12 meses de datos históricos ---
        if (mesesHistoricos.size() > 12) {
            // Si hay más de 12, tomamos los últimos 12
            mesesHistoricos = mesesHistoricos.subList(mesesHistoricos.size() - 12, mesesHistoricos.size());

            montos = montos.subList(montos.size() - 12, montos.size());
        } else if (mesesHistoricos.size() == 11) {
            // Si hay solo 11 meses, generamos el mes 12 automáticamente
            String ultimoMes = mesesHistoricos.get(mesesHistoricos.size() - 1);
            int ultimoAnio = Integer.parseInt(ultimoMes.split("-")[0]);
            int ultimoMesNum = Integer.parseInt(ultimoMes.split("-")[1]);

            // Calcular incremento promedio (puede ser porcentual o absoluto simple)
            BigDecimal incrementoPromedio = BigDecimal.ZERO;
            for (int i = 1; i < montos.size(); i++) {
                incrementoPromedio = incrementoPromedio.add(montos.get(i).subtract(montos.get(i - 1)));
            }
            incrementoPromedio = incrementoPromedio.divide(BigDecimal.valueOf(montos.size() - 1), 2, BigDecimal.ROUND_HALF_UP);

            // Generar el nuevo mes
            ultimoMesNum++;
            if (ultimoMesNum > 12) {
                ultimoMesNum = 1;
                ultimoAnio++;
            }
            String nuevoMes = ultimoAnio + "-" + String.format("%02d", ultimoMesNum);
            BigDecimal nuevoMonto = montos.get(montos.size() - 1).add(incrementoPromedio);

            // Agregarlo a la lista
            mesesHistoricos = new ArrayList<>(mesesHistoricos);
            montos = new ArrayList<>(montos);
            mesesHistoricos.add(nuevoMes);
            montos.add(nuevoMonto);
        }

        switch (metodo) {
            case MINIMOS_CUADRADOS:
                return proyectarMinimosCuadrados(mesesHistoricos, montos, mesesFuturos);
            case INCREMENTO_PORCENTUAL:
                return proyectarIncrementoPorcentual(mesesHistoricos, montos, mesesFuturos);
            case INCREMENTO_ABSOLUTO:
                return proyectarIncrementoAbsoluto(mesesHistoricos, montos, mesesFuturos);
            default:
                throw new BadRequestException("Método de proyección no válido");
        }
    }

    // ---------------------- MÉTODO 1: MÍNIMOS CUADRADOS (MENSUAL) ----------------------
    private List<ProyeccionDTO> proyectarMinimosCuadrados(List<String> mesesHistoricos, List<BigDecimal> montos, int mesesFuturos) {
        int n = montos.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = montos.get(i).doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        // Obtener la última fecha histórica (mes más reciente)
        String ultimoMes = mesesHistoricos.get(mesesHistoricos.size() - 1);
        int ultimoAnio = Integer.parseInt(ultimoMes.split("-")[0]);
        int ultimoMesNum = Integer.parseInt(ultimoMes.split("-")[1]);

        List<ProyeccionDTO> resultado = new ArrayList<>();

        for (int i = 1; i <= mesesFuturos; i++) {
            double x = n + i;
            double y = a + b * x;

            // Avanzar mes a mes
            ultimoMesNum++;
            if (ultimoMesNum > 12) {
                ultimoMesNum = 1;
                ultimoAnio++;
            }

            ProyeccionDTO dto = new ProyeccionDTO();
            dto.setFechaProyectada(LocalDate.of(ultimoAnio, ultimoMesNum, 1).withDayOfMonth(LocalDate.of(ultimoAnio, ultimoMesNum, 1).lengthOfMonth()));
            dto.setMontoProyectado(BigDecimal.valueOf(y));
            resultado.add(dto);
        }

        return resultado;
    }

    // ---------------------- MÉTODO 2: INCREMENTO PORCENTUAL (MENSUAL) ----------------------
    private List<ProyeccionDTO> proyectarIncrementoPorcentual(List<String> mesesHistoricos, List<BigDecimal> montos, int mesesFuturos) {
        BigDecimal incrementoPromedio = BigDecimal.ZERO;

        for (int i = 1; i < montos.size(); i++) {
            BigDecimal incremento = montos.get(i).subtract(montos.get(i - 1))
                    .divide(montos.get(i - 1), 6, BigDecimal.ROUND_HALF_UP);
            incrementoPromedio = incrementoPromedio.add(incremento);
        }

        incrementoPromedio = incrementoPromedio.divide(BigDecimal.valueOf(montos.size() - 1), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal montoBase = montos.get(montos.size() - 1);

        String ultimoMes = mesesHistoricos.get(mesesHistoricos.size() - 1);
        int ultimoAnio = Integer.parseInt(ultimoMes.split("-")[0]);
        int ultimoMesNum = Integer.parseInt(ultimoMes.split("-")[1]);

        List<ProyeccionDTO> resultado = new ArrayList<>();

        for (int i = 1; i <= mesesFuturos; i++) {
            montoBase = montoBase.multiply(BigDecimal.ONE.add(incrementoPromedio));

            ultimoMesNum++;
            if (ultimoMesNum > 12) {
                ultimoMesNum = 1;
                ultimoAnio++;
            }

            ProyeccionDTO dto = new ProyeccionDTO();
            dto.setFechaProyectada(LocalDate.of(ultimoAnio, ultimoMesNum, 1).withDayOfMonth(LocalDate.of(ultimoAnio, ultimoMesNum, 1).lengthOfMonth()));
            dto.setMontoProyectado(montoBase);
            resultado.add(dto);
        }

        return resultado;
    }

    // ---------------------- MÉTODO 3: INCREMENTO ABSOLUTO (MENSUAL) ----------------------
    private List<ProyeccionDTO> proyectarIncrementoAbsoluto(List<String> mesesHistoricos, List<BigDecimal> montos, int mesesFuturos) {
        BigDecimal incrementoPromedio = BigDecimal.ZERO;

        for (int i = 1; i < montos.size(); i++) {
            incrementoPromedio = incrementoPromedio.add(montos.get(i).subtract(montos.get(i - 1)));
        }

        incrementoPromedio = incrementoPromedio.divide(BigDecimal.valueOf(montos.size() - 1), 2, BigDecimal.ROUND_HALF_UP);

        BigDecimal montoBase = montos.get(montos.size() - 1);

        String ultimoMes = mesesHistoricos.get(mesesHistoricos.size() - 1);
        int ultimoAnio = Integer.parseInt(ultimoMes.split("-")[0]);
        int ultimoMesNum = Integer.parseInt(ultimoMes.split("-")[1]);

        List<ProyeccionDTO> resultado = new ArrayList<>();

        for (int i = 1; i <= mesesFuturos; i++) {
            montoBase = montoBase.add(incrementoPromedio);

            ultimoMesNum++;
            if (ultimoMesNum > 12) {
                ultimoMesNum = 1;
                ultimoAnio++;
            }

            ProyeccionDTO dto = new ProyeccionDTO();
            dto.setFechaProyectada(LocalDate.of(ultimoAnio, ultimoMesNum, 1).withDayOfMonth(LocalDate.of(ultimoAnio, ultimoMesNum, 1).lengthOfMonth()));
            dto.setMontoProyectado(montoBase);
            resultado.add(dto);
        }

        return resultado;
    }

}