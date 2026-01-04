package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EsgPrinter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compares budgets of two different years per sector.
 * Shows percentage allocation, top changes, and ESG impact.
 */
public class YearToYearBudgetComparison {

  private final EnvBudgetTranslator translator;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;

  public YearToYearBudgetComparison(EnvBudgetTranslator translator) {
    this.translator = translator;
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
  }

  /** Main entry point for comparing two years */
  public void compareYears(
      EnvYear baseYear,
      EnvYear compareYear,
      double baseTotal,
      double compareTotal) {

    printHeader(baseYear.getYear(), compareYear.getYear());

    Map<String, Double> baseTotals = calculateSectorTotals(baseYear);
    Map<String, Double> compareTotals = calculateSectorTotals(compareYear);

    printSectorComparison(baseTotals, compareTotals, baseTotal, compareTotal);
    printTopChanges(baseTotals, compareTotals);
    printEsgComparison(baseYear, compareYear, baseTotal, compareTotal);
    printConclusions(baseTotals, compareTotals);

    printFooter();
  }

  /** Sum all entries per sector for a given year */
  private Map<String, Double> calculateSectorTotals(EnvYear year) {
    Map<String, Double> totals = new LinkedHashMap<>();
    for (EnvSector sector : year.getSectors()) {
      double sum = 0.0;
      for (EnvUnit unit : sector.getUnits()) {
        for (EnvEntry entry : unit.getEntries()) {
          sum += entry.getAmount();
        }
      }
      totals.put(sector.getJsonKey(), sum);
    }
    return totals;
  }

  /** Prints sector comparison table */
  private void printSectorComparison(
      Map<String, Double> base,
      Map<String, Double> compare,
      double baseTotal,
      double compareTotal) {

    System.out.println("┌────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                 ΣΥΓΚΡΙΣΗ ΑΝΑ ΤΟΜΕΑ                                 │");
    System.out.println("├─────────────────────────────────────┬────────┬────────┬────────────┤");
    System.out.println("│ Τομέας                              │ Πριν   │ Μετά   │ Αλλαγή     │");
    System.out.println("├─────────────────────────────────────┼────────┼────────┼────────────┤");

    Set<String> allKeys = new LinkedHashSet<>();
    allKeys.addAll(base.keySet());
    allKeys.addAll(compare.keySet());

    for (String key : allKeys) {
      double baseValue = base.getOrDefault(key, 0.0);
      double compareValue = compare.getOrDefault(key, 0.0);
      double diff = compareValue - baseValue;
      double pctChange = baseValue > 0 ? (diff / baseValue) * 100 : 0.0;
      String arrow = diff > 0 ? "⬆" : diff < 0 ? "⬇" : "→";
      String name = truncate(translator.translateCategory(key), 35);

      System.out.printf(
          "│ %-35s │ %5.1f%% │ %5.1f%% │ %s%6.1f%%   │%n",
          name,
          (baseValue / baseTotal) * 100,
          (compareValue / compareTotal) * 100,
          arrow,
          pctChange);
    }

    System.out.println("└─────────────────────────────────────────────────────────────────────┘\n");
  }

  /** Shows top 3 increases and decreases */
  private void printTopChanges(Map<String, Double> base, Map<String, Double> compare) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                     ΟΙ ΜΕΓΑΛΥΤΕΡΕΣ ΑΛΛΑΓΕΣ                          │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘\n");

    List<SectorChange> changes = new ArrayList<>();
    for (String key : base.keySet()) {
      double diff = compare.getOrDefault(key, 0.0) - base.get(key);
      changes.add(new SectorChange(key, diff));
    }

    changes.sort((a, b) -> Double.compare(Math.abs(b.diff), Math.abs(a.diff)));

    int increases = 0, decreases = 0;
    for (SectorChange change : changes) {
      String name = truncate(translator.translateCategory(change.key), 40);
      if (change.diff > 0 && increases < 3) {
        System.out.printf(" • Μεγαλύτερη αύξηση: %s: +%,.2f €%n", name, change.diff);
        increases++;
      } else if (change.diff < 0 && decreases < 3) {
        System.out.printf(" • Μεγαλύτερη μείωση: %s: %, .2f €%n", name, change.diff);
        decreases++;
      }
    }

    if (increases == 0) System.out.println(" • Δεν υπάρχουν αυξήσεις");
    if (decreases == 0) System.out.println(" • Δεν υπάρχουν μειώσεις");
    System.out.println();
  }

  /** Compares ESG reports between the two years */
  private void printEsgComparison(
      EnvYear baseYear,
      EnvYear compareYear,
      double baseTotal,
      double compareTotal) {

    EsgReport baseReport = esgCalculator.calculateReport(baseYear, baseTotal);
    EsgReport compareReport = esgCalculator.calculateReport(compareYear, compareTotal);

    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                   ΕΠΙΠΤΩΣΗ ΣΤΗΝ ΒΙΩΣΙΜΟΤΗΤΑ (ESG)                   │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘\n");

    esgPrinter.printComparison(baseReport, compareReport);
  }

  /** Shows total budget difference */
  private void printConclusions(Map<String, Double> base, Map<String, Double> compare) {
    double baseSum = base.values().stream().mapToDouble(Double::doubleValue).sum();
    double compareSum = compare.values().stream().mapToDouble(Double::doubleValue).sum();

    System.out.println("📊 Συνολική Αλλαγή Προϋπολογισμού:");
    System.out.printf(" Διαφορά: %+, .2f €%n%n", compareSum - baseSum);
  }

  private void printHeader(String baseYear, String compareYear) {
    System.out.println();
    System.out.println("══════════════════════════════════════════════════════");
    System.out.println(" Σύγκριση Προϋπολογισμού: " + baseYear + " vs " + compareYear);
    System.out.println("══════════════════════════════════════════════════════\n");
  }

  private void printFooter() {
    System.out.println("══════════════════════════════════════════════════════");
    System.out.println(" Τέλος Ανάλυσης Σύγκρισης\n");
  }

  /** Shortens long labels for table output */
  private String truncate(String value, int maxLength) {
    if (value.length() <= maxLength) return value;
    return value.substring(0, maxLength - 3) + "...";
  }

  /** Holder for sector difference sorting */
  private static class SectorChange {
    private final String key;
    private final double diff;

    SectorChange(String key, double diff) {
      this.key = key;
      this.diff = diff;
    }
  }
}
