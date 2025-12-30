package gr.det.spinnovators.service;

/**
 * Service for validating budget changes.
 * This class is decoupled from UI concerns.
 */
public class BudgetValidator {

  private static final double EXTREME_DEVIATION_THRESHOLD = 30.0;

  /**
   * Possible validation outcomes.
   */
  public enum ValidationResult {
    OK,
    NEGATIVE_VALUE,
    EXCEEDS_TOTAL_BUDGET,
    EXTREME_DEVIATION
  }

  /**
   * Validates the proposed budget value.
   *
   * @param totalBudget The maximum allowed budget.
   * @param oldValue    The previous budget value.
   * @param newValue    The new proposed value.
   * @return The result of the validation.
   */
  public ValidationResult validate(double totalBudget, double oldValue, double newValue) {
    if (newValue < 0) {
      return ValidationResult.NEGATIVE_VALUE;
    }

    if (newValue > totalBudget) {
      return ValidationResult.EXCEEDS_TOTAL_BUDGET;
    }

    if (isExtremeDeviation(oldValue, newValue)) {
      return ValidationResult.EXTREME_DEVIATION;
    }

    return ValidationResult.OK;
  }

  /**
   * Internal check for extreme changes.
   */
  private boolean isExtremeDeviation(double oldValue, double newValue) {
    if (oldValue == 0) {
      return newValue > 0;
    }
    return calculateDeviationPercentage(oldValue, newValue) > EXTREME_DEVIATION_THRESHOLD;
  }

  /**
   * Calculates the absolute percentage change.
   *
   * @param oldValue Previous value.
   * @param newValue Proposed value.
   * @return The percentage (0-100).
   */
  public double calculateDeviationPercentage(double oldValue, double newValue) {
    if (oldValue == 0) {
      return 100.0;
    }
    return Math.abs((newValue - oldValue) / oldValue) * 100.0;
  }
}
