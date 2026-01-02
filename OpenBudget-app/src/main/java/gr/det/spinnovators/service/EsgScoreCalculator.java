package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgCategory;
import gr.det.spinnovators.envdatamodel.EsgReport;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates ESG sustainability scores for ministry budgets.
 *
 * <p>Analyzes budget data, classifies expenses into ESG categories,
 * and computes a weighted overall sustainability score from 0 to 100.
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EsgScoreCalculator {

  private static final double WEIGHT_ENVIRONMENTAL = 0.40;
  private static final double WEIGHT_SOCIAL = 0.30;
  private static final double WEIGHT_GOVERNANCE = 0.30;
  private final EsgClassifier classifier;

  /**
   * Constructs an ESG score calculator with default classifier.
   */
  public EsgScoreCalculator() {
    this.classifier = new EsgClassifier();
  }

  /**
   * Calculates a complete ESG report for a given year's budget.
   *
   * @param year The budget year to analyze.
   * @param totalBudget The total ministry budget for that year.
   * @return Complete ESG report with scores and breakdowns.
   */
  public EsgReport calculateReport(EnvYear year, double totalBudget) {
    // Aggregate amounts by ESG category
    Map<EsgCategory, Double> categoryAmounts = aggregateByCategory(year);

    double envAmount = categoryAmounts.getOrDefault(EsgCategory.ENVIRONMENTAL, 0.0);
    double socAmount = categoryAmounts.getOrDefault(EsgCategory.SOCIAL, 0.0);
    double govAmount = categoryAmounts.getOrDefault(EsgCategory.GOVERNANCE, 0.0);
    double neutAmount = categoryAmounts.getOrDefault(EsgCategory.NEUTRAL, 0.0);

    // Calculate individual scores (percentage of total budget)
    double envScore = (envAmount / totalBudget) * 100.0;
    double socScore = (socAmount / totalBudget) * 100.0;
    double govScore = (govAmount / totalBudget) * 100.0;

    // Fixed OperatorWrap: '+' moved to new lines
    double overallScore = (envScore * WEIGHT_ENVIRONMENTAL)
        + (socScore * WEIGHT_SOCIAL)
        + (govScore * WEIGHT_GOVERNANCE);

    return new EsgReport(
      year.getYear(),
      totalBudget,
      envAmount,
      socAmount,
      govAmount,
      neutAmount,
      envScore,
      socScore,
      govScore,
      overallScore
    );
  }

  /**
   * Aggregates all budget entries by their ESG category.
   *
   * @param year The budget year to analyze.
   * @return Map of ESG category to total amount.
   */
  private Map<EsgCategory, Double> aggregateByCategory(EnvYear year) {
    Map<EsgCategory, Double> totals = new HashMap<>();

    // Initialize all categories with 0
    for (EsgCategory category : EsgCategory.values()) {
      totals.put(category, 0.0);
    }

    // Iterate through all sectors, units, and entries
    for (EnvSector sector : year.getSectors()) {
      // Fixed Indentation: expected 14 (inside 3 levels of nested loops)
      String sectorKey = sector.getJsonKey();

      for (EnvUnit unit : sector.getUnits()) {
        for (EnvEntry entry : unit.getEntries()) {
          EsgCategory category = classifier.classifyEntry(
              sectorKey, entry.getJsonKey()
          );

          double currentTotal = totals.get(category);
          totals.put(category, currentTotal + entry.getAmount());
        }
      }
    }
    return totals;
  }

  /**
   * Calculates the difference between two ESG reports.
   *
   * <p>Used to show users how their budget changes affect sustainability.
   * A positive result indicates an improvement.</p>
   *
   * @param before ESG report before changes.
   * @param after ESG report after changes.
   * @return Difference in overall score (positive = improvement).
   */
  public double calculateScoreDifference(EsgReport before, EsgReport after) {
    return after.getOverallScore() - before.getOverallScore();
  }
}
