package de.isb.mjelen.itkons.poi;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class StatefulPoiHelper {

    private final CellStyle headerCellStyle;
    private final CellStyle contentDateCellStyle;
    private final CellStyle contentTextCellStyle;

    @With
    private Sheet sheet;

    public void buildHeaderFromTitleArray(String[] titles) {
        Row headerRow = sheet.createRow(0);
        int columnIndex = 0;
        for (String t : titles) {
            Cell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(t);
            cell.setCellStyle(headerCellStyle);
            columnIndex++;
        }
    }

    public void buildContentRowFromStringArray(int index, String[] content) {
        Row contentRow = sheet.createRow(index);
        int columnIndex = 0;
        for (String c : content) {
            Cell cell = contentRow.createCell(columnIndex);
            cell.setCellValue(c);
            cell.setCellStyle(contentTextCellStyle);
            columnIndex++;
        }
    }

    public void autosizeHelper(int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
