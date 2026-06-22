package io.github.eliot667.reconcile.model;

import java.util.List;

public record RowKey(List<String> values) {
    public RowKey{
        //incoming values list becomes a copy of so the original data cannot be modified by this class
        values = List.copyOf(values);
    }

    public static RowKey from(Row row, List<String> keyColumns)
    {
        return new RowKey(keyColumns.stream()
                             .map(col -> row.fields().get(col))
                             .toList());
    }
}