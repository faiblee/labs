package ru.ssau.tk.faible.labs.database.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class ExcelWriter {

    public static void saveToExcel(List<BenchmarkResult> results, String filename) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Benchmark Results");

            // Создаем заголовок
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Query Name");
            headerRow.createCell(1).setCellValue("Duration (ms)");
            headerRow.createCell(2).setCellValue("Records Count");

            // Заполняем данные
            int rowNum = 1;
            for (BenchmarkResult result : results) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.getQueryName());
                row.createCell(1).setCellValue(result.getDuration());
                row.createCell(2).setCellValue(result.getRecords_count());
            }

            // Авто-размер колонок
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            // Сохраняем файл
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                workbook.write(fos);
            }

            log.info("Результаты сохранены в файл с названием {} ", filename);

        } catch (Exception e) {
            log.error("Ошибка при сохранении данных в файл");
        }
    }
}
