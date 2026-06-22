package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Row;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();

    public List<Row> load(Path path) throws IOException {
        List<Row> rows = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser parser = FORMAT.parse(reader)) {
            for (CSVRecord record : parser) {
                rows.add(new Row(record.toMap()));
            }
        }
        return rows;
    }
}