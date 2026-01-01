package gr.det.spinnovators.envdatamodel;

/**
 * Represents a complete ESG sustainability report for a ministry budget.
 *
 * Contains breakdown of expenses by ESG category and calculates
 * an overall sustainability score from 0 to 100.
 *
 * @author Spinnovators Team
 * 
 * @version 1.0
 */
public class EsgReport {
  private final String year;
  private final double totalBudget;

  private final double environmentalAmount;
  private final double socialAmount;
  private final double governanceAmount;
  private final double neutralAmount;

  private final double environmentalScore;
  private final double socialScore;
  private final double governanceScore;
  private final double overallScore;

  /**
   * Constructs an ESG report with all calculated values.
   *
   * @param year The budget year
   * 
   * @param totalBudget Total budget amount
   * 
   * @param environmentalAmount Amount allocated to environmental expenses
   * 
   * @param socialAmount Amount allocated to social expenses
   * 
   * @param governanceAmount Amount allocated to governance expenses
   * 
   * @param neutralAmount Amount allocated to neutral expenses
   * 
   * @param environmentalScore Environmental score (0-100)
   * 
   * @param socialScore Social score (0-100)
   * 
   * @param governanceScore Governance score (0-100)
   * 
   * @param overallScore Overall weighted ESG score (0-100)
   */
  public EsgReport(String year, double totalBudget,
                     double environmentalAmount, double socialAmount,
                     double governanceAmount, double neutralAmount,
                     double environmentalScore, double socialScore,
                     double governanceScore, double overallScore) {
    this.year = year;
    this.totalBudget = totalBudget;
    this.environmentalAmount = environmentalAmount;
    this.socialAmount = socialAmount;
    this.governanceAmount = governanceAmount;
    this.neutralAmount = neutralAmount;
    this.environmentalScore = environmentalScore;
    this.socialScore = socialScore;
    this.governanceScore = governanceScore;
    this.overallScore = overallScore;
  }

  // Getters
  /** 
   * @return The fiscal year of the report. 
   */
  public String getYear() {
    return year;
  }

  /** 
   * @return The total budget amount.
   */
  public double getTotalBudget() {
    return totalBudget;
  }

  /** 
   * @return The total amount for environmental initiatives.
   */
  public double getEnvironmentalAmount() {
    return environmentalAmount;
  }

  /**
   * @return The total amount for social responsibility.
   */
  public double getSocialAmount() {
    return socialAmount;
  }

  /** 
   * @return The total amount for governance and administration.
   */
  public double getGovernanceAmount() {
    return governanceAmount;
  }

  /** 
   * @return The total amount of neutral expenses.
   */
  public double getNeutralAmount() {
    return neutralAmount;
  }

  /** 
   * @return The calculated environmental sustainability score.
   */
  public double getEnvironmentalScore() {
    return environmentalScore;
  }

  /** 
   * @return The calculated social responsibility score.
   */
  public double getSocialScore() {
    return socialScore;
  }

  /** 
   * @return The calculated governance and transparency score.
   */
  public double getGovernanceScore() {
    return governanceScore;
  }

  /** 
   * @return The final weighted ESG sustainability score.
   */
  public double getOverallScore() {
    return overallScore;
  }

  /**
   * Gets the sustainability rating based on overall score.
   *
   * @return Rating string: Excellent, Good, Moderate, Poor, or Critical
   */
  public String getRating() {
    if (overallScore >= 80) {
      return "Excellent";
    } else if (overallScore >= 60) {
      return "Good";
    } else if (overallScore >= 40) {
      return "Moderate";
    } else if (overallScore >= 20) {
      return "Poor";
    } else {
      return "Critical";
    }
  }

  /**
   * Gets the Greek translation of the rating.
   *
   * @return Greek rating string
   */
  public String getRatingGreek() {
    if (overallScore >= 80) {
      return "Άριστη";
    } else if (overallScore >= 60) {
      return "Καλή";
    } else if (overallScore >= 40) {
      return "Μέτρια";
    } else if (overallScore >= 20) {
      return "Χαμηλή";
    } else {
      return "Πολύ Χαμηλή";
    }
  }
}  
