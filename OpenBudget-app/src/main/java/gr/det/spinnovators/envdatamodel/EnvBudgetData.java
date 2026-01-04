package gr.det.spinnovators.envdatamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class that stores all environmental budget data grouped by year.
 * This class acts as a data container, mapping fiscal years to their 
 * respective environmental budget details and total ministry allocations.
 */

public class EnvBudgetData {

  private final Map<String, EnvYear> dataByYear;

  // Total budget per year (env_ministry_total_budget)
  private final Map<String, Double> envMinistryTotalBudget;
  // Constructor

  /**
   * Constructs an EnvBudgetData instance with the specified budget mappings.
   *
   * @param dataByYear A map containing detailed budget data per year.
   * @param envMinistryTotalBudget A map containing the total budget amounts per year.
   */
  
  public EnvBudgetData(Map<String, EnvYear> dataByYear,
                       Map<String, Double> envMinistryTotalBudget) {
    this.dataByYear = dataByYear;
    this.envMinistryTotalBudget = envMinistryTotalBudget != null ? new HashMap<>(envMinistryTotalBudget) : new HashMap<>();
  }
  
  /**
   *  Retrieves the detailed environmental budget for a specific year.
   *
   * @param year The fiscal year to search for (e.g., "2025").
   * @return The EnvYear object containing the year's data, or null if not found.
   */

  public EnvYear getBudgetForYear(String year) {
    return dataByYear.get(year);
  } // Returns EnvYear object for a specific year

  /**
   * Retrieves the map of total ministry budgets for all available years.
   *
   * @return A map where keys are years and values are the total budget amounts.
   */
  public Map<String, Double> getEnvMinistryTotalBudget() {
    return Collections.unmodifiableMap(envMinistryTotalBudget);
  } // Getter for total budget per year
}
