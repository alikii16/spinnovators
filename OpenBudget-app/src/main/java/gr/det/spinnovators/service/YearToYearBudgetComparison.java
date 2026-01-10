package gr.det.spinnovators.service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EsgPrinter;

/**
 * Compares budgets of two different years per sector.
 *
 * <p>This class performs comprehensive year-over-year budget analysis,
 * including sector-by-sector comparison, percentage changes, ESG impact
 * evaluation, and summary statistics.
 *
 * @author Spinnovators Team
 * @version 2.0
 */

public class YearToYearBudgetComparison {

  private final EnvBudgetTranslator translator;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;
  private final Map<String, Double> totalBudgets;

  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");

  /**
   * Constructs a YearToYearBudgetComparison analyzer.
   *
   * @param translator Service for translating budget category keys to Greek.
   * @param totalBudgets Map of year to total ministry budget amounts.
   */
  public YearToYearBudgetComparison(EnvBudgetTranslator translator,
                                    Map<String, Double> totalBudgets) {
    this.translator = translator;
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
    this.totalBudgets = totalBudgets;
  }

  /**
   * Main entry point for comparing two years.
   *
   * <p>Performs complete analysis including sector comparison table,
   * summary statistics, and ESG sustainability impact evaluation.
   *
   * @param base The base year for comparison.
   * @param compare The comparison year.
   */

  public void compareYears(EnvYear base, EnvYear compare) {
    String baseYear = base.getYear();
    String compareYear = compare.getYear();

    printHeader(baseYear, compareYear);

    // 1. Calculate per-sector totals
    Map<String, Double> baseSectors = getSectorTotals(base);
    Map<String, Double> compareSectors = getSectorTotals(compare);

    // 2. Identify all unique sectors involved
    Set<String> allSectors = new LinkedHashSet<>();
    allSectors.addAll(baseSectors.keySet());
    allSectors.addAll(compareSectors.keySet());

    // 3. Print table header
    System.out.printf(HELLENIC_LOCALE, "%-45s | %12s | %12s | %12s | %8s%n",
        "Τομέας / Sector", baseYear, compareYear, "Διαφορά", "%");
    System.out.println("------------------------------------------------------");

    // 4. Print rows
    for (String sectorKey : allSectors) {
      double val1 = baseSectors.getOrDefault(sectorKey, 0.0);
      double val2 = compareSectors.getOrDefault(sectorKey, 0.0);
      double diff = val2 - val1;
      double pct = (val1 == 0) ? (val2 == 0 ? 0 : 100.0) : (diff / val1) * 100.0;

      String name = translator.translateCategory(sectorKey);
      if (name.length() > 45) {
        name = truncate(name, 42);
      }

      System.out.printf(HELLENIC_LOCALE, "%-45s | %,12.0f | %,12.0f | %+,.0f | %+7.1f%%%n",
          name, val1, val2, diff, pct);
    }

    // 5. Comparison Conclusions
    printConclusions(baseSectors, compareSectors);

    // 6. ESG Comparison
    Double totalBase = totalBudgets.get(baseYear);
    Double totalCompare = totalBudgets.get(compareYear);

    if (totalBase == null) {
      totalBase = baseSectors.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    if (totalCompare == null) {
      totalCompare = compareSectors.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    System.out.println(" Σύγκριση ESG Score:");
    EsgReport baseReport = esgCalculator.calculateReport(base, totalBase);
    EsgReport compareReport = esgCalculator.calculateReport(compare, totalCompare);

    esgPrinter.printComparison(baseReport, compareReport);

    printFooter();
  }


  /**
   * Aggregates budget totals by sector for a given year.
   *
   * @param year The budget year to analyze.
   * @return Map of sector key to total amount.
   */
  private Map<String, Double> getSectorTotals(EnvYear year) {
    Map<String, Double> map = new LinkedHashMap<>();
    for (EnvSector s : year.getSectors()) {
      double sum = 0;
      for (EnvUnit u : s.getUnits()) {
        for (EnvEntry e : u.getEntries()) {
          sum += e.getAmount();
        }
      }
      map.put(s.getJsonKey(), sum);
    }
    return map;
  }

  /**
   * Prints summary conclusions showing total budget difference.
   *
   * @param base Base year sector totals.
   * @param compare Comparison year sector totals.
   */
  private void printConclusions(Map<String, Double> base, Map<String, Double> compare) {
    double baseSum = base.values().stream().mapToDouble(Double::doubleValue).sum();
    double compareSum = compare.values().stream().mapToDouble(Double::doubleValue).sum();

    System.out.println();
    System.out.println(" Συνολική Αλλαγή Προϋπολογισμού:");
    System.out.printf(HELLENIC_LOCALE, " Διαφορά: %+,.2f €%n%n", compareSum - baseSum);
  }
  /**
   * Prints the comparison header.
   *
   * @param baseYear Base year string.
   * @param compareYear Comparison year string.
   */
  private void printHeader(String baseYear, String compareYear) {
    System.out.println();
    System.out.println("══════════════════════════════════════════════════════");
    System.out.println(" Σύγκριση Προϋπολογισμού: " + baseYear + " vs " + compareYear);
    System.out.println("══════════════════════════════════════════════════════\n");
  }
  /**
   * Prints the comparison footer.
   */
  private void printFooter() {
    System.out.println("══════════════════════════════════════════════════════");
    System.out.println(" Τέλος Ανάλυσης Σύγκρισης\n");
  }

  /**
   * Shortens long labels for table output.
   *
   * @param value The string to truncate.
   * @param maxLength Maximum allowed length.
   * @return Truncated string with "..." suffix if needed.
   */
  private String truncate(String value, int maxLength) {
    if (value.length() <= maxLength) {
    return value;
   }
    return value.substring(0, maxLength - 3) + "...";
  }
}
