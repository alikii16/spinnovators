package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import gr.det.spinnovators.service.EsgScoreCalculator;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link YearComparisonWebDisplay} class.
 *
 * <p>This test suite verifies the HTML generation for year-over-year budget comparisons.
 * It ensures 100% coverage by testing all logical branches including score
 * improvements, deteriorations, and neutral states.</p>
 *
 * <p>Key features tested:
 * <ul>
 * <li>Score difference analysis (Improvement/Deterioration/Neutral)</li>
 * <li>Visual formatting with Greek Locale (commas and signs)</li>
 * <li>Reflection-based mock injection</li>
 * <li>String truncation for UI consistency</li>
 * <li>Robust null and empty data handling</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 2.0
 */
public class YearComparisonWebDisplayTest {

  private EnvBudgetTranslator mockTranslator;
  private EsgScoreCalculator mockCalculator;
  private YearComparisonWebDisplay webDisplay;

  /**
   * Sets up the test environment.
   * Mocks the translator and uses Reflection to inject a mock calculator
   * into the private field of the YearComparisonWebDisplay instance.
   */
  @BeforeEach
  public void setUp() throws Exception {
    // 1. Mock the Translator
    mockTranslator = Mockito.mock(EnvBudgetTranslator.class);
    when(mockTranslator.translateCategory(anyString()))
        .thenAnswer(i -> "Μετάφραση_" + i.getArguments()[0]);

    // 2. Create the instance
    webDisplay = new YearComparisonWebDisplay(mockTranslator);

    // 3. Mock the internal Calculator
    mockCalculator = Mockito.mock(EsgScoreCalculator.class);

    // 4. Inject the mock calculator using Reflection
    Field field = YearComparisonWebDisplay.class.getDeclaredField("esgCalculator");
    field.setAccessible(true);
    field.set(webDisplay, mockCalculator);
  }

  // --- Helper Methods ---

  /**
   * Creates a mock EsgReport with configured scores.
   */
  private EsgReport createMockReport(double overall, double env, double soc, double gov) {
    EsgReport report = Mockito.mock(EsgReport.class);
    when(report.getOverallScore()).thenReturn(overall);
    when(report.getEnvironmentalScore()).thenReturn(env);
    when(report.getSocialScore()).thenReturn(soc);
    when(report.getGovernanceScore()).thenReturn(gov);
    return report;
  }

  /**
   * Creates a mock EnvYear structure with a single entry for basic testing.
   */
  private EnvYear createMockYear(String yearStr, String sectorName, double amount) {
    EnvEntry entry = new EnvEntry("entry1", amount);
    EnvUnit unit = new EnvUnit("unit1", List.of(entry));
    EnvSector sector = new EnvSector(sectorName, List.of(unit));
    return new EnvYear(yearStr, List.of(sector));
  }

  // --- Tests ---

  /**
   * Verifies the basic HTML structure and the presence of compared years.
   */
  @Test
  public void testGenerateComparisonContent_BasicStructure() {
    EnvYear year2024 = createMockYear("2024", "energy", 100.0);
    EnvYear year2025 = createMockYear("2025", "energy", 200.0);

    EsgReport simpleReport = createMockReport(50.0, 50.0, 50.0, 50.0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(simpleReport);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertNotNull(html);
    Assertions.assertTrue(html.contains("2024 vs 2025"));
    Assertions.assertTrue(html.contains("Μετάφραση_energy"));
  }

  /**
   * Verifies that long sector names are truncated to preserve UI alignment.
   */
  @Test
  public void testGenerateComparisonContent_LongSectorName() {
    EnvYear year2024 = createMockYear("2024", "long_sector", 100.0);
    EnvYear year2025 = createMockYear("2025", "long_sector", 100.0);

    String longName = "This is an extremely long sector name that definitely needs to be truncated";
    when(mockTranslator.translateCategory("long_sector")).thenReturn(longName);

    EsgReport dummy = createMockReport(50.0, 50.0, 50.0, 50.0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummy);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("..."));
    Assertions.assertTrue(html.contains(longName.substring(0, 42)));
  }

  /**
   * Tests division-by-zero protection and Greek formatting for zero change.
   */
  @Test
  public void testGenerateComparisonContent_ZeroValues() {
    EnvYear year2024 = createMockYear("2024", "new_sector", 0.0);
    EnvYear year2025 = createMockYear("2025", "new_sector", 0.0);

    EsgReport dummy = createMockReport(50.0, 50.0, 50.0, 50.0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummy);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    boolean containsZeroPercent = html.contains("+0,0%") || html.contains("+0.0%");
    Assertions.assertTrue(containsZeroPercent, "Should contain properly formatted 0% change");
  }

  /**
   * Verifies the UI indicators for a significant improvement in ESG performance.
   */
  @Test
  public void testGenerateComparisonContent_SignificantImprovement() {
    EnvYear year2024 = createMockYear("2024", "sector", 100.0);
    EnvYear year2025 = createMockYear("2025", "sector", 200.0);

    EsgReport reportLow = createMockReport(10.0, 10.0, 10.0, 10.0);
    EsgReport reportHigh = createMockReport(90.0, 90.0, 90.0, 90.0);

    when(mockCalculator.calculateReport(any(), anyDouble()))
        .thenReturn(reportLow)
        .thenReturn(reportHigh);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("ΒΕΛΤΙΩΣΗ"));
    Assertions.assertTrue(html.contains("Εξαιρετικά!"));
  }

  /**
   * Verifies the UI indicators for a small improvement in ESG performance.
   */
  @Test
  public void testGenerateComparisonContent_SmallImprovement() {
    EnvYear year2024 = createMockYear("2024", "sector", 100.0);
    EnvYear year2025 = createMockYear("2025", "sector", 101.0);

    EsgReport reportBase = createMockReport(50.0, 50.0, 50.0, 50.0);
    EsgReport reportBetter = createMockReport(51.0, 51.0, 51.0, 51.0);

    when(mockCalculator.calculateReport(any(), anyDouble()))
        .thenReturn(reportBase)
        .thenReturn(reportBetter);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("ΒΕΛΤΙΩΣΗ"));
    Assertions.assertTrue(html.contains("Καλή αλλαγή!"));
  }

  /**
   * Verifies the UI indicators for a significant deterioration in ESG performance.
   */
  @Test
  public void testGenerateComparisonContent_SignificantDeterioration() {
    EnvYear year2024 = createMockYear("2024", "sector", 200.0);
    EnvYear year2025 = createMockYear("2025", "sector", 100.0);

    EsgReport reportHigh = createMockReport(90.0, 90.0, 90.0, 90.0);
    EsgReport reportLow = createMockReport(10.0, 10.0, 10.0, 10.0);

    when(mockCalculator.calculateReport(any(), anyDouble()))
        .thenReturn(reportHigh)
        .thenReturn(reportLow);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("ΕΠΙΔΕΙΝΩΣΗ"));
    Assertions.assertTrue(html.contains("ΠΡΟΣΟΧΗ"));
  }

  /**
   * Verifies the UI indicators for a small deterioration in ESG performance.
   */
  @Test
  public void testGenerateComparisonContent_SmallDeterioration() {
    EnvYear year2024 = createMockYear("2024", "sector", 101.0);
    EnvYear year2025 = createMockYear("2025", "sector", 100.0);

    EsgReport reportBase = createMockReport(51.0, 51.0, 51.0, 51.0);
    EsgReport reportWorse = createMockReport(50.0, 50.0, 50.0, 50.0);

    when(mockCalculator.calculateReport(any(), anyDouble()))
        .thenReturn(reportBase)
        .thenReturn(reportWorse);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("ΕΠΙΔΕΙΝΩΣΗ"));
    Assertions.assertTrue(html.contains("μειώνει ελαφρώς"));
  }

  /**
   * Verifies the UI state when no changes occur between the compared years.
   */
  @Test
  public void testGenerateComparisonContent_NoChange() {
    EnvYear year2024 = createMockYear("2024", "sector", 500.0);
    EnvYear year2025 = createMockYear("2025", "sector", 500.0);

    EsgReport reportSame = createMockReport(50.0, 50.0, 50.0, 50.0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(reportSame);

    String html = webDisplay.generateComparisonContent(year2024, year2025, null);

    Assertions.assertTrue(html.contains("ΚΑΜΙΑ ΑΛΛΑΓΗ"));
  }

  /**
   * Verifies that the display can use an external total budgets map.
   */
  @Test
  public void testGenerateComparisonContent_WithTotalBudgetMapOverride() {
    EnvYear year2024 = createMockYear("2024", "sectorA", 100.0);
    EnvYear year2025 = createMockYear("2025", "sectorA", 120.0);

    Map<String, Double> totalBudgets = new HashMap<>();
    totalBudgets.put("2024", 5000.0);
    totalBudgets.put("2025", 6000.0);

    EsgReport dummyReport = createMockReport(50.0, 50.0, 50.0, 50.0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummyReport);

    String html = webDisplay.generateComparisonContent(year2024, year2025, totalBudgets);

    Assertions.assertNotNull(html);
  }

  /**
   * Tests robustness when encountering malformed data or null entries.
   */
  @Test
  public void testNullHandling_MalformedData() {
    EnvSector validSector = new EnvSector("valid", null);
    EnvUnit unitWithNullEntries = new EnvUnit("unitNullEntries", null);
    EnvSector sectorWithNullUnitsList = new EnvSector("sector2", List.of(unitWithNullEntries));

    List<EnvSector> sectors = Arrays.asList(null, validSector, sectorWithNullUnitsList);

    EnvYear messyYear = new EnvYear("2025", sectors);
    EnvYear emptyYear = new EnvYear("2024", Collections.emptyList());

    EsgReport dummy = createMockReport(0, 0, 0, 0);
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummy);

    String html = webDisplay.generateComparisonContent(emptyYear, messyYear, null);

    Assertions.assertNotNull(html);
  }
}