package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link EnvBudgetPrinter} class.
 *
 * <p>This test suite ensures 100% Branch Coverage by verifying the printing 
 * logic for yearly budget reports. It validates both the successful display 
 * of hierarchical data and the handling of missing data scenarios.</p>
 *
 * <p>Verification includes:
 * <ul>
 * <li>Correct translation of keys (Sectors, Units, Entries)</li>
 * <li>Proper formatting of monetary amounts</li>
 * <li>Accurate summation of nested budget levels</li>
 * <li>Graceful error messaging for non-existent years</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvBudgetPrinterTest {

  private ByteArrayOutputStream outContent;
  private EnvBudgetTranslator simpleTranslator;

  /**
   * Sets up the test environment by redirecting standard output to capture
   * console prints. A simple anonymous implementation of EnvBudgetTranslator
   * is used to focus the test on the printer's logic.
   */
  @BeforeEach
  public void setUp() throws Exception {
    // Capture System.out output in UTF-8 to handle Greek characters
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));

    // Simple Translator implementation for testing purposes
    simpleTranslator = new EnvBudgetTranslator() {
      @Override
      public String translateCategory(String key) {
        return "Translated_" + key;
      }
    };
  }

  /**
   * Test Case: Print Budget for an Existing Year.
   * Verifies that the hierarchical structure (Sectors -> Units -> Entries) 
   * and the mathematical totals are printed correctly in the console.
   */
  @Test
  public void testPrintYearlyBudget_Success() {
    // 1. Prepare Hierarchical Data
    EnvEntry entry1 = new EnvEntry("personnel", 1000.0);
    EnvEntry entry2 = new EnvEntry("equipment", 500.0);

    // Sum for this unit should be 1500.0
    EnvUnit unit = new EnvUnit("unit_secretariat", List.of(entry1, entry2));
    EnvSector sector = new EnvSector("sector_energy", List.of(unit));
    EnvYear year = new EnvYear("2025", List.of(sector));

    Map<String, EnvYear> dataMap = new HashMap<>();
    dataMap.put("2025", year);
    
    // Total budgets map can be empty for this test as we focus on analtyical data
    EnvBudgetData data = new EnvBudgetData(dataMap, new HashMap<>());

    // 2. Initialize Printer
    EnvBudgetPrinter printer = new EnvBudgetPrinter(data, simpleTranslator);

    // 3. Execute Printing
    printer.printYearlyBudget("2025");

    // 4. Verify Output Content
    String output = outContent.toString(StandardCharsets.UTF_8);

    assertTrue(output.contains("Translated_sector_energy"), 
        "The output should contain the translated sector name.");
    assertTrue(output.contains("Translated_unit_secretariat"), 
        "The output should contain the translated unit name.");
    assertTrue(output.contains("Translated_personnel"), 
        "The output should contain the translated entry name.");
    assertTrue(output.contains("1.500,00"), 
        "The unit total should be correctly formatted (1000 + 500 = 1500).");
  }

  /**
   * Test Case: Print Budget for Non-Existent Year.
   * Covers the branch where yearlyBudget is null, ensuring the user 
   * receives a clear notification instead of a crash.
   */
  @Test
  public void testPrintYearlyBudget_NoData() {
    // Initialize with empty data maps
    EnvBudgetData emptyData = new EnvBudgetData(new HashMap<>(), new HashMap<>());

    EnvBudgetPrinter printer = new EnvBudgetPrinter(emptyData, simpleTranslator);

    // Request a year that is definitely not in the map
    printer.printYearlyBudget("2099");

    String output = outContent.toString(StandardCharsets.UTF_8);

    // Verify error handling branch
    assertTrue(output.contains("Δεν βρέθηκαν αναλυτικά δεδομένα"), 
        "An error message should be displayed when data for the year is missing.");
    assertTrue(output.contains("2099"), 
        "The error message should explicitly mention the missing year.");
  }
}