package ru.ssau.tk.faible.labs.performance;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.List;

@Component
public class ExcelWriter {
    private static final Logger log = LoggerFactory.getLogger(ExcelWriter.class);

    public void saveToExcel(List<BenchmarkResult> results, String filename) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Benchmark Results");

            // Стиль для заголовка
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создаем заголовок
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "Query Name", headerStyle);
            createCell(headerRow, 1, "Duration (ms)", headerStyle);
            createCell(headerRow, 2, "Records Count", headerStyle);

            // Заполняем данные
            int rowNum = 1;
            for (BenchmarkResult result : results) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.getQueryName());
                row.createCell(1).setCellValue(result.getDuration());
                row.createCell(2).setCellValue(result.getRecordsCount());
            }

            // Авто-размер колонок
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Сохраняем файл
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                workbook.write(fos);
            }

            log.info("Results saved to Excel file: {}", filename);

        } catch (Exception e) {
            log.error("Error saving results to Excel: {}", e.getMessage());
        }
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}