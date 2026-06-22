package io.github.eliot667.reconcile.model;

import java.util.Map;

public record Row(Map<String, String> fields) {
    public Row{
        //incoming fields map becomes a copy of so the original data cannot be modified by this class
        fields = Map.copyOf(fields);
    }
}