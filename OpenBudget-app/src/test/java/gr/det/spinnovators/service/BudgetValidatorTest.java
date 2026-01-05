package gr.det.spinnovators.service;

import gr.det.spinnovators.service.BudgetValidator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link BudgetValidator} class.
 * <p>
 * Designed to achieve 100% coverage by hitting every branch condition:
 * - Pre-checks (Negative, Exceeds)
 * - Switch cases (Env, Gov, Social) including all True/False combinations of their ifs
 * - Fall-through logic (Social/Neutral -> Final Check -> Final Return)
 * </p>
 */
public class BudgetValidatorTest {

  private BudgetValidator validator;
  private final double totalBudget = 1000.0;

  @BeforeEach
  public void setUp() {
    validator = new BudgetValidator();
  }

  // --- 1. BASIC PRE-CHECKS ---

  @Test
  public void testValidateNegativeValue() {
    ValidationResult result = validator.validate(totalBudget, 100.0, -50.0, "any", "any");
    Assertions.assertEquals(ValidationResult.NEGATIVE_VALUE, result);
  }

  @Test
  public void testValidateExceedsTotalBudget() {
    ValidationResult result = validator.validate(totalBudget, 100.0, 1500.0, "any", "any");
    Assertions.assertEquals(ValidationResult.EXCEEDS_TOTAL_BUDGET, result);
  }

  // --- 2. ENVIRONMENTAL RULES ---

  @Test
  public void testEsgEnv_DecreaseViolation() {
    // Environmental: Decrease 10% (> 5% allowed) -> Error
    ValidationResult result = validator.validate(totalBudget, 100.0, 90.0, 
        "natural_environment_and_water_protection", "any");
    Assertions.assertEquals(ValidationResult.ESG_ENV_PROTECTION, result);
  }

  @Test
  public void testEsgEnv_AllowedChanges() {
    // Environmental: Increase (Allowed)
    Assertions.assertEquals(ValidationResult.OK, 
        validator.validate(totalBudget, 100.0, 110.0, "natural_environment_and_water_protection", "any"));
        
    // Environmental: Small decrease 4% (Allowed)
    Assertions.assertEquals(ValidationResult.OK, 
        validator.validate(totalBudget, 100.0, 96.0, "natural_environment_and_water_protection", "any"));
  }

  // --- 3. GOVERNANCE RULES ---

  @Test
  public void testEsgGov_IncreaseViolation() {
    // Governance: Increase 20% (> 10% allowed) -> Error
    ValidationResult result = validator.validate(totalBudget, 100.0, 120.0, 
        "executive_coordination_and_investments", "purchase_of_goods_and_services");
    Assertions.assertEquals(ValidationResult.ESG_GOV_RESTRICTION, result);
  }

  @Test
  public void testEsgGov_AllowedChanges() {
    // Governance: Decrease (Allowed)
    Assertions.assertEquals(ValidationResult.OK, 
        validator.validate(totalBudget, 100.0, 90.0, 
        "executive_coordination_and_investments", "purchase_of_goods_and_services"));

    // Governance: Small increase 5% (Allowed)
    Assertions.assertEquals(ValidationResult.OK, 
        validator.validate(totalBudget, 100.0, 105.0, 
        "executive_coordination_and_investments", "purchase_of_goods_and_services"));
  }

  // --- 4. SOCIAL RULES (The missing link!) ---

  @Test
  public void testEsgSocial_DecreaseViolation() {
    // Social: Decrease 20% (> 10% allowed) -> Error
    ValidationResult result = validator.validate(totalBudget, 100.0, 80.0, 
        "any_sector", "personnel_costs");
    Assertions.assertEquals(ValidationResult.ESG_SOCIAL_PROTECTION, result);
  }

  /**
   * *** NEW TEST FOR 100% COVERAGE ***
   * Covers: Social AND Decrease AND Within Limits
   */
  @Test
  public void testEsgSocial_SmallDecrease_Allowed() {
    // Social: Decrease 5% (< 10% allowed)
    // Path: Case Social -> if(isDecrease && >10) is FALSE -> break -> Final OK
    ValidationResult result = validator.validate(totalBudget, 100.0, 95.0, 
        "any_sector", "personnel_costs");
    Assertions.assertEquals(ValidationResult.OK, result);
  }

  @Test
  public void testEsgSocial_ExtremeIncrease() {
    // Social: Increase 50% (> 30% limit) -> Extreme Deviation
    ValidationResult result = validator.validate(totalBudget, 100.0, 150.0, 
        "any_sector", "personnel_costs");
    Assertions.assertEquals(ValidationResult.EXTREME_DEVIATION, result);
  }

  @Test
  public void testEsgSocial_NormalIncrease_HitsFinalReturn() {
    // Social: Increase 10% (Safe) -> Final OK
    ValidationResult result = validator.validate(totalBudget, 100.0, 110.0, 
        "any_sector", "personnel_costs");
    Assertions.assertEquals(ValidationResult.OK, result);
  }

  // --- 5. NEUTRAL / DEFAULT ---

  @Test
  public void testNeutral_ExtremeDeviation() {
    // Neutral: > 30% change -> Error
    ValidationResult result = validator.validate(totalBudget, 100.0, 200.0, 
        "unknown_sector", "unknown_entry");
    Assertions.assertEquals(ValidationResult.EXTREME_DEVIATION, result);
  }

  @Test
  public void testNeutral_NormalChange_HitsFinalReturn() {
    // Neutral: Small change -> OK
    ValidationResult result = validator.validate(totalBudget, 100.0, 110.0, 
        "unknown_sector", "unknown_entry");
    Assertions.assertEquals(ValidationResult.OK, result);
  }

  // --- 6. UTILITIES ---

  @Test
  public void testCalculateDeviationPercentage() {
    Assertions.assertEquals(50.0, validator.calculateDeviationPercentage(100.0, 150.0), 0.01);
    Assertions.assertEquals(100.0, validator.calculateDeviationPercentage(0.0, 100.0), 0.01);
    Assertions.assertEquals(0.0, validator.calculateDeviationPercentage(0.0, 0.0), 0.01);
  }
  
  @Test
  public void testEnumValues() {
    Assertions.assertNotNull(ValidationResult.valueOf("OK"));
    Assertions.assertTrue(ValidationResult.values().length > 0);
  }
}
