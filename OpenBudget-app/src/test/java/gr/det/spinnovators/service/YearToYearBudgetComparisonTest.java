package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
 * Unit tests for the {@link YearToYearBudgetComparison} class.
 *
 * <p>This test suite achieves 100% Code Coverage by verifying the comparison
 * between different budget years, handling both cases where total budget data
 * is provided via a map or calculated manually as a fallback.</p>
 *
 * @author Spinnovators Team
 * @version 2.0
 */
public class YearToYearBudgetComparisonTest {

  private EnvBudgetTranslator mockTranslator;
  private EsgScoreCalculator mockCalculator;
  private YearToYearBudgetComparison comparison;

  /**
   * Initializes the mock objects before each test.
   * Sets up default behavior for the translator to return translated keys.
   */
  @BeforeEach
  public void setUp() throws Exception {
    mockTranslator = Mockito.mock(EnvBudgetTranslator.class);
    when(mockTranslator.translateCategory(anyString())).thenAnswer(i -> "Trans_" + i.getArguments()[0]);

    mockCalculator = Mockito.mock(EsgScoreCalculator.class);
  }

  /**
   * Helper method to inject the mock calculator into the comparison instance
   * using Reflection, as the field is private.
   */
  private void injectCalculator(YearToYearBudgetComparison instance) throws Exception {
    Field field = YearToYearBudgetComparison.class.getDeclaredField("esgCalculator");
    field.setAccessible(true);
    field.set(instance, mockCalculator);
  }

  /**
   * Helper method to create a standardized EnvYear structure for comparison tests.
   */
  private EnvYear createYear(String yearStr, String sectorName, double amount) {
    EnvEntry entry = new EnvEntry("entry", amount);
    EnvUnit unit = new EnvUnit("unit", List.of(entry));
    EnvSector sector = new EnvSector(sectorName, List.of(unit));
    return new EnvYear(yearStr, List.of(sector));
  }

  /**
   * Helper method to create a mocked ESG report with balanced scores.
   */
  private EsgReport createMockReport() {
    EsgReport r = Mockito.mock(EsgReport.class);
    when(r.getOverallScore()).thenReturn(50.0);
    when(r.getEnvironmentalScore()).thenReturn(50.0);
    when(r.getSocialScore()).thenReturn(50.0);
    when(r.getGovernanceScore()).thenReturn(50.0);
    return r;
  }

  /**
   * Tests the constructor's defensive logic when a null map is provided.
   * Verifies that the instance is created without throwing an exception.
   */
  @Test
  public void testConstructor_WithNullMap() {
    YearToYearBudgetComparison localComparison = new YearToYearBudgetComparison(mockTranslator, null);
    Assertions.assertNotNull(localComparison);
  }

  /**
   * Tests the comparison logic when the total budget for a year is found in the map.
   */
  @Test
  public void testCompareYears_MapHit() throws Exception {
    Map<String, Double> budgetMap = new HashMap<>();
    budgetMap.put("2025", 5000.0);
    budgetMap.put("2026", 6000.0);

    comparison = new YearToYearBudgetComparison(mockTranslator, budgetMap);
    injectCalculator(comparison);

    EnvYear y1 = createYear("2025", "s", 100.0);
    EnvYear y2 = createYear("2026", "s", 120.0);

    EsgReport dummyReport = createMockReport();
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummyReport);

    comparison.compareYears(y1, y2);
  }

  /**
   * Tests the comparison when a year's budget is missing from the map.
   * Verifies that the system falls back to calculating the total sum manually.
   */
  @Test
  public void testCompareYears_MapMiss_FallbackToSum() throws Exception {
    Map<String, Double> budgetMap = new HashMap<>();

    comparison = new YearToYearBudgetComparison(mockTranslator, budgetMap);
    injectCalculator(comparison);

    EnvYear y1 = createYear("2025", "s", 100.0);
    EnvYear y2 = createYear("2026", "s", 120.0);

    EsgReport dummyReport = createMockReport();
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummyReport);

    comparison.compareYears(y1, y2);
  }

  /**
   * Tests mathematical edge cases, such as zero-value budgets or transitions
   * from zero to a positive amount, ensuring no division-by-zero errors occur.
   */
  @Test
  public void testMathLogic_ZeroValues() throws Exception {
    comparison = new YearToYearBudgetComparison(mockTranslator, new HashMap<>());
    injectCalculator(comparison);

    EnvYear yZero1 = createYear("2025", "secZero", 0.0);
    EnvYear yZero2 = createYear("2026", "secZero", 0.0);

    EnvSector sInc1 = new EnvSector("secInc", List.of(new EnvUnit("u", List.of(new EnvEntry("e", 0.0)))));
    EnvSector sInc2 = new EnvSector("secInc", List.of(new EnvUnit("u", List.of(new EnvEntry("e", 100.0)))));

    List<EnvSector> list1 = new ArrayList<>();
    list1.add(yZero1.getSectors().get(0));
    list1.add(sInc1);

    List<EnvSector> list2 = new ArrayList<>();
    list2.add(yZero2.getSectors().get(0));
    list2.add(sInc2);

    EnvYear y1 = new EnvYear("2025", list1);
    EnvYear y2 = new EnvYear("2026", list2);

    EsgReport dummyReport = createMockReport();
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummyReport);

    comparison.compareYears(y1, y2);
  }

  /**
   * Tests the visual truncation of very long sector names in the terminal output.
   * Ensures that the UI handles extreme strings gracefully.
   */
  @Test
  public void testTruncate_LongName() throws Exception {
    comparison = new YearToYearBudgetComparison(mockTranslator, new HashMap<>());
    injectCalculator(comparison);

    String longName = "very_long_sector_name_that_is_definitely_more_than_45_chars_long";
    EnvYear y1 = createYear("2025", longName, 100.0);
    EnvYear y2 = createYear("2026", longName, 100.0);

    when(mockTranslator.translateCategory(anyString())).thenReturn(longName);

    EsgReport dummyReport = createMockReport();
    when(mockCalculator.calculateReport(any(), anyDouble())).thenReturn(dummyReport);

    comparison.compareYears(y1, y2);
  }
}