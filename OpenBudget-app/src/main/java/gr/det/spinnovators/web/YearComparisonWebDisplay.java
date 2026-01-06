package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import gr.det.spinnovators.service.EsgScoreCalculator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Handles the generation of year-to-year budget comparison content for the web interface.
 *
 * <p>This class compares budgets of two different years and formats them into HTML fragments
 * to be displayed in the web application's year comparison page.
 */
public class YearComparisonWebDisplay {
  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
  private final EnvBudgetTranslator translator;
  private final EsgScoreCalculator esgCalculator;

  public YearComparisonWebDisplay(EnvBudgetTranslator translator) {
    this.translator = translator;
    this.esgCalculator = new EsgScoreCalculator();
  }

  /**
   * Generates the complete year-to-year comparison content HTML.
   *
   * @param baseYear First year for comparison
   * @param compareYear Second year for comparison
   * @param totalBudgets Map of year to total budget (can be null)
   * @return Complete HTML content for year-to-year comparison
   */
  public String generateComparisonContent(EnvYear baseYear, EnvYear compareYear, Map<String, Double> totalBudgets) {
    String baseYearStr = baseYear.getYear();
    String compareYearStr = compareYear.getYear();
    Map<String, Double> baseSectors = calculateSectorTotals(baseYear);
    Map<String, Double> compareSectors = calculateSectorTotals(compareYear);
    Set<String> allSectors = new LinkedHashSet<>(baseSectors.keySet());
    allSectors.addAll(compareSectors.keySet());
    double baseTotal = baseSectors.values().stream().mapToDouble(Double::doubleValue).sum();
    double compareTotal = compareSectors.values().stream().mapToDouble(Double::doubleValue).sum();
    if (totalBudgets != null) {
      Double baseFromMap = totalBudgets.get(baseYearStr);
      Double compareFromMap = totalBudgets.get(compareYearStr);
      if (baseFromMap != null) baseTotal = baseFromMap;
      if (compareFromMap != null) compareTotal = compareFromMap;
    }
    StringBuilder html = new StringBuilder();
    html.append("<h2 class='section-title'>Σύγκριση Προϋπολογισμού: ").append(baseYearStr).append(" vs ").append(compareYearStr).append("</h2>");
    html.append("<p class='description'>Σύγκριση ανά Τομέα</p>");
    html.append(buildComparisonTable(baseSectors, compareSectors, baseYearStr, compareYearStr, allSectors));
    html.append(buildTotalChange(baseSectors, compareSectors));
    html.append(buildEsgComparison(baseYear, compareYear, baseTotal, compareTotal, baseYearStr, compareYearStr));
    return html.toString();
  }

  private Map<String, Double> calculateSectorTotals(EnvYear year) {
    Map<String, Double> totals = new LinkedHashMap<>();
    if (year == null || year.getSectors() == null) return totals;
    for (EnvSector sector : year.getSectors()) {
      if (sector == null || sector.getJsonKey() == null) continue;
      double sectorTotal = 0.0;
      if (sector.getUnits() != null) {
        for (EnvUnit unit : sector.getUnits()) {
          if (unit == null || unit.getEntries() == null) continue;
          for (EnvEntry entry : unit.getEntries()) {
            if (entry != null) sectorTotal += entry.getAmount();
          }
        }
      }
      totals.put(sector.getJsonKey(), sectorTotal);
    }
    return totals;
  }

  private String buildComparisonTable(Map<String, Double> baseSectors, Map<String, Double> compareSectors,
                                      String baseYear, String compareYear, Set<String> allSectors) {
    StringBuilder html = new StringBuilder();
    html.append("<div style='margin-top: 32px; padding-top: 24px; border-top: 2px solid #c8e6c9;'>");
    html.append("<div style='overflow-x: auto;'>");
    html.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 20px;'>");
    html.append("<thead><tr style='background: #e8f5e9; border-bottom: 2px solid #0d4f1c;'>");
    html.append("<th style='padding: 12px; text-align: left; color: #0d4f1c; font-weight: 700;'>Τομέας</th>");
    html.append("<th style='padding: 12px; text-align: right; color: #0d4f1c; font-weight: 700;'>").append(baseYear).append("</th>");
    html.append("<th style='padding: 12px; text-align: right; color: #0d4f1c; font-weight: 700;'>").append(compareYear).append("</th>");
    html.append("<th style='padding: 12px; text-align: right; color: #0d4f1c; font-weight: 700;'>Διαφορά</th>");
    html.append("<th style='padding: 12px; text-align: right; color: #0d4f1c; font-weight: 700;'>%</th>");
    html.append("</tr></thead><tbody>");
    for (String sectorKey : allSectors) {
      double baseAmount = baseSectors.getOrDefault(sectorKey, 0.0);
      double compareAmount = compareSectors.getOrDefault(sectorKey, 0.0);
      double diff = compareAmount - baseAmount;
      double pct = (baseAmount == 0) ? (compareAmount == 0 ? 0 : 100.0) : (diff / baseAmount) * 100.0;
      String sectorName = translator.translateCategory(sectorKey);
      if (sectorName.length() > 45) sectorName = sectorName.substring(0, 42) + "...";
      String diffColor = diff > 0.01 ? "#1b5e20" : (diff < -0.01 ? "#c62828" : "#616161");
      String arrow = diff > 0.01 ? "↑" : (diff < -0.01 ? "↓" : "→");
      html.append("<tr style='border-bottom: 1px solid #e8e8e8;'>");
      html.append("<td style='padding: 12px; color: #1b5e20; font-weight: 600;'>").append(sectorName).append("</td>");
      html.append("<td style='padding: 12px; text-align: right;'>");
      html.append(String.format(HELLENIC_LOCALE, "<span style='color: #2e7d32; font-weight: 600;'>%,.0f €</span>", baseAmount));
      html.append("</td><td style='padding: 12px; text-align: right;'>");
      html.append(String.format(HELLENIC_LOCALE, "<span style='color: #2e7d32; font-weight: 600;'>%,.0f €</span>", compareAmount));
      html.append("</td><td style='padding: 12px; text-align: right;'>");
      html.append("<span style='color: ").append(diffColor).append("; font-weight: 700;'>").append(arrow).append(" ")
          .append(String.format(HELLENIC_LOCALE, "%,.0f €", Math.abs(diff))).append("</span>");
      html.append("</td><td style='padding: 12px; text-align: right;'>");
      html.append(String.format(HELLENIC_LOCALE, "<span style='color: %s; font-weight: 600;'>%+.1f%%</span>", diffColor, pct));
      html.append("</td></tr>");
    }
    html.append("</tbody></table></div></div>");
    return html.toString();
  }

  private String buildTotalChange(Map<String, Double> baseSectors, Map<String, Double> compareSectors) {
    double totalDiff = compareSectors.values().stream().mapToDouble(Double::doubleValue).sum()
        - baseSectors.values().stream().mapToDouble(Double::doubleValue).sum();
    String diffColor = totalDiff > 0.01 ? "#1b5e20" : (totalDiff < -0.01 ? "#c62828" : "#616161");
    return "<div style='margin-top: 32px; padding-top: 24px; border-top: 2px solid #c8e6c9;'>"
        + "<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; margin-bottom: 20px; text-align: center;'>Συνολική Αλλαγή Προϋπολογισμού</h3>"
        + "<div style='padding: 16px; background: #f1f8e9; border: 2px solid #0d4f1c; border-radius: 10px; text-align: center;'>"
        + String.format(HELLENIC_LOCALE, "<p style='color: %s; font-weight: 700; font-size: 18px;'>Διαφορά: %+,.2f €</p>", diffColor, totalDiff)
        + "</div></div>";
  }

  private String buildEsgComparison(EnvYear baseYear, EnvYear compareYear, double baseTotal, double compareTotal,
                                    String baseYearStr, String compareYearStr) {
    EsgReport baseReport = esgCalculator.calculateReport(baseYear, baseTotal);
    EsgReport compareReport = esgCalculator.calculateReport(compareYear, compareTotal);
    double scoreDiff = compareReport.getOverallScore() - baseReport.getOverallScore();
    String message = scoreDiff > 0 ? "ΒΕΛΤΙΩΣΗ" : (scoreDiff < 0 ? "ΕΠΙΔΕΙΝΩΣΗ" : "ΚΑΜΙΑ ΑΛΛΑΓΗ");
    String feedbackMsg = scoreDiff > 2.0 ? "Εξαιρετικά! Η αλλαγή βελτιώνει σημαντικά τη βιωσιμότητα!" :
        (scoreDiff > 0 ? "Καλή αλλαγή! Μικρή βελτίωση στη βιωσιμότητα." :
        (scoreDiff < -2.0 ? "ΠΡΟΣΟΧΗ: Η αλλαγή επιδεινώνει σημαντικά τη βιωσιμότητα!" :
        (scoreDiff < 0 ? "Η αλλαγή μειώνει ελαφρώς τη βιωσιμότητα." : "Η αλλαγή δεν επηρεάζει το ESG score.")));
    StringBuilder html = new StringBuilder();
    html.append("<div style='margin-top: 32px; padding-top: 24px; border-top: 2px solid #c8e6c9;'>");
    html.append("<h3 style='font-size: 20px; font-weight: 700; color: #0d4f1c; margin-bottom: 20px; text-align: center;'>Σύγκριση ESG Score</h3>");
    html.append("<div style='display: flex; gap: 20px; margin-bottom: 32px; flex-wrap: wrap; justify-content: center;'>");
    html.append(buildEsgScoreBox("ESG Score " + baseYearStr, baseReport.getOverallScore(), "#e8f5e9", "#0d4f1c"));
    html.append(buildEsgScoreBox("ESG Score " + compareYearStr, compareReport.getOverallScore(), "#e8f5e9", "#0d4f1c"));
    html.append(buildEsgScoreBox("Διαφορά", Math.abs(scoreDiff), "#fff3e0", "#ff9800", message));
    html.append("</div>");
    html.append("<div style='margin-top: 24px; padding-top: 24px; border-top: 2px solid #c8e6c9;'>");
    html.append("<h4 style='font-size: 18px; font-weight: 700; color: #0d4f1c; margin-bottom: 16px; text-align: center;'>Λεπτομέρειες Αλλαγών</h4>");
    html.append("<div style='display: flex; flex-direction: column; gap: 12px;'>");
    html.append(buildEsgCategoryRow("Environmental (E)", baseReport.getEnvironmentalScore(), compareReport.getEnvironmentalScore()));
    html.append(buildEsgCategoryRow("Social (S)", baseReport.getSocialScore(), compareReport.getSocialScore()));
    html.append(buildEsgCategoryRow("Governance (G)", baseReport.getGovernanceScore(), compareReport.getGovernanceScore()));
    html.append("</div>");
    html.append("<div style='margin-top: 24px; padding: 16px 20px; background: #f1f8e9; border: 2px solid #0d4f1c; border-radius: 10px; text-align: center;'>");
    html.append("<p style='font-size: 16px; font-weight: 600; color: #1b5e20; margin: 0;'>").append(feedbackMsg).append("</p>");
    html.append("</div></div></div>");
    return html.toString();
  }

  private String buildEsgScoreBox(String label, double value, String bgColor, String borderColor) {
    return buildEsgScoreBox(label, value, bgColor, borderColor, null);
  }

  private String buildEsgScoreBox(String label, double value, String bgColor, String borderColor, String message) {
    StringBuilder html = new StringBuilder();
    html.append("<div style='flex: 1; min-width: 180px; background: ").append(bgColor).append("; border: 2px solid ").append(borderColor)
        .append("; border-radius: 12px; padding: 20px; text-align: center;'>");
    html.append("<div style='font-size: 14px; color: #2e7d32; font-weight: 600; margin-bottom: 8px;'>").append(label).append("</div>");
    if (message == null) {
      html.append(String.format(HELLENIC_LOCALE, "<div style='font-size: 32px; font-weight: 700; color: #0d4f1c; margin-bottom: 4px;'>%.2f / 100</div>", value));
    } else {
      html.append(String.format(HELLENIC_LOCALE, "<div style='font-size: 32px; font-weight: 700; color: #0d4f1c; margin-bottom: 4px;'>%.2f points</div>", value));
      html.append("<div style='font-size: 12px; color: #1b5e20; font-weight: 600; margin-top: 8px;'>").append(message).append("</div>");
    }
    html.append("</div>");
    return html.toString();
  }

  private String buildEsgCategoryRow(String label, double baseScore, double compareScore) {
    double diff = compareScore - baseScore;
    String[] diffInfo = getDiffInfo(diff);
    String bg = diffInfo[0].equals("positive") ? "#e8f5e9" : (diffInfo[0].equals("negative") ? "#ffebee" : "#f5f5f5");
    String color = diffInfo[0].equals("positive") ? "#1b5e20" : (diffInfo[0].equals("negative") ? "#c62828" : "#616161");
    return "<div style='display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #f9f9f9; border-radius: 8px; border: 1px solid #c8e6c9;'>"
        + "<div style='font-weight: 600; color: #1b5e20; font-size: 15px;'>" + label + "</div>"
        + "<div style='display: flex; gap: 12px; align-items: center;'>"
        + String.format(HELLENIC_LOCALE, "<span style='font-size: 15px; color: #2e7d32; font-weight: 600;'>%.1f%%</span>", baseScore)
        + "<span style='color: #81c784;'>→</span>"
        + String.format(HELLENIC_LOCALE, "<span style='font-size: 15px; color: #2e7d32; font-weight: 600;'>%.1f%%</span>", compareScore)
        + "<span style='padding: 4px 8px; border-radius: 4px; font-weight: 700; background: " + bg + "; color: " + color + ";'>" + diffInfo[1]
        + String.format(HELLENIC_LOCALE, "%.1f%%", Math.abs(diff)) + "</span>"
        + "</div></div>";
  }

  private String[] getDiffInfo(double diff) {
    return new String[]{
      diff > 0 ? "positive" : (diff < 0 ? "negative" : "neutral"),
      diff > 0 ? "↑" : (diff < 0 ? "↓" : "→")
    };
  }
}
