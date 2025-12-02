package gr.det.spinnovators.envdatamodel;

import java.util.Map;

/**
 * Model class that stores all budget data grouped by year.
 */

public class EnvBudgetData {

  private final Map<String, EnvYear> dataByYear;

  // Total budget per year (env_ministry_total_budget)
  private final Map<String, Double> envMinistryTotalBudget;
  // Constructor
  
  public EnvBudgetData(Map<String, EnvYear> dataByYear,
                       Map<String, Double> envMinistryTotalBudget) {
    this.dataByYear = dataByYear;
    this.envMinistryTotalBudget = envMinistryTotalBudget;
  }
  
  /* 
   * Getter methods that return an EnvYear object for a specific 
   * year - or null if it does not exist - and the total budjet per year
   */
  public EnvYear getBudgetForYear(String year) {
    if (dataByYear == null) {
      return null;
    }
    return dataByYear.get(year);
  } 

  public Map<String, Double> getEnvMinistryTotalBudget() {
    return envMinistryTotalBudget;
  } 
}
