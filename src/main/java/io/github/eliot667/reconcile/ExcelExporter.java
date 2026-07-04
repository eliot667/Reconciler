package io.github.eliot667.reconcile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.eliot667.reconcile.model.Discrepancy;
import io.github.eliot667.reconcile.model.RowKey;
import io.github.eliot667.reconcile.model.Row;
import io.github.eliot667.reconcile.RowIndex;

public final class ExcelExporter{
    
    static void writeToExcel(List<String> keyRows, List<Discrepancy> discrepancies, List<Row> source, List<Row> target)
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Discrepancy Report");

        CellStyle missingCell = workbook.createCellStyle();
        CellStyle mismatchCell = workbook.createCellStyle(); 

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Row keyNames = sheet.createRow(1);

        Object[] columnArray = source.get(0).fields().keySet().toArray();

        Cell sourceHeader = header.createCell(0);
        sourceHeader.setCellValue("Source");

        Cell targetHeader = header.createCell(columnArray.length);
        targetHeader.setCellValue("Target");

        

        for(int i = 0; i < columnArray.length; i++)
        {
            Cell sourceKeyName = keyNames.createCell(i);
            Cell targetKeyName = keyNames.createCell(i + columnArray.length);

            sourceKeyName.setCellValue(columnArray[i].toString());
            targetKeyName.setCellValue(columnArray[i].toString());
        }

        Set<RowKey> keySet = new LinkedHashSet<>();

        extractKeysToSet(source, keyRows, keySet);
        extractKeysToSet(target, keyRows, keySet);

        Map<RowKey,Row> sourceByKey = RowIndex.indexByKey(source, keyRows);
        Map<RowKey,Row> targetByKey = RowIndex.indexByKey(target, keyRows);
        Map<RowKey,Discrepancy> discrepancyByKey = RowIndex.discrepancyByKey(discrepancies, keyRows);

        int sheetRowCount = 2;
        for(RowKey key : keySet)
        {
            org.apache.poi.ss.usermodel.Row sheetRow = sheet.createRow(sheetRowCount++);
            Cell sourceCell = sheetRow.createCell(0);
            Cell targetCell = sheetRow.createCell(columnArray.length);

            if(discrepancyByKey.containsKey(key))
            {
                switch (discrepancyByKey.get(key)) {
                case Discrepancy.MissingInSource d:
                    for(int i = 0; i < columnArray.length; i++)
                    {
                        sourceCell = sheetRow.createCell(i);
                        populateExcelCell(sourceCell, "MISSING", IndexedColors.YELLOW);

                        targetCell = sheetRow.createCell(i + columnArray.length);
                        populateExcelCell(targetCell, targetByKey.get(key).fields().get(columnArray[i]), IndexedColors.YELLOW);
                    }
                    
                    break;
                case Discrepancy.MissingInTarget d:
                    for(int i = 0; i < columnArray.length; i++)
                    {
                        sourceCell = sheetRow.createCell(i);
                        populateExcelCell(sourceCell, sourceByKey.get(key).fields().get(columnArray[i]), IndexedColors.YELLOW);

                        targetCell = sheetRow.createCell(i + columnArray.length);
                        populateExcelCell(targetCell, "MISSING", IndexedColors.YELLOW);
                    }
                    
                    break;
                case Discrepancy.ValueMismatch d:
                    for(int i = 0; i < columnArray.length; i++)
                    {
                        sourceCell = sheetRow.createCell(i);
                        populateExcelCell(sourceCell, sourceByKey.get(key).fields().get(columnArray[i]), IndexedColors.RED);
                    }
                    for(int i = 0; i < columnArray.length; i++)
                    {
                        targetCell = sheetRow.createCell(i + columnArray.length);
                        populateExcelCell(targetCell, targetByKey.get(key).fields().get(columnArray[i]), IndexedColors.RED);
                    }
                    break;
                default:
                    break;
                }
            }
            else
            {
                for(int i = 0; i < columnArray.length; i++)
                    {
                        sourceCell = sheetRow.createCell(i);
                        sourceCell.setCellValue(sourceByKey.get(key).fields().get(columnArray[i]));
                    }
                for(int i = 0; i < columnArray.length; i++)
                    {
                        targetCell = sheetRow.createCell(i + columnArray.length);
                        targetCell.setCellValue(targetByKey.get(key).fields().get(columnArray[i]));
                    }
            }
        }
        
        try(FileOutputStream fileOut = new FileOutputStream("TEST.xlsx"))
        {
            workbook.write(fileOut);
            System.out.println("File exported.");
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void extractKeysToSet(List<Row> rows, List<String> keyColumns, Set<RowKey> keySet)
    {
        for(Row row : rows)
        {
            keySet.add(RowKey.from(row, keyColumns));
        }
    }

    private static void populateExcelCell(Cell cell, String rowData, IndexedColors color)
    {
        cell.setCellValue(rowData);

        CellStyle cellStyle = cell.getCellStyle();
        cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        cellStyle.setFillForegroundColor(color.getIndex());
        cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
        cell.setCellStyle(cellStyle);
    }
}