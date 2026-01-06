package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * Unit tests for EsgWebDisplay.
 *
 * <p>Tests the HTML generation of ESG comparison content.
 * Covers all logical branches for score differences:
 * significant improvement, small improvement, significant deterioration,
 * small deterioration, and no change.
 */
public class EsgWebDisplayTest {

  /**
   * Helper method to create a mock EnvYear with a specific budget amount.
   *
   * @param year The year string (e.g., "2025").
   * @param amount The budget amount for the entry.
   * @return A constructed EnvYear object.
   */
  private EnvYear createMockYear(String year, double amount) {
    EnvEntry entry = new EnvEntry("entry1", amount);
    EnvUnit unit = new EnvUnit("unit1", List.of(entry));
    EnvSector sector = new EnvSector("executive_coordination_and_investments", List.of(unit));
    return new EnvYear(year, List.of(sector));
  }

  /**
   * Tests the scenario where the ESG score improves significantly (> 2.0).
   * Covers the "Excellent" feedback message branch.
   */
  @Test
  public void testGenerateEsgComparisonContent_SignificantImprovement() {
    EnvYear originalYear = createMockYear("2025", 50.0);
    EnvYear modifiedYear = createMockYear("2026", 90.0); // Large increase
    double totalBudget = 200.0;

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

    Assertions.assertTrue(htmlContent.contains("Εξαιρετικά!"), "Should contain excellent feedback");
    Assertions.assertTrue(htmlContent.contains("positive"), "Should contain positive class");
  }

  /**
   * Tests the scenario where the ESG score improves slightly (0 < diff <= 2.0).
   * Covers the "Good change" feedback message branch (fixes missed branch in line 59).
   */
  @Test
  public void testGenerateEsgComparisonContent_SmallImprovement() {
    EnvYear originalYear = createMockYear("2025", 50.0);
    EnvYear modifiedYear = createMockYear("2026", 52.0); // Small increase
    double totalBudget = 200.0;

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

    Assertions.assertTrue(htmlContent.contains("Καλή αλλαγή!"), "Should contain small improvement feedback");
    Assertions.assertTrue(htmlContent.contains("positive"), "Should contain positive class");
  }

  /**
   * Tests the scenario where the ESG score deteriorates significantly (< -2.0).
   * Covers the "Warning" feedback message branch.
   */
  @Test
  public void testGenerateEsgComparisonContent_SignificantDeterioration() {
    EnvYear originalYear = createMockYear("2025", 90.0);
    EnvYear modifiedYear = createMockYear("2026", 50.0); // Large decrease
    double totalBudget = 200.0;

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

    Assertions.assertTrue(htmlContent.contains("ΠΡΟΣΟΧΗ"), "Should contain warning feedback");
    Assertions.assertTrue(htmlContent.contains("negative"), "Should contain negative class");
  }

  /**
   * Tests the scenario where the ESG score deteriorates slightly (-2.0 <= diff < 0).
   * Covers the "Slight reduction" feedback message branch (fixes missed branch in line 63).
   */
  @Test
  public void testGenerateEsgComparisonContent_SmallDeterioration() {
    EnvYear originalYear = createMockYear("2025", 52.0);
    EnvYear modifiedYear = createMockYear("2026", 50.0); // Small decrease
    double totalBudget = 200.0;

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

    Assertions.assertTrue(htmlContent.contains("μειώνει ελαφρώς"), "Should contain slight reduction feedback");
    Assertions.assertTrue(htmlContent.contains("negative"), "Should contain negative class");
  }

  /**
   * Tests the scenario where the ESG score remains unchanged.
   * Covers the "No change" feedback message branch and neutral classes.
   */
  @Test
  public void testGenerateEsgComparisonContent_NoChange() {
    EnvYear originalYear = createMockYear("2025", 50.0);
    EnvYear modifiedYear = createMockYear("2026", 50.0); // Exact same inputs
    double totalBudget = 200.0;

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

    Assertions.assertTrue(htmlContent.contains("ΚΑΜΙΑ ΑΛΛΑΓΗ"), "Should indicate no change");
    Assertions.assertTrue(htmlContent.contains("neutral"), "Should contain neutral class");
    Assertions.assertTrue(htmlContent.contains("δεν επηρεάζει"), "Should contain neutral feedback message");
  }
}
