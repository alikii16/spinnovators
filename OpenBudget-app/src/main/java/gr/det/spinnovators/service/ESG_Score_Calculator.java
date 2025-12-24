package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.ESG_Category;
import gr.det.spinnovators.envdatamodel.ESG_Report;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates ESG sustainability scores for ministry budgets.
 *
 * Analyzes budget data, classifies expenses into ESG categories,
 * and computes a weighted overall sustainability score from 0 to 100.
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class ESG_Score_Calculator {

  private static final double WEIGHT_ENVIRONMENTAL = 0.40;
  private static final double WEIGHT_SOCIAL = 0.30;
  private static final double WEIGHT_GOVERNANCE = 0.30;
  private final ESG_Classifier classifier;

  /**
   * Constructs an ESG score calculator with default classifier.
   */
  public ESG_Score_Calculator() {
    this.classifier = new ESG_Classifier();
  }

  /**
   * Calculates a complete ESG report for a given year's budget.
   *
   * @param year The budget year to analyze
   * @param totalBudget The total ministry budget for that year
   * @return Complete ESG report with scores and breakdowns
   */
  public ESG_Report calculateReport(EnvYear year, double totalBudget) {
    // Aggregate amounts by ESG category
    Map<ESG_Category, Double> categoryAmounts = aggregateByCategory(year);

    double envAmount = categoryAmounts.getOrDefault(ESG_Category.ENVIRONMENTAL, 0.0);
    double socAmount = categoryAmounts.getOrDefault(ESG_Category.SOCIAL, 0.0);
    double govAmount = categoryAmounts.getOrDefault(ESG_Category.GOVERNANCE, 0.0);
    double neutAmount = categoryAmounts.getOrDefault(ESG_Category.NEUTRAL, 0.0);

    // Calculate individual scores (percentage of total budget)
    double envScore = (envAmount / totalBudget) * 100.0;
    double socScore = (socAmount / totalBudget) * 100.0;
    double govScore = (govAmount / totalBudget) * 100.0;

    // Calculate weighted overall score
    double overallScore = (envScore * WEIGHT_ENVIRONMENTAL) +
                          (socScore * WEIGHT_SOCIAL) +
                          (govScore * WEIGHT_GOVERNANCE);

    return new ESG_Report(
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
   * @param year The budget year to analyze
   * @return Map of ESG category to total amount
   */
  private Map<ESG_Category, Double> aggregateByCategory(EnvYear year) {
    Map<ESG_Category, Double> totals = new HashMap<>();

    // Initialize all categories with 0
    for (ESG_Category category : ESG_Category.values()) {
      totals.put(category, 0.0);
    }

    // Iterate through all sectors, units, and entries
    for (EnvSector sector : year.getSectors()) {
      String sectorKey = sector.getJsonKey();

      for (EnvUnit unit : sector.getUnits()) {
        for (EnvEntry entry : unit.getEntries()) {
          ESG_Category category = classifier.classifyEntry(
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
   * Used to show users how their budget changes affect sustainability.
   *
   * @param before ESG report before changes
   * @param after ESG report after changes
   * @return Difference in overall score (positive = improvement)
   */
  public double calculateScoreDifference(ESG_Report before, ESG_Report after) {
    return after.getOverallScore() - before.getOverallScore();
  }
}
