package io.github.eliot667.reconcile.model;

public sealed interface Discrepancy {
    //Interfacing key call here because every record already has a key function, and we will need to
    //index discrepancies by key for excel export.
    RowKey key();

    record MissingInSource(RowKey key, Row row) implements Discrepancy {};

    record MissingInTarget(RowKey key, Row row) implements Discrepancy {};

    record ValueMismatch(RowKey key, String column, String sourceValue, String targetValue) implements Discrepancy {};
	//POTENTIAL TODO Make discrepancy for missing column/column mismatch?
}