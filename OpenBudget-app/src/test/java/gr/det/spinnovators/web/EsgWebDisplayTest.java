package gr.det.spinnovators.web;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.service.EsgScoreCalculator;
import java.lang.reflect.Field;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link EsgWebDisplay}.
 *
 * <p><b>FIXED STRATEGY:</b> Mocking with Reflection & Consistent Data.
 * We ensure that when the Overall Score changes, the Individual Category Scores (Env, Soc, Gov)
 * also change in the Mocks. This ensures that both the feedback message (based on Overall)
 * and the CSS classes (based on Categories) are generated correctly.</p>
 */
public class EsgWebDisplayTest {

  @Mock
  private EsgScoreCalculator mockCalculator;

  /**
   * Initializes Mockito mocks before each test.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Helper method to inject the mock calculator into the EsgWebDisplay instance.
   *
   * @param display The display instance.
   * @param calculator The mock calculator to inject.
   */
  private void injectMockCalculator(EsgWebDisplay display, EsgScoreCalculator calculator) {
    try {
      Field field = EsgWebDisplay.class.getDeclaredField("calculator");
      field.setAccessible(true);
      field.set(display, calculator);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject mock calculator", e);
    }
  }

  /**
   * Helper to create a mocked EsgReport with specified scores.
   *
   * @param overall Overall score value.
   * @param env Environmental score value.
   * @param soc Social score value.
   * @param gov Governance score value.
   * @return A mocked EsgReport.
   */
  private EsgReport createMockReport(double overall, double env, double soc, double gov) {
    EsgReport report = mock(EsgReport.class);
    when(report.getOverallScore()).thenReturn(overall);
    when(report.getEnvironmentalScore()).thenReturn(env);
    when(report.getSocialScore()).thenReturn(soc);
    when(report.getGovernanceScore()).thenReturn(gov);
    return report;
  }

  private EnvYear createDummyYear(String year) {
    return new EnvYear(year, Collections.emptyList());
  }

  /**
   * Test: Significant improvement detection (Overall 50 -> 60).
   */
  @Test
  public void testGenerateEsgComparisonContent_SignificantImprovement() {
    // Setup: Overall increases significantly (50 -> 60)
    // AND Env increases (10 -> 20) to trigger "positive" class
    EsgReport originalReport = createMockReport(50.0, 10.0, 10.0, 10.0);
    EsgReport modifiedReport = createMockReport(60.0, 20.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("Εξαιρετικά!"), "Should detect Significant Improvement");
  }

  /**
   * Test: Small improvement detection (Overall 50 -> 51).
   */
  @Test
  public void testGenerateEsgComparisonContent_SmallImprovement() {
    // Setup: Overall increases slightly (50 -> 51)
    // AND Env increases slightly (10 -> 11) to trigger "positive" class
    EsgReport originalReport = createMockReport(50.0, 10.0, 10.0, 10.0);
    EsgReport modifiedReport = createMockReport(51.0, 11.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("Καλή αλλαγή!"), "Should detect Small Improvement");
  }

  /**
   * Test: Significant deterioration detection (Overall 60 -> 50).
   */
  @Test
  public void testGenerateEsgComparisonContent_SignificantDeterioration() {
    // Setup: Overall drops significantly (60 -> 50)
    // AND Env drops (20 -> 10) to trigger "negative" class
    EsgReport originalReport = createMockReport(60.0, 20.0, 10.0, 10.0);
    EsgReport modifiedReport = createMockReport(50.0, 10.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("ΠΡΟΣΟΧΗ"), "Should detect Significant Deterioration");
    Assertions.assertTrue(html.contains("negative"), "Should have negative class");
  }

  /**
   * Test: Small deterioration detection (Overall 51 -> 50).
   */
  @Test
  public void testGenerateEsgComparisonContent_SmallDeterioration() {
    // Setup: Overall drops slightly (51 -> 50)
    // AND Env drops slightly (11 -> 10) to trigger "negative" class
    EsgReport originalReport = createMockReport(51.0, 11.0, 10.0, 10.0);
    EsgReport modifiedReport = createMockReport(50.0, 10.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("μειώνει ελαφρώς"), "Should detect Small Deterioration");
    Assertions.assertTrue(html.contains("negative"), "Should have negative class");
  }

  /**
   * Test: Scenario with no score changes.
   */
  @Test
  public void testGenerateEsgComparisonContent_NoChange() {
    // Setup: No changes
    EsgReport originalReport = createMockReport(50.0, 10.0, 10.0, 10.0);
    EsgReport modifiedReport = createMockReport(50.0, 10.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("ΚΑΜΙΑ ΑΛΛΑΓΗ"), "Should detect No Change");
    Assertions.assertTrue(html.contains("neutral"), "Should have neutral class");
  }

  /**
   * Test: Mixed changes across categories (Pos, Neg, Neutral).
   */
  @Test
  public void testGenerateEsgComparisonContent_MixedCategoryChanges() {
    // Env: 10 -> 20 (Increase -> positive)
    // Soc: 20 -> 10 (Decrease -> negative)
    // Gov: 10 -> 10 (Same -> neutral)
    EsgReport originalReport = createMockReport(50.0, 10.0, 20.0, 10.0);
    EsgReport modifiedReport = createMockReport(50.0, 20.0, 10.0, 10.0);

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalReport);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedReport);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html.contains("positive"), "Env should trigger positive class");
    Assertions.assertTrue(html.contains("negative"), "Soc should trigger negative class");
    Assertions.assertTrue(html.contains("neutral"), "Gov should trigger neutral class");
  }

  /**
   * Tests remaining branches for Social and Governance to reach 100% coverage.
   */
  @Test
  public void testGenerateEsgComparisonContent_RemainingBranches() {
    // --- CASE 1: Social & Governance INCREASE (Positive) ---
    EsgReport originalRep1 = createMockReport(50.0, 10.0, 10.0, 10.0);
    EsgReport modifiedRep1 = createMockReport(60.0, 10.0, 20.0, 20.0); 

    EnvYear y1 = createDummyYear("2025");
    EnvYear y2 = createDummyYear("2026");

    when(mockCalculator.calculateReport(eq(y1), anyDouble())).thenReturn(originalRep1);
    when(mockCalculator.calculateReport(eq(y2), anyDouble())).thenReturn(modifiedRep1);

    EsgWebDisplay webDisplay = new EsgWebDisplay();
    injectMockCalculator(webDisplay, mockCalculator);
    String html1 = webDisplay.generateEsgComparisonContent(y1, y2, 1000.0);

    Assertions.assertTrue(html1.contains("positive"), "Should hit positive branch for Soc/Gov");

    // --- CASE 2: Governance DECREASES (Negative) ---
    EsgReport originalRep2 = createMockReport(50.0, 10.0, 10.0, 20.0);
    EsgReport modifiedRep2 = createMockReport(40.0, 10.0, 10.0, 10.0); 

    EnvYear y3 = createDummyYear("2027");
    EnvYear y4 = createDummyYear("2028");

    when(mockCalculator.calculateReport(eq(y3), anyDouble())).thenReturn(originalRep2);
    when(mockCalculator.calculateReport(eq(y4), anyDouble())).thenReturn(modifiedRep2);

    String html2 = webDisplay.generateEsgComparisonContent(y3, y4, 1000.0);

    Assertions.assertTrue(html2.contains("negative"), "Should hit negative branch for Gov");
  }
}
