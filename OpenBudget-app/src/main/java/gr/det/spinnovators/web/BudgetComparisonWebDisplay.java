package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles the generation of budget comparison content for the web interface.
 *
 * <p>This class compares original and modified budgets and formats them into HTML fragments
 * to be displayed in the web application's comparison page.
 */
public class BudgetComparisonWebDisplay {
  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
  private final EnvBudgetTranslator translator;

  public BudgetComparisonWebDisplay(EnvBudgetTranslator translator) {
    this.translator = translator;
  }

  /**
   * Generates the complete comparison content HTML.
   *
   * @param originalYear Original budget year (before changes)
   * @param modifiedYear Modified budget year (after changes)
   * @param totalBudget Total ministry budget
   * @return Complete HTML content for budget comparison
   */
  public String generateComparisonContent(
      EnvYear originalYear,
      EnvYear modifiedYear,
      double totalBudget) {
    Map<String, Double> originalTotals = calculateSectorTotals(originalYear);
    Map<String, Double> modifiedTotals = calculateSectorTotals(modifiedYear);
    double originalTotalBudget =
        originalTotals.values().stream().mapToDouble(Double::doubleValue).sum();
    double modifiedTotalBudget =
        modifiedTotals.values().stream().mapToDouble(Double::doubleValue).sum();

    if (originalTotalBudget == 0.0) {
      originalTotalBudget = totalBudget;
    }

    if (modifiedTotalBudget == 0.0) {
      modifiedTotalBudget = totalBudget;
    }

    StringBuilder html = new StringBuilder();
    html.append("<h2 class='section-title'>Σύγκριση Προϋπολογισμού - Έτος ")
        .append(originalYear.getYear())
        .append("</h2>");
    html.append("<p class='description'>Πριν & Μετά τις Αλλαγές</p>");
    html.append(
        buildSectorComparisonTable(
        originalTotals,
        modifiedTotals,
        originalTotalBudget,
        modifiedTotalBudget));
    html.append(
        buildBarChartsComparison(
        originalTotals,
        modifiedTotals,
        originalTotalBudget,
        modifiedTotalBudget));
    html.append(buildTopChanges(originalTotals, modifiedTotals));
    html.append(buildConclusions(originalTotals, modifiedTotals, totalBudget));

    return html.toString();
  }

  /**
   * Calculates total budget amount for each sector.
   */
  private Map<String, Double> calculateSectorTotals(EnvYear year) {
    Map<String, Double> totals = new LinkedHashMap<>();
    if (year == null || year.getSectors() == null) {
      return totals;
    }

    for (EnvSector sector : year.getSectors()) {
      if (sector == null || sector.getJsonKey() == null) {
        continue;
  }

      double sectorTotal = 0.0;

      if (sector.getUnits() != null) {
        for (EnvUnit unit : sector.getUnits()) {
          if (unit == null || unit.getEntries() == null) {
            continue;
          }

          for (EnvEntry entry : unit.getEntries()) {
            if (entry != null) {
              sectorTotal += entry.getAmount();
            }
          }
        }
      }

      totals.put(sector.getJsonKey(), sectorTotal);
    }
    return totals;
  }

  /**
   * Builds HTML table for sector comparison.
   */
  private String buildSectorComparisonTable(
      Map<String, Double> original,
      Map<String, Double> modified,
                                           double originalTotalBudget, double modifiedTotalBudget) {
    Map<String, Double> origPercentages =
    calculateAdjustedPercentages(original, originalTotalBudget);
    Map<String, Double> modPercentages = calculateAdjustedPercentages(modified, modifiedTotalBudget);
    StringBuilder html = new StringBuilder();
    html.append(
        "<div style='margin-top: 32px; padding-top: 24px; "
        + "border-top: 2px solid #c8e6c9;'>"
    );
    html.append(
        "<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; "
        + "margin-bottom: 20px; text-align: center;'>Σύγκριση ανά Τομέα</h3>");
    html.append("<div style='overflow-x: auto;'>");
    html.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 20px;'>");
    html.append("<thead>");
    html.append("<tr style='background: #e8f5e9; border-bottom: 2px solid #0d4f1c;'>");
    html.append(
        "<th style='padding: 12px; text-align: left; color: #0d4f1c; "
        + "font-weight: 700;'>Τομέας</th>"
    );
    html.append(
        "<th style='padding: 12px; text-align: right; color: #0d4f1c; "
        + "font-weight: 700;'>Πριν</th>"
    );
    html.append(
        "<th style='padding: 12px; text-align: right; color: #0d4f1c; "
        + "font-weight: 700;'>Μετά</th>"
    );
    html.append(
        "<th style='padding: 12px; text-align: right; color: #0d4f1c; "
        + "font-weight: 700;'>Αλλαγή</th>"
    );
    html.append("</tr></thead><tbody>");
    for (String sectorKey : original.keySet()) {
      double origAmount = original.get(sectorKey);
      double modAmount = modified.getOrDefault(sectorKey, 0.0);
      double change = modAmount - origAmount;
      double changePercent = (origAmount > 0) ? (change / origAmount) * 100 : 0;
      String sectorName = translator.translateCategory(sectorKey);
      double origPercent = origPercentages.getOrDefault(sectorKey, 0.0);
      double modPercent = modPercentages.getOrDefault(sectorKey, 0.0);
      String arrow = change > 0.01 ? "↑" : (change < -0.01 ? "↓" : "→");

      html.append("<tr style='border-bottom: 1px solid #e8e8e8;'>");
      html.append(
          "<td style='padding: 12px; color: #1b5e20; font-weight: 600;'>"
        )
          .append(sectorName)
          .append("</td>");
      html.append("<td style='padding: 12px; text-align: right;'>");
      html.append(
          String.format(
          HELLENIC_LOCALE,
          "<span style='color: #2e7d32; font-weight: 600;'>%,.2f €</span>",
          origAmount
        )
      );
      html.append(formatPercent(origPercent, "#81c784"));
      html.append("</td>");
      html.append("<td style='padding: 12px; text-align: right;'>");
      html.append(
          String.format(
          HELLENIC_LOCALE,
          "<span style='color: #2e7d32; font-weight: 600;'>%,.2f €</span>",
          modAmount
        )
      );
      html.append(formatPercent(modPercent, "#81c784"));
      html.append("</td>");
      html.append("<td style='padding: 12px; text-align: right;'>");
      if (Math.abs(change) > 0.01) {
        String changeColor = change > 0.01 ? "#1b5e20" : "#c62828";
        html.append("<span style='color: ")
            .append(changeColor)
            .append("; font-weight: 700; font-size: 15px;'>")
            .append(arrow).append(" ")
            .append(String.format(HELLENIC_LOCALE, "%,.2f €", Math.abs(change)))
            .append("</span>");
        html.append("<br><span style='color: ")
            .append(changeColor)
            .append("; font-size: 13px; font-weight: 600;'>(")
            .append(formatPercentChange(changePercent)).append(")</span>");
        double percentDiff = modPercent - origPercent;
        if (Math.abs(percentDiff) > 0.01) {
          html.append("<br><span style='color: ")
              .append(changeColor)
              .append("; font-size: 12px;'>[ποσοστό: ")
              .append(formatPercentChange(percentDiff)).append("]</span>");
        }
      } else {
        html.append("<span style='color: #616161; font-weight: 600;'>→ 0,00 €</span>");
        html.append("<br><span style='color: #616161; font-size: 13px;'>(0,0%)</span>");
      }
      html.append("</td>");
      html.append("</tr>");
    }

    html.append("</tbody></table></div></div>");
    return html.toString();
  }

  /**
   * Builds side-by-side bar charts for before/after comparison.
   */
  private String buildBarChartsComparison(
      Map<String, Double> original,
      Map<String, Double> modified,
                                         double originalTotalBudget, double modifiedTotalBudget) {
    StringBuilder html = new StringBuilder();
    html.append(
        "<div style='margin-top: 32px; padding-top: 24px; "
        + "border-top: 2px solid #c8e6c9;'>"
    );
    html.append(
        "<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; "
        + "margin-bottom: 20px; text-align: center;'>Κατανομή Προϋπολογισμού</h3>"
    );
        String[] colors = {
        "#1b5e20",
        "#2e7d32",
        "#388e3c",
        "#43a047",
        "#66bb6a",
        "#81c784",
        "#a5d6a7",
        "#c8e6c9",
        "#4caf50",
        "#558b2f"
      };
    html.append(
        "<div style='display: grid; grid-template-columns: 1fr 1fr; "
        + "gap: 24px; margin-bottom: 20px;'>"
    );
    html.append("<div>");
    html.append(
        "<h4 style='text-align: center; color: #0d4f1c; "
        + "font-weight: 600; margin-bottom: 16px;'>Πριν τις Αλλαγές</h4>"
    );
    html.append(buildBarChartSide(original, originalTotalBudget, colors));
    html.append("</div>");
    html.append("<div>");
    html.append(
        "<h4 style='text-align: center; color: #0d4f1c; "
        + "font-weight: 600; margin-bottom: 16px;'>Μετά τις Αλλαγές</h4>"
    );
    html.append(buildBarChartSide(modified, modifiedTotalBudget, colors));
    html.append("</div>");
    html.append("</div></div>");
    return html.toString();
  }

  /**
   * Builds HTML for top changes section.
   */
  private String buildTopChanges(Map<String, Double> original, Map<String, Double> modified) {
    StringBuilder html = new StringBuilder();
    html.append(
        "<div style='margin-top: 32px; padding-top: 24px; "
        + "border-top: 2px solid #c8e6c9;'>"
    );
    html.append(
        "<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; "
        + "margin-bottom: 20px; text-align: center;'>Οι Μεγαλύτερες Αλλαγές</h3>"
    );
    List<SectorChange> changes = new ArrayList<>();
    for (String sectorKey : original.keySet()) {
      double origAmount = original.get(sectorKey);
      double modAmount = modified.getOrDefault(sectorKey, 0.0);
      double change = modAmount - origAmount;
      double changePercent = (origAmount > 0) ? (change / origAmount) * 100 : 0;
      changes.add(new SectorChange(sectorKey, change, changePercent));
    }
    changes.sort((a, b) -> Double.compare(Math.abs(b.absoluteChange), Math.abs(a.absoluteChange)));
    html.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 24px;'>");
    html.append("<div>");
    html.append(
        "<h4 style='color: #1b5e20; font-weight: 600; "
        + "margin-bottom: 12px;'>Μεγαλύτερες Αυξήσεις</h4>"
    );
    int increases = 0;
    for (SectorChange change : changes) {
      if (change.absoluteChange > 0.01 && increases < 3) {
        String name = translator.translateCategory(change.sectorKey);
        html.append(
            "<div style='padding: 10px; margin-bottom: 8px; background: #e8f5e9; "
            + "border-radius: 6px; border-left: 4px solid #1b5e20;'>"
        );
        html.append(
            String.format(
            HELLENIC_LOCALE,
            "<div style='font-weight: 600; color: #0d4f1c;'>%d. %s</div>",
            increases + 1,
            name
          )
        );
        html.append(
            String.format(
            HELLENIC_LOCALE,
            "<div style='color: #1b5e20; font-size: 14px; margin-top: 4px;'>"
            + "+%,.2f € (+%.1f%%)</div>",
            change.absoluteChange,
            change.percentChange
          )
        );
        html.append("</div>");
        increases++;
      }
    }
    if (increases == 0) {
      html.append("<div style='color: #616161; font-style: italic;'>(Δεν υπάρχουν αυξήσεις)</div>");
    }
    html.append("</div>");

    html.append("<div>");
    html.append(
        "<h4 style='color: #c62828; font-weight: 600; "
        + "margin-bottom: 12px;'>Μεγαλύτερες Μειώσεις</h4>"
    );
    int decreases = 0;
    for (int i = changes.size() - 1; i >= 0 && decreases < 3; i--) {
      SectorChange change = changes.get(i);
      if (change.absoluteChange < -0.01) {
        String name = translator.translateCategory(change.sectorKey);
        html.append(
            "<div style='padding: 10px; margin-bottom: 8px; background: #ffebee; "
            + "border-radius: 6px; border-left: 4px solid #c62828;'>"
        );
        html.append(
            String.format(
            HELLENIC_LOCALE,
            "<div style='font-weight: 600; color: #b71c1c;'>%d. %s</div>",
            decreases + 1,
            name
          )
        );
        html.append(
            String.format(
            HELLENIC_LOCALE,
            "<div style='color: #c62828; "
            + "font-size: 14px; margin-top: 4px;'>"
            + "%,.2f € (%.1f%%)</div>",
            change.absoluteChange,
            change.percentChange
          )
        );
        html.append("</div>");
        decreases++;
      }
    }
    if (decreases == 0) {
      html.append("<div style='color: #616161; font-style: italic;'>(Δεν υπάρχουν μειώσεις)</div>");
    }
    html.append("</div>");

    html.append("</div></div>");
    return html.toString();
  }

  /**
   * Builds HTML for conclusions section.
   */
  private String buildConclusions(
      Map<String, Double> original,
      Map<String, Double> modified,
      double totalBudget
  ) {
    StringBuilder html = new StringBuilder();
    html.append(
        "<div style='margin-top: 32px; padding-top: 24px; "
        + "border-top: 2px solid #c8e6c9;'>"
   );
    html.append(
        "<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; "
        + "margin-bottom: 20px; text-align: center;'>Συμπεράσματα</h3>"
   );
    double totalOriginal = original.values().stream().mapToDouble(Double::doubleValue).sum();
    double totalModified = modified.values().stream().mapToDouble(Double::doubleValue).sum();
    double totalChange = totalModified - totalOriginal;

    html.append(
        "<div style='padding: 16px; background: #f1f8e9; "
        + "border: 2px solid #0d4f1c; border-radius: 10px;'>"
    );
    html.append(
        "<h4 style='color: #0d4f1c; font-weight: 600; "
        + "margin-bottom: 12px;'>Ισοσκέλιση Προϋπολογισμού</h4>"
    );
    if (Math.abs(totalChange) < 0.01) {
    html.append(
        "<p style='color: #1b5e20; font-weight: 600;'>"
        + "Ο προϋπολογισμός είναι πλήρως ισοσκελισμένος!</p>"
    );
    } else {
      html.append(
          String.format(
          HELLENIC_LOCALE,
          "<p style='color: #b71c1c; font-weight: 600;'>"
          + "Διαφορά: %,.2f € (χρειάζεται περαιτέρω προσαρμογές)</p>",
          totalChange
      )
      );
    }
    html.append("</div>");

    int sectorsIncreased = 0;
    int sectorsDecreased = 0;
    String maxIncreaseSector = "";
    double maxIncrease = 0.0;
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

    html.append(
          "<div style='margin-top: 16px; padding: 16px; background: #ffffff; "
          + "border: 1px solid #c8e6c9; border-radius: 10px;'>"
    );
    html.append(
          "<h4 style='color: #0d4f1c; font-weight: 600; "
          + "margin-bottom: 12px;'>Εστίαση Αλλαγών</h4>"
    );
    if (sectorsIncreased > 0 && !maxIncreaseSector.isEmpty()) {
      String name = translator.translateCategory(maxIncreaseSector);
      html.append(
          String.format(
          HELLENIC_LOCALE,
          "<p style='color: #1b5e20; margin-bottom: 8px;'>"
          + "<strong>Κύρια εστίαση:</strong> %s (+%,.2f €)</p>",
          name,
          maxIncrease
    )
   );
    }
    html.append(
      String.format(
      HELLENIC_LOCALE,
      "<p style='color: #1b5e20;'><strong>Τομείς με αύξηση:</strong> %d</p>",
      sectorsIncreased
      )
    );
    html.append(
          String.format(
          HELLENIC_LOCALE,
          "<p style='color: #1b5e20;'><strong>Τομείς με μείωση:</strong> %d</p>",
          sectorsDecreased
    )
    );
    if (sectorsIncreased == 0 && sectorsDecreased == 0) {
    html.append("<p style='color: #616161; font-style: italic;'>Δεν έγιναν σημαντικές αλλαγές</p>");
    }
    html.append("</div>");

    html.append("</div>");
    return html.toString();
  }

  private Map<String, Double> calculateAdjustedPercentages(
      Map<String, Double> totals,
      double totalBudget
  ) {
    Map<String, Double> percentages = new LinkedHashMap<>();
    List<String> keys = new ArrayList<>(totals.keySet());
    List<Double> amounts = new ArrayList<>();
    List<Double> percents = new ArrayList<>();
    for (String key : keys) {
      double amount = totals.get(key);
      amounts.add(amount);
      double percent = totalBudget > 0 ? (amount / totalBudget) * 100.0 : 0.0;
      percents.add(percent);
    }
    double sumPercentages = 0.0;
    for (int i = 0; i < percents.size(); i++) {
      double rounded = Math.round(percents.get(i) * 10.0) / 10.0;
      percents.set(i, rounded);
      sumPercentages += rounded;
    }
    if (Math.abs(sumPercentages - 100.0) > 0.001 && percents.size() > 0) {
      int largestIndex = 0;
      for (int i = 1; i < amounts.size(); i++) {
        if (amounts.get(i) > amounts.get(largestIndex)) {
          largestIndex = i;
        }
      }
      percents.set(largestIndex, percents.get(largestIndex) + (100.0 - sumPercentages));
    }
    for (int i = 0; i < keys.size(); i++) {
      percentages.put(keys.get(i), percents.get(i));
    }
    return percentages;
  }

  private String formatPercent(double percent, String color) {
    String format = Math.abs(percent - Math.round(percent)) < 0.01 ? "%.0f%%" : "%.1f%%";
    return String.format(
    HELLENIC_LOCALE,
    "<br><span style='color: %s; font-size: 13px;'>("
        + format
        + ")</span>",
    color,
    percent
);
  }
  private String formatPercentChange(double change) {
    return Math.abs(change - Math.round(change)) < 0.01 
        ? String.format(HELLENIC_LOCALE, "%+.0f%%", change)
        : String.format(HELLENIC_LOCALE, "%+.1f%%", change);
  }

  private String buildBarChartSide(
      Map<String, Double> totals,
      double totalBudget,
      String[] colors
  ) {
    Map<String, Double> percentages = calculateAdjustedPercentages(totals, totalBudget);
    StringBuilder html = new StringBuilder();
    int idx = 0;
    for (String sectorKey : totals.keySet()) {
      String sectorName = translator.translateCategory(sectorKey);
      double percent = percentages.getOrDefault(sectorKey, 0.0);
      String color = colors[idx % colors.length];
      html.append("<div style='margin-bottom: 12px;'>");
      html.append(
          "<div style='display: flex; justify-content: space-between; "
          + "margin-bottom: 4px;'>"
      );
      html.append(
          "<span style='font-size: 13px; font-weight: 600; color: #1b5e20;'>"
      )
          .append(sectorName)
          .append("</span>");
      html.append(
          String.format(
          HELLENIC_LOCALE,
          "<span style='font-size: 13px; font-weight: 600; color: #2e7d32;'>"
          + "%.1f%%</span>",
          percent
      )
      );     
      html.append("</div>");
      html.append(
          "<div style='background: #e8e8e8; border-radius: 4px; "
          + "height: 20px; overflow: hidden;'>"
      );
      html.append(
          String.format(
          "<div style='background: %s; height: 100%%; width: %.1f%%; "
          + "border-radius: 4px;'></div>",
          color,
          percent
      )
      );
      html.append("</div></div>");
      idx++;
    }
    return html.toString();
  }

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
