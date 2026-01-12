package gr.det.spinnovators.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EsgPrinter;

/**
 * Analyzes and compares budget data before and after changes.
 *
 * <p>This service provides comprehensive comparison including:
 * <ul>
 *   <li>Sector-by-sector breakdown with percentage changes</li>
 *   <li>Top changes analysis (biggest increases and decreases)</li>
 *   <li>ESG (Environmental, Social, Governance) impact analysis</li>
 *   <li>Visual pie charts</li>
 *   <li>Budget balance verification</li>
 *   <li>Strategic recommendations based on changes</li>
 * </ul>
 * </p>
 *
 * <p>The comparison is displayed in a formatted console report with
 * visual indicators (arrows, percentages) and structured sections.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */

public class InitialBudgetComparison {

  private final EnvBudgetTranslator translator;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;

  /**
   * Constructs a budget comparison analyzer with the specified translator.
   *
   * <p>Initializes the ESG calculator and printer for sustainability analysis.</p>
   *
   * @param translator the translator service for converting keys to Greek text
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

    // 3. Top changes analysis
    printTopChanges(originalTotals, modifiedTotals);

    // 4. ESG comparison
    printEsgComparison(originalYear, modifiedYear, totalBudget);

    // 5. Pie charts (ASCII)
    printPieChartComparison(originalTotals, modifiedTotals, totalBudget);

    // 6. Conclusions and recommendations
    printConclusions(originalTotals, modifiedTotals, totalBudget,
                    originalYear, modifiedYear);

    printComparisonFooter();
  }

  /**
   * Calculates the total budget amount for each sector.
   *
   * <p>This private helper method traverses the hierarchical budget structure
   * (Sector → Unit → Entry) and sums all entry amounts within each sector.
   * Results are stored in a LinkedHashMap to preserve insertion order.</p>
   *
   * @param year the budget year to analyze
   * @return a map of sector JSON keys to their total budget amounts
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
   * Prints a formatted comparison header with the budget year.
   *
   * <p>This private helper method displays a decorative box header
   * using Unicode box-drawing characters.</p>
   *
   * @param year the fiscal year being compared
   */
  private void printComparisonHeader(String year) {
    System.out.println("%n");
    System.out.println("╔════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                                                    ║");
    System.out.println("║           ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ " + year + "           ║");
    System.out.println("║              Πριν & Μετά τις Αλλαγές                               ║");
    System.out.println("║                                                                    ║");
    System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    System.out.println();
  }

  /**
   * Prints a side-by-side comparison table of all sectors.
   *
   * <p>This private helper method displays a formatted table showing:
   * <ul>
   *   <li>Sector name (translated to Greek)</li>
   *   <li>Percentage of total budget before changes</li>
   *   <li>Percentage of total budget after changes</li>
   *   <li>Percentage change with directional arrow (⬆⬇→)</li>
   * </ul>
   * </p>
   *
   * <p>Arrows indicate: ⬆ increase, ⬇ decrease, → no change.</p>
   *
   * @param original the map of original sector totals
   * @param modified the map of modified sector totals
   * @param totalBudget the total ministry budget for percentage calculations
   */
  private void printSectorComparison(Map<String, Double> original,
                                     Map<String, Double> modified,
                                     double totalBudget) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                    ΣΥΓΚΡΙΣΗ ΑΝΑ ΤΟΜΕΑ                               │");
    System.out.println("├─────────────────────────────────────────────────────────────────────┤");
    System.out.println("│ Τομέας                              │  Πριν  │  Μετά  │  Αλλαγή    │");
    System.out.println("├─────────────────────────────────────┼────────┼────────┼────────────┤");

    for (Map.Entry<String, Double> entry : original.entrySet()) {
      String sectorKey = entry.getKey();
      double origAmount = entry.getValue();
      double modAmount = modified.getOrDefault(sectorKey, 0.0);
      double change = modAmount - origAmount;
      double changePercent = (origAmount > 0) ? (change / origAmount) * 100 : 0;

      String sectorName = translator.translateCategory(sectorKey);
      String shortName = truncate(sectorName, 35);

      double origPercent = (origAmount / totalBudget) * 100;
      double modPercent = (modAmount / totalBudget) * 100;

      String arrow = change > 0 ? "⬆" : (change < 0 ? "⬇" : "→");

      System.out.printf("│ %-35s │ %5.1f%% │ %5.1f%% │ %s%6.1f%%   │%n",
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

      System.out.printf("    %s │%-20s %5.1f%%   │%-20s %5.1f%%%n",
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
      System.out.printf("    %s = %s%n", shortName, fullName);
    }
  }

  /**
   * Prints analysis of the top changes (biggest increases and decreases).
   *
   * <p>This private helper method:
   * <ol>
   *   <li>Calculates absolute and percentage changes for all sectors</li>
   *   <li>Sorts sectors by absolute change magnitude</li>
   *   <li>Displays up to 3 biggest increases</li>
   *   <li>Displays up to 3 biggest decreases</li>
   * </ol>
   * </p>
   *
   * <p>If no increases or decreases exist, appropriate messages are displayed.</p>
   *
   * @param original the map of original sector totals
   * @param modified the map of modified sector totals
   */
  private void printTopChanges(Map<String, Double> original,
                               Map<String, Double> modified) {
    System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
    System.out.println("│                     ΟΙ ΜΕΓΑΛΥΤΕΡΕΣ ΑΛΛΑΓΕΣ                          │");
    System.out.println("└─────────────────────────────────────────────────────────────────────┘");
    System.out.println();

    // Calculate all changes
    List<SectorChange> changes = new ArrayList<>();
    for (Map.Entry<String, Double> entry : original.entrySet()) {
      String sectorKey = entry.getKey();
      double origAmount = entry.getValue();
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
        System.out.printf("   %d. %s: +%,.2f € (%+.1f%%)%n",
            increases + 1, truncate(name, 40),
            change.absoluteChange, change.percentChange);
        increases++;
      }
    }
    if (increases == 0) {
      System.out.println(" (Δεν υπάρχουν αυξήσεις)");
    }

    System.out.println();
    System.out.println(" Μεγαλύτερες Μειώσεις:");
    int decreases = 0;
    for (int i = changes.size() - 1; i >= 0 && decreases < 3; i--) {
      SectorChange change = changes.get(i);
      if (change.absoluteChange < 0) {
        String name = translator.translateCategory(change.sectorKey);
        System.out.printf("   %d. %s: %,.2f € (%.1f%%)%n",
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
   * Prints ESG (Environmental, Social, Governance) score comparison.
   *
   * <p>This private helper method:
   * <ul>
   *   <li>Calculates ESG reports for both original and modified budgets</li>
   *   <li>Displays the comparison using the EsgPrinter</li>
   * </ul>
   * </p>
   *
   * <p>The comparison shows how budget changes affect sustainability scores
   * in each ESG category and overall.</p>
   *
   * @param original the original budget year
   * @param modified the modified budget year
   * @param totalBudget the total ministry budget
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
   * Prints conclusions, recommendations, and overall assessment.
   *
   * <p>This private helper method generates a comprehensive analysis including:
   * <ul>
   *   <li>Budget balance verification</li>
   *   <li>Change focus analysis (which sectors changed most)</li>
   *   <li>ESG-based recommendations</li>
   *   <li>Overall assessment based on ESG scores</li>
   * </ul>
   * </p>
   *
   * <p>Recommendations are tailored based on ESG scores and help guide
   * future budget decisions toward sustainability goals.</p>
   *
   * @param original the map of original sector totals
   * @param modified the map of modified sector totals
   * @param totalBudget the total ministry budget
   * @param originalYear the original budget year
   * @param modifiedYear the modified budget year
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
    System.out.println(" Ισοσκέλιση Προϋπολογισμού:");
    if (Math.abs(totalChange) < 0.01) {
      System.out.println("    Ο προϋπολογισμός είναι πλήρως ισοσκελισμένος!");
    } else {
      System.out.printf(" Διαφορά: %,.2f € (χρειάζεται περαιτέρω προσαρμογές)%n",
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
      System.out.println(" Καλή έμφαση σε περιβαλλοντικές δαπάνες");
    } else {
      System.out.println(" Εξετάστε αύξηση περιβαλλοντικών επενδύσεων");
    }

    if (modReport.getSocialScore() < 20) {
      System.out.println(" Οι κοινωνικές δαπάνες είναι χαμηλές - εξετάστε αύξηση");
    }

    if (modReport.getGovernanceScore() > 20) {
      System.out.println(" Υψηλές διοικητικές δαπάνες - εξετάστε βελτιστοποίηση");
    }

    System.out.println();

    // Overall assessment
    System.out.println(" Συνολική Αξιολόγηση:");
    double overallScore = modReport.getOverallScore();
    if (overallScore >= 60) {
      System.out.println(" Εξαιρετική κατανομή προϋπολογισμού!");
      System.out.println(" Οι αλλαγές ενισχύουν τη βιωσιμότητα του Υπουργείου.");
    } else if (overallScore >= 40) {
      System.out.println(" Καλή κατανομή με περιθώρια βελτίωσης");
      System.out.println(" Συνεχίστε να επενδύετε σε πράσινες τεχνολογίες.");
    } else {
      System.out.println(" Χρειάζεται περισσότερη έμφαση στη βιωσιμότητα");
      System.out.println(" Προτεραιότητα: Αύξηση περιβαλλοντικών δαπανών.");
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

    for (Map.Entry<String, Double> entry : original.entrySet()) {
      String sectorKey = entry.getKey();
      double change = modified.getOrDefault(sectorKey, 0.0) - entry.getValue();

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
      System.out.printf("   • Κύρια εστίαση: %s (+%,.2f €)%n",
          truncate(name, 40), maxIncrease);
    }

    System.out.printf("   • Τομείς με αύξηση: %d%n", sectorsIncreased);
    System.out.printf("   • Τομείς με μείωση: %d%n", sectorsDecreased);

    if (sectorsIncreased == 0 && sectorsDecreased == 0) {
      System.out.println("   • Δεν έγιναν σημαντικές αλλαγές");
    }
  }

  /**
   * Prints a formatted comparison footer.
   *
   * <p>This private helper method displays a decorative box footer
   * using Unicode box-drawing characters to mark the end of the comparison.</p>
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
   * Truncates a string to a maximum length with ellipsis if needed.
   *
   * <p>This private helper method ensures that long sector names fit
   * within the formatted table columns. If the string exceeds the maximum
   * length, it is cut and "..." is appended.</p>
   *
   * @param str the string to potentially truncate
   * @param maxLength the maximum allowed length including the ellipsis
   * @return the truncated string with "..." suffix if needed, or the original string
   */
  private String truncate(String str, int maxLength) {
    if (str.length() <= maxLength) {
      return str;
    }
    return str.substring(0, maxLength - 3) + "...";
  }

  /**
   * Constructs a SectorChange record.
   *
   * @param key the sector's JSON key
   * @param absChange the absolute change in euros
   * @param pctChange the percentage change
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
