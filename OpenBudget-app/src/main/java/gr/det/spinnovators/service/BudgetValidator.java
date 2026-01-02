package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EsgCategory;

/**
 * Service for validating budget changes with Smart ESG Logic.
 * Enforces policies that naturally steer the budget towards higher ESG scores.
 */
public class BudgetValidator {

  // Standard threshold for generic warnings (Yellow Card)
  private static final double STANDARD_LIMIT = 30.0;
  private final EsgClassifier classifier;

  /**
   * Possible validation outcomes including specific ESG warnings.
   */
  public enum ValidationResult {
    OK,
    NEGATIVE_VALUE,
    EXCEEDS_TOTAL_BUDGET,
    EXTREME_DEVIATION,      // Yellow Card: Generic high deviation warning
    ESG_ENV_PROTECTION,     // Red Card: Preventing cuts to environmental budget
    ESG_GOV_RESTRICTION,    // Red Card: Preventing hikes in governance/bureaucracy
    ESG_SOCIAL_PROTECTION   // Red Card: Preventing cuts to social salaries/benefits
  }

  public BudgetValidator() {
    this.classifier = new EsgClassifier();
  }

  /**
   * Validates the proposed budget value considering ESG rules.

   * @param totalBudget The max budget cap.
   * @param oldValue The current amount.
   * @param newValue The new amount proposed.
   * @param sectorKey The sector ID (needed for classification).
   * @param entryKey The entry ID (needed for classification).
   * @return The validation result.
   */
  public ValidationResult validate(double totalBudget, double oldValue, double newValue, 
                                   String sectorKey, String entryKey) {
    
    // 1. Basic Checks
    if (newValue < 0) {
      return ValidationResult.NEGATIVE_VALUE;
    }
    if (newValue > totalBudget) {
      return ValidationResult.EXCEEDS_TOTAL_BUDGET;
    }

    double percentChange = calculateDeviationPercentage(oldValue, newValue);
    boolean isDecrease = newValue < oldValue;
    boolean isIncrease = newValue > oldValue;

    // 2. Determine ESG Category (Environmental, Social, Governance)
    EsgCategory category = classifier.classifyEntry(sectorKey, entryKey);

    // 3. Apply Smart ESG Rules
    switch (category) {
      case ENVIRONMENTAL:
        // ENVIRONMENTAL (0.4 weight): Strict protection against cuts > 5%
        if (isDecrease && percentChange > 5.0) {
          return ValidationResult.ESG_ENV_PROTECTION;
        }
        // Increases are unrestricted (incentivizing green investment)
        return ValidationResult.OK;

      case GOVERNANCE:
        // GOVERNANCE (0.3 weight): Strict restriction against hikes > 10%
        if (isIncrease && percentChange > 10.0) {
          return ValidationResult.ESG_GOV_RESTRICTION;
        }
        return ValidationResult.OK;

      case SOCIAL:
        // SOCIAL: Protection against cuts > 10% (Salary/Benefit protection)
        if (isDecrease && percentChange > 10.0) {
          return ValidationResult.ESG_SOCIAL_PROTECTION;
        }
        break;
        
      default:
        break;
    }

    // 4. General rule for Neutral categories or Social increases
    if (percentChange > STANDARD_LIMIT) {
      return ValidationResult.EXTREME_DEVIATION;
    }

    return ValidationResult.OK;
  }

  /**
   * Helper to calculate percentage deviation.
   */
  public double calculateDeviationPercentage(double oldValue, double newValue) {
    if (oldValue == 0) {
      return newValue > 0 ? 100.0 : 0.0;
    }
    return Math.abs((newValue - oldValue) / oldValue) * 100.0;
  }
}
