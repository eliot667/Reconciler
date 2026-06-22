package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Discrepancy;

import java.io.IOException;
import java.util.List;

public interface Reporter{
    void report(List<Discrepancy> discrepancies) throws IOException;
}