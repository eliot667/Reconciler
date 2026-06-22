package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Row;
import io.github.eliot667.reconcile.model.RowKey;
import io.github.eliot667.reconcile.model.Discrepancy;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public final class RowIndex {
    public static Map<RowKey, Row> indexByKey(List<Row> rows, List<String> keyColumns) {
    Map<RowKey, Row> byKey = new HashMap<>();
        for (Row row : rows) {
            byKey.put(RowKey.from(row, keyColumns), row);
        }
        return byKey;
    }

    public static Map<RowKey, Discrepancy> discrepancyByKey(List<Discrepancy> discrepancies, List<String> keyColumns) {
    Map<RowKey, Discrepancy> byKey = new HashMap<>();
        for (Discrepancy discrepancy : discrepancies) {
            byKey.put(discrepancy.key(), discrepancy);
        }
        return byKey;
    }
}
