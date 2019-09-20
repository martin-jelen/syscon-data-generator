package de.isb.mjelen.itkons;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Generator {
    public static void main(String... args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Systeme");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue("A");
        cell.setCellStyle(headerCellStyle);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        Row row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(new Date());
        cell.setCellStyle(dateCellStyle);

        sheet.autoSizeColumn(0);

        FileOutputStream fileOut = new FileOutputStream("systemlandschaft.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }
}
