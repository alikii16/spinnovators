package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EsgCategory;

/**
 * Service for validating budget changes with Smart ESG Logic.
 * Enforces sustainability-oriented policies that naturally steer the budget
 * towards higher ESG (Environmental, Social, Governance) scores.
 *
 * <p>This validator implements a dual-tier warning system:
 * <ul>
 *   <li><b>Yellow Card</b>: Generic warnings for extreme deviations (over 30%)</li>
 *   <li><b>Red Card</b>: Category-specific restrictions based on ESG principles</li>
 * </ul>
 * </p>
 *
 * <p>The ESG-based validation rules are:
 * <ul>
 *   <li><b>Environmental (E)</b>: Protects green investments, blocks cuts over 5%</li>
 *   <li><b>Social (S)</b>: Protects personnel welfare, blocks cuts over 10%</li>
 *   <li><b>Governance (G)</b>: Prevents bureaucracy expansion, blocks increases over 10%</li>
 *   <li><b>Neutral</b>: Standard 30% deviation limit applies</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class BudgetValidator {

  /** Standard threshold percentage for generic warnings (Yellow Card). */
  private static final double STANDARD_LIMIT = 30.0;
  private final EsgClassifier classifier;

  /**
   * Enumeration of possible validation outcomes including specific ESG warnings.
   *
   * <p>The outcomes are categorized as:
   * <ul>
   *   <li>Basic validations: OK, NEGATIVE_VALUE, EXCEEDS_TOTAL_BUDGET</li>
   *   <li>Yellow Card: EXTREME_DEVIATION (generic high deviation warning)</li>
   *   <li>Red Cards: ESG_ENV_PROTECTION, ESG_GOV_RESTRICTION, ESG_SOCIAL_PROTECTION</li>
   * </ul>
   * </p>
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

  /**
   * Constructs a BudgetValidator with an initialized ESG classifier.
   *
   * <p>The classifier is used to categorize budget entries into
   * Environmental, Social, Governance, or Neutral categories.</p>
   */
  public BudgetValidator() {
    this.classifier = new EsgClassifier();
  }

  /**
   * Validates the proposed budget value considering ESG rules.
   *
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
   * Calculates the percentage deviation between old and new budget values.
   *
   * <p>This helper method computes the absolute percentage change.
   * Special handling is provided for the edge case where the old value is zero:
   * <ul>
   *   <li>If oldValue is 0 and newValue is positive, returns 100%</li>
   *   <li>If both are 0, returns 0%</li>
   * </ul>
   * </p>
   *
   * <p>Formula: |newValue - oldValue| / oldValue × 100</p>
   *
   * @param oldValue the original budget amount
   * @param newValue the proposed new budget amount
   * @return the absolute percentage deviation between the two values
   */
  public double calculateDeviationPercentage(double oldValue, double newValue) {
    if (oldValue == 0) {
      return newValue > 0 ? 100.0 : 0.0;
    }
    return Math.abs((newValue - oldValue) / oldValue) * 100.0;
  }
}
