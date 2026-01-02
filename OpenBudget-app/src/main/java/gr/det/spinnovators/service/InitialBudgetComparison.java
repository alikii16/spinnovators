package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EsgPrinter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyzes and compares budget data before and after changes.
 *
 * <p>Provides comprehensive comparison including:
 * - Sector-by-sector breakdown
 * - Percentage changes
 * - Visual pie charts
 * - ESG impact analysis
 * - Recommendations based on changes
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class InitialBudgetComparison {

  private final EnvBudgetTranslator translator;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;

  /**
   * Constructs a budget comparison analyzer.
   *
   * @param translator Translator for Greek text
   */
  public InitialBudgetComparison(EnvBudgetTranslator translator) {
    this.translator = translator;
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
  }

  /**
   * Performs a complete comparison analysis between original and modified budget.
   *
   * @param originalYear Original budget year (before changes)
   * @param modifiedYear Modified budget year (after changes)
   * @param totalBudget Total ministry budget
   */
  public void performFullComparison(EnvYear originalYear, EnvYear modifiedYear,
                                    double totalBudget) {

    printComparisonHeader(originalYear.getYear());

    // 1. Calculate sector totals
    Map<String, Double> originalTotals = calculateSectorTotals(originalYear);
    Map<String, Double> modifiedTotals = calculateSectorTotals(modifiedYear);

    // 2. Side-by-side sector comparison
    printSectorComparison(originalTotals, modifiedTotals, totalBudget);

    // 3. Pie charts (ASCII)
    printPieChartComparison(originalTotals, modifiedTotals, totalBudget);

    // 4. Top changes analysis
    printTopChanges(originalTotals, modifiedTotals);

    // 5. ESG comparison
    printEsgComparison(originalYear, modifiedYear, totalBudget);

    // 6. Conclusions and recommendations
    printConclusions(originalTotals, modifiedTotals, totalBudget,
                    originalYear, modifiedYear);

    printComparisonFooter();
  }

  /**
   * Calculates total budget amount for each sector.
   *
   * @param year The budget year to analyze
   * @return Map of sector keys to total amounts
   */
  private Map<String, Double> calculateSectorTotals(EnvYear year) {
    Map<String, Double> totals = new LinkedHashMap<>();

    for (EnvSector sector : year.getSectors()) {
      double sectorTotal = 0.0;

      for (EnvUnit unit : sector.getUnits()) {
        for (EnvEntry entry : unit.getEntries()) {
          sectorTotal += entry.getAmount();
        }
      }

      totals.put(sector.getJsonKey(), sectorTotal);
    }

    return totals;
  }

  /**
   * Prints the comparison header.
   *
   * @param year The budget year
   */
  private void printComparisonHeader(String year) {
    System.out.println("\n");
    System.out.println("╔════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                                                    ║");
    System.out.println("║           ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ " + year + "           ║");
    System.out.println("║              Πριν & Μετά τις Αλλαγές                               ║");
    System.out.println("║                                                                    ║");
    System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    System.out.println();
  }

  /**
   * Prints side-by-side sector comparison.
   *
   * @param original Original sector totals
   * @param modified Modified sector totals
   * @param totalBudget Total ministry budget
   */
  private void printSectorComparison(Map<String, Double> original,
                                     Map<String, Double> modified,
                                     double totalBudget) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                    ΣΥΓΚΡΙΣΗ ΑΝΑ ΤΟΜΕΑ                               │");
    System.out.println("├─────────────────────────────────────────────────────────────────────┤");
    System.out.println("│ Τομέας                              │  Πριν  │  Μετά  │  Αλλαγή    │");
    System.out.println("├─────────────────────────────────────┼────────┼────────┼────────────┤");

    for (String sectorKey : original.keySet()) {
      double origAmount = original.get(sectorKey);
      double modAmount = modified.getOrDefault(sectorKey, 0.0);
      double change = modAmount - origAmount;
      double changePercent = (origAmount > 0) ? (change / origAmount) * 100 : 0;

      String sectorName = translator.translateCategory(sectorKey);
      String shortName = truncate(sectorName, 35);

      double origPercent = (origAmount / totalBudget) * 100;
      double modPercent = (modAmount / totalBudget) * 100;

      String arrow = change > 0 ? "⬆" : (change < 0 ? "⬇" : "→");

      System.out.printf("│ %-35s │ %5.1f%% │ %5.1f%% │ %s%6.1f%%   │\n",
          shortName, origPercent, modPercent, arrow, changePercent);
    }

    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();
  }

  /**
   * Prints ASCII pie chart comparison.
   *
   * @param original Original sector totals
   * @param modified Modified sector totals
   * @param totalBudget Total ministry budget
   */
  private void printPieChartComparison(Map<String, Double> original,
                                       Map<String, Double> modified,
                                       double totalBudget) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│               ΚΑΤΑΝΟΜΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ (Pie Charts)                  │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();

    // Print side by side
    System.out.println("         ΠΡΙΝ ΤΙΣ ΑΛΛΑΓΕΣ                    ΜΕΤΑ ΤΙΣ ΑΛΛΑΓΕΣ");
    System.out.println("    ════════════════════════          ════════════════════════");

    // Create visual bars for each sector
    List<String> sectorKeys = new ArrayList<>(original.keySet());

    for (int i = 0; i < sectorKeys.size(); i++) {
      String sectorKey = sectorKeys.get(i);
      String shortName = getShortSectorName(sectorKey, i);

      double origPercent = (original.get(sectorKey) / totalBudget) * 100;
      double modPercent = (modified.getOrDefault(sectorKey, 0.0) / totalBudget) * 100;

      String origBar = createPercentageBar(origPercent, 20);
      String modBar = createPercentageBar(modPercent, 20);

      System.out.printf("    %s │%-20s %5.1f%%   │%-20s %5.1f%%\n",
          shortName, origBar, origPercent, modBar, modPercent);
    }

    System.out.println();
    printSectorLegend(sectorKeys);
    System.out.println();
  }

  /**
   * Creates a visual percentage bar.
   *
   * @param percent Percentage value (0-100)
   * @param maxWidth Maximum width of the bar in characters
   * @return String representation of the bar
   */
  private String createPercentageBar(double percent, int maxWidth) {
    int filled = (int) Math.round((percent / 100.0) * maxWidth);
    filled = Math.min(filled, maxWidth);

    StringBuilder bar = new StringBuilder();
    for (int i = 0; i < filled; i++) {
      bar.append("█");
    }
    for (int i = filled; i < maxWidth; i++) {
      bar.append("░");
    }
    return bar.toString();
  }

  /**
   * Gets a short abbreviation for sector name.
   *
   * @param sectorKey The JSON key of the sector
   * @param index The index for fallback abbreviation
   * @return Short abbreviation string
   */
  private String getShortSectorName(String sectorKey, int index) {
    Map<String, String> abbreviations = new LinkedHashMap<>();
    abbreviations.put("executive_coordination_and_investments", "[Α]");
    abbreviations.put("natural_environment_and_water_protection", "[Β]");
    abbreviations.put("spatial_planning_and_urban_environment", "[Γ]");
    abbreviations.put("energy_and_mineral_resources_management", "[Δ]");

    return abbreviations.getOrDefault(sectorKey, "[" + (char) ('Α' + index) + "]");
  }

  /**
   * Prints legend for sector abbreviations.
   *
   * @param sectorKeys List of sector keys
   */
  private void printSectorLegend(List<String> sectorKeys) {
    System.out.println("    Υπόμνημα Τομέων:");
    for (int i = 0; i < sectorKeys.size(); i++) {
      String shortName = getShortSectorName(sectorKeys.get(i), i);
      String fullName = translator.translateCategory(sectorKeys.get(i));
      System.out.printf("    %s = %s\n", shortName, fullName);
    }
  }

  /**
   * Prints top changes (biggest increases/decreases).
   *
   * @param original Original sector totals
   * @param modified Modified sector totals
   */
  private void printTopChanges(Map<String, Double> original,
                               Map<String, Double> modified) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                     ΟΙ ΜΕΓΑΛΥΤΕΡΕΣ ΑΛΛΑΓΕΣ                          │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();

    // Calculate all changes
    List<SectorChange> changes = new ArrayList<>();
    for (String sectorKey : original.keySet()) {
      double origAmount = original.get(sectorKey);
      double modAmount = modified.getOrDefault(sectorKey, 0.0);
      double change = modAmount - origAmount;
      double changePercent = (origAmount > 0) ? (change / origAmount) * 100 : 0;

      changes.add(new SectorChange(sectorKey, change, changePercent));
    }

    // Sort by absolute change
    changes.sort((a, b) -> Double.compare(
        Math.abs(b.absoluteChange), Math.abs(a.absoluteChange)));

    // Print biggest increases
    System.out.println(" Μεγαλύτερες Αυξήσεις:");
    int increases = 0;
    for (SectorChange change : changes) {
      if (change.absoluteChange > 0 && increases < 3) {
        String name = translator.translateCategory(change.sectorKey);
        System.out.printf("   %d. %s: +%,.2f € (%+.1f%%)\n",
            increases + 1, truncate(name, 40),
            change.absoluteChange, change.percentChange);
        increases++;
      }
    }
    if (increases == 0) {
      System.out.println("   (Δεν υπάρχουν αυξήσεις)");
    }

    System.out.println();
    System.out.println(" Μεγαλύτερες Μειώσεις:");
    int decreases = 0;
    for (int i = changes.size() - 1; i >= 0 && decreases < 3; i--) {
      SectorChange change = changes.get(i);
      if (change.absoluteChange < 0) {
        String name = translator.translateCategory(change.sectorKey);
        System.out.printf("   %d. %s: %,.2f € (%.1f%%)\n",
            decreases + 1, truncate(name, 40),
            change.absoluteChange, change.percentChange);
        decreases++;
      }
    }
    if (decreases == 0) {
      System.out.println("   (Δεν υπάρχουν μειώσεις)");
    }

    System.out.println();
  }

  /**
   * Prints ESG comparison.
   *
   * @param original Original budget year
   * @param modified Modified budget year
   * @param totalBudget Total ministry budget
   */
  private void printEsgComparison(EnvYear original, EnvYear modified,
                                 double totalBudget) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                   ΕΠΙΠΤΩΣΗ ΣΤΗΝ ΒΙΩΣΙΜΟΤΗΤΑ (ESG)                   │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();

    EsgReport originalReport = esgCalculator.calculateReport(original, totalBudget);
    EsgReport modifiedReport = esgCalculator.calculateReport(modified, totalBudget);

    esgPrinter.printComparison(originalReport, modifiedReport);
  }

  /**
   * Prints conclusions and recommendations.
   *
   * @param original Original sector totals
   * @param modified Modified sector totals
   * @param totalBudget Total ministry budget
   * @param originalYear Original budget year
   * @param modifiedYear Modified budget year
   */
  private void printConclusions(Map<String, Double> original,
                               Map<String, Double> modified,
                               double totalBudget,
                               EnvYear originalYear,
                               EnvYear modifiedYear) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                   ΣΥΜΠΕΡΑΣΜΑΤΑ & ΣΥΣΤΑΣΕΙΣ                          │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();

    // Calculate total change
    double totalOriginal = original.values().stream().mapToDouble(Double::doubleValue).sum();
    double totalModified = modified.values().stream().mapToDouble(Double::doubleValue).sum();
    double totalChange = totalModified - totalOriginal;

    // Budget balance check
    System.out.println("📊 Ισοσκέλιση Προϋπολογισμού:");
    if (Math.abs(totalChange) < 0.01) {
      System.out.println("    Ο προϋπολογισμός είναι πλήρως ισοσκελισμένος!");
    } else {
      System.out.printf("     Διαφορά: %,.2f € (χρειάζεται περαιτέρω προσαρμογές)\n",
          totalChange);
    }

    System.out.println();

    // Sector focus analysis
    System.out.println(" Εστίαση Αλλαγών:");
    analyzeChangeFocus(original, modified);

    System.out.println();

    // ESG recommendations
    System.out.println(" Συστάσεις ESG:");
    EsgReport modReport = esgCalculator.calculateReport(modifiedYear, totalBudget);

    if (modReport.getEnvironmentalScore() >= 60) {
      System.out.println("    Καλή έμφαση σε περιβαλλοντικές δαπάνες");
    } else {
      System.out.println("    Εξετάστε αύξηση περιβαλλοντικών επενδύσεων");
    }

    if (modReport.getSocialScore() < 20) {
      System.out.println("    Οι κοινωνικές δαπάνες είναι χαμηλές - εξετάστε αύξηση");
    }

    if (modReport.getGovernanceScore() > 20) {
      System.out.println("    Υψηλές διοικητικές δαπάνες - εξετάστε βελτιστοποίηση");
    }

    System.out.println();

    // Overall assessment
    System.out.println(" Συνολική Αξιολόγηση:");
    double overallScore = modReport.getOverallScore();
    if (overallScore >= 60) {
      System.out.println("    Εξαιρετική κατανομή προϋπολογισμού!");
      System.out.println("   Οι αλλαγές ενισχύουν τη βιωσιμότητα του Υπουργείου.");
    } else if (overallScore >= 40) {
      System.out.println("    Καλή κατανομή με περιθώρια βελτίωσης");
      System.out.println("   Συνεχίστε να επενδύετε σε πράσινες τεχνολογίες.");
    } else {
      System.out.println("    Χρειάζεται περισσότερη έμφαση στη βιωσιμότητα");
      System.out.println("   Προτεραιότητα: Αύξηση περιβαλλοντικών δαπανών.");
    }

    System.out.println();
  }

  /**
   * Analyzes where the changes are focused.
   *
   * @param original Original sector totals
   * @param modified Modified sector totals
   */
  private void analyzeChangeFocus(Map<String, Double> original,
                                  Map<String, Double> modified) {
    int sectorsIncreased = 0;
    int sectorsDecreased = 0;
    String maxIncreaseSector = "";
    double maxIncrease = 0;

    for (String sectorKey : original.keySet()) {
      double change = modified.getOrDefault(sectorKey, 0.0) - original.get(sectorKey);

      if (change > 0.01) {
        sectorsIncreased++;
        if (change > maxIncrease) {
          maxIncrease = change;
          maxIncreaseSector = sectorKey;
        }
      } else if (change < -0.01) {
        sectorsDecreased++;
      }
    }

    if (sectorsIncreased > 0) {
      String name = translator.translateCategory(maxIncreaseSector);
      System.out.printf("   • Κύρια εστίαση: %s (+%,.2f €)\n",
          truncate(name, 40), maxIncrease);
    }

    System.out.printf("   • Τομείς με αύξηση: %d\n", sectorsIncreased);
    System.out.printf("   • Τομείς με μείωση: %d\n", sectorsDecreased);

    if (sectorsIncreased == 0 && sectorsDecreased == 0) {
      System.out.println("   • Δεν έγιναν σημαντικές αλλαγές");
    }
  }

  /**
   * Prints the comparison footer.
   */
  private void printComparisonFooter() {
    System.out.println("╔════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                                                    ║");
    System.out.println("║                    ΤΕΛΟΣ ΑΝΑΛΥΣΗΣ ΣΥΓΚΡΙΣΗΣ                        ║");
    System.out.println("║                                                                    ║");
    System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    System.out.println();
  }

  /**
   * Truncates a string to a maximum length.
   *
   * @param str String to truncate
   * @param maxLength Maximum length
   * @return Truncated string with "..." suffix if needed
   */
  private String truncate(String str, int maxLength) {
    if (str.length() <= maxLength) {
      return str;
    }
    return str.substring(0, maxLength - 3) + "...";
  }

  /**
   * Helper class to store sector change information.
   */
  private static class SectorChange {
    String sectorKey;
    double absoluteChange;
    double percentChange;

    SectorChange(String key, double absChange, double pctChange) {
      this.sectorKey = key;
      this.absoluteChange = absChange;
      this.percentChange = pctChange;
    }
  }
}
