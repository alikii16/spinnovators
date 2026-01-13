package gr.det.spinnovators.envdatamodel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Model class that stores all environmental budget data grouped by year.
 *
 * <p>This class acts as a data container, mapping fiscal years to their
 * respective environmental budget details and total ministry allocations.</p>
 *
 * @author Spinnovators Team
 * @version 1.1
 * @see EnvYear
 */
public class EnvBudgetData {

  private final Map<String, EnvYear> dataByYear;

  // Total budget per year (env_ministry_total_budget)
  private final Map<String, Double> envMinistryTotalBudget;

  /**
   * Constructs an EnvBudgetData instance with the specified budget mappings.
   *
   * @param dataByYear a map containing detailed budget data per year,
   * where keys are year strings and values are EnvYear objects
   * @param envMinistryTotalBudget a map containing the total budget amounts per year,
   * where keys are year strings and values are total budget amounts
   */
  public EnvBudgetData(Map<String, EnvYear> dataByYear,
                       Map<String, Double> envMinistryTotalBudget) {
    this.dataByYear = (dataByYear != null)
        ? new java.util.HashMap<>(dataByYear)
        : new java.util.HashMap<>();

    this.envMinistryTotalBudget = (envMinistryTotalBudget != null)
        ? new java.util.HashMap<>(envMinistryTotalBudget)
        : new java.util.HashMap<>();
  }

  /**
   * Auxiliary Constructor for Lists (Required for Testing).
   *
   * @param yearsList The list of EnvYear objects to load into the map.
   */
  public EnvBudgetData(List<EnvYear> yearsList) {
    this.dataByYear = new java.util.HashMap<>();
    if (yearsList != null) {
      for (EnvYear y : yearsList) {
        this.dataByYear.put(y.getYear(), y);
      }
    }
    this.envMinistryTotalBudget = new java.util.HashMap<>();
  }

  /**
   * Retrieves the detailed environmental budget for a specific year.
   *
   * @param year the fiscal year to search for (e.g., "2025")
   * @return the EnvYear object containing the year's data, or null if not found
   */
  public EnvYear getBudgetForYear(String year) {
    return dataByYear.get(year);
  }

  /**
   * Retrieves the map of total ministry budgets for all available years.
   *
   * @return an unmodifiable map where keys are years (e.g., "2025")
   * and values are the total budget amounts for the environmental ministry
   */
  public Map<String, Double> getEnvMinistryTotalBudget() {
    return Collections.unmodifiableMap(envMinistryTotalBudget);
  }
}