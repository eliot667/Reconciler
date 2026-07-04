package io.github.eliot667.reconcile;

import io.github.eliot667.reconcile.model.Discrepancy;
import io.github.eliot667.reconcile.model.Row;
import io.github.eliot667.reconcile.CsvLoader;
import io.github.eliot667.reconcile.ConsoleReporter;
import io.github.eliot667.reconcile.ExcelExporter;

import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        CsvLoader loader = new CsvLoader();

        List<String> keyRows = new ArrayList<>();
        keyRows.add("id");

        List<Row> source = loader.load(Path.of("data/source.csv"));
        List<Row> target = loader.load(Path.of("data/target.csv"));

        Reconciler reconciler = new Reconciler(keyRows);
        List<Discrepancy> findings = reconciler.reconcile(source, target);

        ConsoleReporter fileReporter = new ConsoleReporter();
        fileReporter.report(findings);
        System.out.println("\nWould you like to save an Excel file of this report? [y/n]");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if(Character.toLowerCase(input.charAt(0)) == 'y')
        {
            System.out.println("Exporting...");
            ExcelExporter.writeToExcel(keyRows,findings, source, target);
        }
        else
        {
            return;
        }
    }
}