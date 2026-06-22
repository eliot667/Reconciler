package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Discrepancy;
import java.util.List;
import io.github.eliot667.reconcile.model.RowKey;
import io.github.eliot667.reconcile.model.Row;

public class ConsoleReporter implements Reporter {

    @Override
    public void report(List<Discrepancy> discrepancies) {
        System.out.println("Reconciliation report");
        System.out.println("=====================");
        System.out.println(discrepancies.size() + " discrepancies found");
        System.out.println();
        for (Discrepancy d : discrepancies) {
            System.out.println(">" + describe(d));
        }
    }

    private String describe(Discrepancy d) {
        return switch (d) {
            case Discrepancy.MissingInTarget(RowKey key, Row row) ->
                "MISSING IN TARGET   key = " + key.values();
            // TODO: case for MissingInSource — same shape, different label
            case Discrepancy.MissingInSource(RowKey key, Row row) ->
                "MISSING IN SOURCE key = " + key.values();
            // TODO: case for ValueMismatch — components are (key, column, sourceValue, targetValue);
            //       show the column and the two values, e.g. "column 'amount': 200 -> 250"
            case Discrepancy.ValueMismatch(RowKey key, String column, String sourceValue, String targetValue) ->
                "VALUE MISMATCH, source value = " + sourceValue + ", target value = " + targetValue; 
                
        };
    }
}