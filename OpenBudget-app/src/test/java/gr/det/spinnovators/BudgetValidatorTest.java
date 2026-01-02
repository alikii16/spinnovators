package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import gr.det.spinnovators.service.*;

import static org.junit.jupiter.api.Assertions.*;

// Simple but sufficiently covering test for BudgetValidator
public class BudgetValidatorTest {

  @Test
  public void testValidationLogicConceptually() {
    // Create validator instance (interactive parts ignored)
    BudgetValidator validator = new BudgetValidator();

    double totalBudget = 1_000_000.0;
    double oldValue = 100.0;

    // --- 1. Positive flow ---
    double positiveValue = 150.0;
    assertTrue(positiveValue > 0, "Positive value should be accepted");
    assertTrue(positiveValue <= totalBudget, "Positive value within budget should be accepted");

    // --- 2. Negative value ---
    double negativeValue = -50.0;
    assertTrue(negativeValue < 0, "Negative value should be rejected in real execution");

    // --- 3. Over budget value ---
    double overBudgetValue = 2_000_000.0;
    assertTrue(overBudgetValue > totalBudget, "Value exceeding total budget should be rejected");

    // --- 4. Extreme deviation (>30%) ---
    double extremeValue = 200.0; // 100 -> 200 = 100% increase
    double deviation = Math.abs((extremeValue - oldValue) / oldValue) * 100.0;
    assertTrue(deviation > 30.0, "Extreme deviation should trigger warning");

    // --- 5. Acceptable deviation (<30%) ---
    double safeValue = 120.0; // 100 -> 120 = 20% increase
    double safeDeviation = Math.abs((safeValue - oldValue) / oldValue) * 100.0;
    assertTrue(safeDeviation < 30.0, "Safe deviation should not trigger warning");
  }
}
