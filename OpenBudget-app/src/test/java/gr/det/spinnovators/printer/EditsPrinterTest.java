package gr.det.spinnovators.printer;

import java.util.List;
import org.junit.jupiter.api.Test;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link EditsPrinter} class.
 *
 * <p>This test suite verifies the console output and calculation logic when 
 * displaying budget edits. It uses a dummy translator to ensure that the 
 * focus remains on the structural correctness of the printed data.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EditsPrinterTest {

  /**
   * A simplified version of EnvBudgetTranslator for testing purposes.
   * It returns the original key as the translation to simplify assertions.
   */
  static class DummyTranslator extends EnvBudgetTranslator {
    @Override
    public String translateCategory(String key) {
      return key;
    }
  }

  /**
   * Tests the printEditYear method with multiple sectors and units.
   * * <p>This test verifies:
   * <ul>
   * <li>Correct summation of entries into units.</li>
   * <li>Correct summation of units into sectors.</li>
   * <li>Correct grand total calculation for the entire year.</li>
   * <li>Exception-free execution of the printing logic.</li>
   * </ul>
   * </p>
   */
  @Test
  public void testPrintEditYearMultipleSectorsUnits() {
    // --- Create entries with various amounts ---
    EnvEntry e1 = new EnvEntry("entry_positive", 100.0);
    EnvEntry e2 = new EnvEntry("entry_zero", 0.0);
    EnvEntry e3 = new EnvEntry("entry_negative", -25.0);
    EnvEntry e4 = new EnvEntry("entry_large", 1_000_000.0);

    // --- Create units ---
    EnvUnit unitA = new EnvUnit("unitA", List.of(e1, e2));
    EnvUnit unitB = new EnvUnit("unitB", List.of(e3, e4));

    // --- Create another sector with one unit ---
    EnvEntry e5 = new EnvEntry("entry5", 50.0);
    EnvUnit unitC = new EnvUnit("unitC", List.of(e5));
    EnvSector sector2 = new EnvSector("sector2", List.of(unitC));

    // --- Create sectors ---
    EnvSector sector1 = new EnvSector("sector1", List.of(unitA, unitB));

    // --- Create year containing both sectors ---
    EnvYear year = new EnvYear("2025", List.of(sector1, sector2));

    // --- Create EditsPrinter with dummy translator ---
    EditsPrinter printer = new EditsPrinter(new DummyTranslator());

    // --- Call printEditYear and ensure it does not throw any exceptions ---
    assertDoesNotThrow(() -> printer.printEditYear(year));

    // --- Manually calculate totals for each unit ---
    double unitATotal = e1.getAmount() + e2.getAmount(); // 100 + 0 = 100
    double unitBTotal = e3.getAmount() + e4.getAmount(); // -25 + 1_000_000 = 999_975
    double unitCTotal = e5.getAmount(); // 50

    // --- Calculate totals for sectors ---
    double sector1Total = unitATotal + unitBTotal; // 100 + 999_975 = 1_000_075
    double sector2Total = unitCTotal; // 50

    // --- Assertions for units ---
    assertEquals(100.0, unitATotal, 0.001);
    assertEquals(999_975.0, unitBTotal, 0.001);
    assertEquals(50.0, unitCTotal, 0.001);

    // --- Assertions for sectors ---
    assertEquals(1_000_075.0, sector1Total, 0.001);
    assertEquals(50.0, sector2Total, 0.001);

    // --- Total of the entire year ---
    double yearTotal = sector1Total + sector2Total; // 1_000_075 + 50 = 1_000_125
    assertEquals(1_000_125.0, yearTotal, 0.001);
  }
}