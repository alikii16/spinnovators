package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the EditsApplier class.
 * This suite focuses on interactive editing cycles, ESG recalculations,
 * and input validation branches.
 */
public class EditsApplierTest {

  private EnvYear year2025;
  private EnvEntry envEntry;

  /**
   * Dummy translator for testing purposes.
   */
  static class DummyTranslator extends EnvBudgetTranslator {
    @Override
    public String translateCategory(String key) {
      return key;
    }
  }

  /**
   * Sets up the test environment with sample budget data.
   */
  @BeforeEach
  void setup() {
    // Using a key that triggers ESG rules (e.g., ENVIRONMENTAL)
    envEntry = new EnvEntry("env_protection_entry", 1000000.0);
    EnvUnit unit = new EnvUnit("unit1", List.of(envEntry));
    EnvSector sector = new EnvSector("sector_environmental", List.of(unit));
    year2025 = new EnvYear("2025", List.of(sector));
  }

  /**
   * THE BIG TEST: Covers successful changes, ESG recalculation, and cancellations.
   * Goal: Coverage for recalculateEsgScore & applyBudgetChange.
   */
  @Test
  public void testFullInteractiveCycle() {
    String simulatedInput = 
        "1\n"                       // Select Sector
        + "1\n"                     // Select Unit
        + "env_protection_entry\n"  // Select Entry
        + "1500000\n"               // Large deviation (+50%)
        + "NAI\n"                   // Confirm -> Triggers apply & recalculate
        + "1\n"                     // Back to Sector 1
        + "1\n"                     // Unit 1
        + "env_protection_entry\n"  // Entry again
        + "1000000\n"               // Restore for balancing (Balance = 0)
        + "0\n"                     // Exit from Units
        + "0\n";                    // Exit from Sectors (Success if balance < 0.01)

    Scanner scanner = createScanner(simulatedInput);
    EditsApplier applier = new EditsApplier(new DummyTranslator());
    
    applier.applyEditsToYear(year2025, scanner);
    
    // Verify if values changed and then returned to original
    assertEquals(1000000.0, envEntry.getAmount(), 
        "The amount should have returned to the original value.");
  }

  /**
   * ERROR TEST: Covers all error messages and catch blocks.
   * Goal: 100% Branch Coverage in handleValidationResult.
   */
  @Test
  public void testAllErrorBranches() {
    // Test with year 2026 for initialization coverage
    EnvYear year2026 = new EnvYear("2026", year2025.getSectors());
    
    String simulatedInput = 
        "1\n" + "1\n" + "env_protection_entry\n" 
        + "-100\n"           // Error: Negative amount
        + "99999999999\n"    // Error: Exceeds limit
        + "100\n"            // Error: ESG_ENV_PROTECTION (reduction > 5%)
        + "invalid\n"        // Error: Invalid format (catch ParseException)
        + " \n"              // Error: Empty input
        + "0\n"              // Exit attempt
        + "0\n";             // Final exit

    Scanner scanner = createScanner(simulatedInput);
    EditsApplier applier = new EditsApplier(new DummyTranslator());
    applier.applyEditsToYear(year2026, scanner);
  }

  /**
   * CANCELLATION TEST: Covers declining the deviation warning.
   * Goal: Coverage for the "else" branch in handleExtremeDeviationWarning.
   */
  @Test
  public void testDeviationCancellation() {
    String simulatedInput = 
        "1\n" + "1\n" + "env_protection_entry\n" 
        + "2000000\n"        // 100% deviation
        + "OXI\n"            // Decline -> "Change cancelled"
        + " \n"              // Empty selection (choice -1)
        + "0\n" + "0\n";

    Scanner scanner = createScanner(simulatedInput);
    EditsApplier applier = new EditsApplier(new DummyTranslator());
    applier.applyEditsToYear(year2025, scanner);
  }

  /**
   * Helper method to create a Scanner from a String.
   *
   * @param input The simulated input string.
   * @return A scanner object.
   */
  private Scanner createScanner(String input) {
    return new Scanner(new ByteArrayInputStream(
        input.getBytes(StandardCharsets.UTF_8)));
  }
}