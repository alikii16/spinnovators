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
public class ESG_Report {
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
  public ESG_Report(String year, double totalBudget,
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
  public String getYear() {
    return year;
  }

  public double getTotalBudget() {
    return totalBudget;
  }

  public double getEnvironmentalAmount() {
    return environmentalAmount;
  }

  public double getSocialAmount() {
    return socialAmount;
  }

  public double getGovernanceAmount() {
    return governanceAmount;
  }

  public double getNeutralAmount() {
    return neutralAmount;
  }

  public double getEnvironmentalScore() {
    return environmentalScore;
  }

  public double getSocialScore() {
    return socialScore;
  }

  public double getGovernanceScore() {
    return governanceScore;
  }

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
