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

public class ExcelExporter{
    
    void writeToExcel(List<String> keyRows, List<Discrepancy> discrepancies, List<Row> source, List<Row> target)
    {
        //TODO implement discrepancy report that will be used to document differences in excel file
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

        //TODO loop through all columns in each row based on size!!!
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
            Cell sourceCell = sheetRow.createCell(1);
            Cell targetCell = sheetRow.createCell(2);

            switch (discrepancyByKey.get(key)) {
                //TODO refactor switch contents to a helper class. DRY!
                case Discrepancy.MissingInSource d:
                    sourceCell.setCellValue("MISSING");
                    colorCellByDiscrepancy(sourceCell,true);
                    targetCell.setCellValue(targetByKey.get(key).fields().get("amount"));
                    //System.out.println(targetByKey.get(key).fields().get(key.values().get(0)));
                    colorCellByDiscrepancy(targetCell,true);
                    break;
                case Discrepancy.MissingInTarget d:
                    sourceCell.setCellValue(sourceByKey.get(key).fields().get("amount"));
                    colorCellByDiscrepancy(sourceCell,true);
                    targetCell.setCellValue("MISSING");
                    colorCellByDiscrepancy(targetCell,true);
                    break;
                case Discrepancy.ValueMismatch d:
                    sourceCell.setCellValue(sourceByKey.get(key).fields().get("amount"));
                    colorCellByDiscrepancy(sourceCell,false);
                    targetCell.setCellValue(targetByKey.get(key).fields().get("amount"));
                    colorCellByDiscrepancy(targetCell,false);
                    break;
                default:
                    sourceCell.setCellValue(sourceByKey.get(key).fields().get("amount"));
                    targetCell.setCellValue(targetByKey.get(key).fields().get("amount"));
                    break;
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

    private void extractKeysToSet(List<Row> rows, List<String> keyColumns, Set<RowKey> keySet)
    {
        for(Row row : rows)
        {
            keySet.add(RowKey.from(row, keyColumns));
        }
    }

    private void colorCellByDiscrepancy(Cell cell, Boolean isWarning)
    {
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        if(isWarning)
        {
            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        }
        else
        {
            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        }
        cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
        cell.setCellStyle(cellStyle);
    }

    private void populateExcelCell(Map<RowKey,Row> sourceByKey, Map<RowKey,Row> targetByKey)
    {

    }
}