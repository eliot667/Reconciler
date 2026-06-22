package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Discrepancy;
import io.github.eliot667.reconcile.model.Row;
import io.github.eliot667.reconcile.model.RowKey;
import io.github.eliot667.reconcile.RowIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Reconciler {
    private final  List<String> keyColumns;

    public Reconciler(List<String> keyColumns) {
        this.keyColumns = List.copyOf(keyColumns);
    }

    public List<Discrepancy> reconcile(List<Row> source, List<Row> target) {
    Map<RowKey, Row> sourceByKey = RowIndex.indexByKey(source, keyColumns);
    Map<RowKey, Row> targetByKey = RowIndex.indexByKey(target, keyColumns);

    List<Discrepancy> results = new ArrayList<>();

    for (var entry : sourceByKey.entrySet()) {
        RowKey key = entry.getKey();
        Row sourceRow = entry.getValue();
        // TODO: if target has no row for this key -> add a MissingInTarget
        if(!targetByKey.containsKey(key)) {
            results.add(new Discrepancy.MissingInTarget(key, sourceRow));
        // TODO: else, compare the two rows column by column ->
        //       add a ValueMismatch for each column whose value differs
        } else {
            Row targetRow = targetByKey.get(key);
            for(String column : sourceRow.fields().keySet()){
                String sourceValue = sourceRow.fields().get(column);
                String targetValue = targetRow.fields().get(column);
                if(!Objects.equals(sourceValue,targetValue)) {
                    results.add(new Discrepancy.ValueMismatch(key, column, sourceRow.fields().get(column), targetRow.fields().get(column)));
                }
            }

        }
    }

    for (var entry : targetByKey.entrySet()) {
        // TODO: if source has no row for this key -> add a MissingInSource
        if(!sourceByKey.containsKey(entry.getKey())) {
            results.add(new Discrepancy.MissingInSource(entry.getKey(), entry.getValue()));
        }
    }

    return results;
}
}