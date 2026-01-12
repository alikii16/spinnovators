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

  @Test
  public void testCompareYears_StandardFlow() {
    Map<String, Double> totalBudgets = new HashMap<>();
    totalBudgets.put("2025", 1000.0);
    totalBudgets.put("2026", 2000.0);
    comparisonService = new YearToYearBudgetComparison(translator, totalBudgets);

    // Short name sector -> Truncate method will return it as is (Coverage for "if <= maxLength")
    EnvYear year2025 = createMockYear("2025", "sector_a", 100.0);
    EnvYear year2026 = createMockYear("2026", "sector_a", 150.0);

    comparisonService.compareYears(year2025, year2026);

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    
    // Checks
    Assertions.assertTrue(output.contains("2025 vs 2026"));
    Assertions.assertTrue(output.contains("50,00"));
    Assertions.assertTrue(output.contains("sector a"), "Short name should appear full");
  }

  @Test
  public void testCompareYears_LongNamesAndFallback() {
    comparisonService = new YearToYearBudgetComparison(translator, new HashMap<>());

    // Long name sector -> Truncate method will cut it (Coverage for "return substring...")
    String longName = "a_very_long_sector_name_that_exceeds_forty_five_characters_limit_test";
    EnvYear year2025 = createMockYear("2025", longName, 100.0);
    EnvYear year2026 = createMockYear("2026", longName, 120.0);

    comparisonService.compareYears(year2025, year2026);

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    
    // Verify truncation happened (should contain "..." )
    Assertions.assertTrue(output.contains("..."), "Should truncate long names");
    Assertions.assertTrue(output.contains("20,00"), "Should calculate diff via fallback");
  }

  @Test
  public void testCompareYears_ZeroLogic() {
    comparisonService = new YearToYearBudgetComparison(translator, new HashMap<>());

    // Δημιουργούμε λίστες Sector ΧΩΡΙΣΤΑ για να αποφύγουμε το UnsupportedOperationException
    List<EnvSector> sectorsBase = new ArrayList<>();
    List<EnvSector> sectorsCompare = new ArrayList<>();

    // 1. Sector: 0 -> 100
    sectorsBase.add(new EnvSector("sector_zero_inc", createUnits(0.0)));
    sectorsCompare.add(new EnvSector("sector_zero_inc", createUnits(100.0)));

    // 2. Sector: 0 -> 0 (Αυτό έλειπε)
    sectorsBase.add(new EnvSector("sector_double_zero", createUnits(0.0)));
    sectorsCompare.add(new EnvSector("sector_double_zero", createUnits(0.0)));

    // Φτιάχνουμε τα EnvYear με τις έτοιμες λίστες
    EnvYear yearBase = new EnvYear("2025", sectorsBase);
    EnvYear yearCompare = new EnvYear("2026", sectorsCompare);

    comparisonService.compareYears(yearBase, yearCompare);
    
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    
    // Assertions
    Assertions.assertTrue(output.contains("100,0"), "0 to 100 should be 100%");
    Assertions.assertTrue(output.contains("0,0"), "0 to 0 should be 0%");
  }

  // Helper methods
  private EnvYear createMockYear(String yearStr, String sectorKey, double amount) {
    List<EnvSector> sectors = new ArrayList<>();
    sectors.add(new EnvSector(sectorKey, createUnits(amount)));
    return new EnvYear(yearStr, sectors);
  }

  private List<EnvUnit> createUnits(double amount) {
      List<EnvEntry> entries = new ArrayList<>();
      entries.add(new EnvEntry("entry_1", amount));
      List<EnvUnit> units = new ArrayList<>();
      units.add(new EnvUnit("unit_1", entries));
      return units;
  }
}