package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for the {@link YearToYearBudgetComparison} class.
 * Ensures correct formatting, calculation logic, and coverage of edge cases.
 */
public class YearToYearBudgetComparisonTest {

  private YearToYearBudgetComparison comparisonService;
  private EnvBudgetTranslator translator;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    // Capture console output in UTF-8 to handle Greek characters correctly
    System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8.name()));
    translator = new EnvBudgetTranslator();
  }

  /**
   * Tests standard flow with known totals.
   */
  @Test
  public void testCompareYears_StandardFlow() {
    Map<String, Double> totalBudgets = new HashMap<>();
    totalBudgets.put("2025", 1000.0);
    totalBudgets.put("2026", 2000.0);
    comparisonService = new YearToYearBudgetComparison(translator, totalBudgets);

    EnvYear year2025 = createMockYear("2025", "sector_a", 100.0);
    EnvYear year2026 = createMockYear("2026", "sector_a", 150.0);

    comparisonService.compareYears(year2025, year2026);

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    // Check header
    Assertions.assertTrue(output.contains("2025 vs 2026"));
    
    // Check difference formatting (Greek locale: comma decimal)
    // 150 - 100 = 50. Format: +50,00
    Assertions.assertTrue(output.contains("50,00"), "Output should contain '50,00'");
  }

  /**
   * CRITICAL TEST FOR 100% COVERAGE.
   * Covers:
   * 1. 'truncate' method (using a very long sector name).
   * 2. 'totalBase == null' fallback logic (using empty totals map).
   */
  @Test
  public void testCompareYears_LongNamesAndFallback() {
    // Empty map forces the class to calculate totals itself (covering the null checks)
    comparisonService = new YearToYearBudgetComparison(translator, new HashMap<>());

    // Create a sector with a very long name (> 45 chars) to trigger truncate
    String longName = "a_very_long_sector_name_that_exceeds_forty_five_characters_limit_test";
    EnvYear year2025 = createMockYear("2025", longName, 100.0);
    EnvYear year2026 = createMockYear("2026", longName, 120.0);

    comparisonService.compareYears(year2025, year2026);

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    
    // Verify truncation happened (should contain "..." )
    Assertions.assertTrue(output.contains("..."), "Should truncate long names");
    
    // Verify calculation worked despite missing totalBudgets map
    Assertions.assertTrue(output.contains("20,00"), "Should calculate diff correctly via fallback");
  }

  /**
   * Tests edge cases for percentage calculation (Division by zero).
   */
  @Test
  public void testCompareYears_ZeroLogic() {
    comparisonService = new YearToYearBudgetComparison(translator, new HashMap<>());

    // Case: 0 -> 100 (Infinite increase handled as 100%)
    EnvYear yearBase = createMockYear("2025", "sector_zero", 0.0);
    EnvYear yearCompare = createMockYear("2026", "sector_zero", 100.0);

    comparisonService.compareYears(yearBase, yearCompare);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    
    // Should handle 0 start value safely
    Assertions.assertTrue(output.contains("100,00"), "Should show increase safely");
  }

  // Helper to create mock data quickly
  private EnvYear createMockYear(String yearStr, String sectorKey, double amount) {
    List<EnvEntry> entries = new ArrayList<>();
    entries.add(new EnvEntry("entry_1", amount));
    List<EnvUnit> units = new ArrayList<>();
    units.add(new EnvUnit("unit_1", entries));
    List<EnvSector> sectors = new ArrayList<>();
    sectors.add(new EnvSector(sectorKey, units));
    return new EnvYear(yearStr, sectors);
  }
}
