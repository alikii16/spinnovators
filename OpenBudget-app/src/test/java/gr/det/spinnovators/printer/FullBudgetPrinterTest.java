package gr.det.spinnovators.printer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gr.det.spinnovators.data.MinistryDataInput;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link FullBudgetPrinter} class.
 *
 * <p>This test suite verifies the console output for the general state budgets
 * across different years. It captures System.out to ensure that headings,
 * ministry names, and total amounts are correctly formatted and displayed.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class FullBudgetPrinterTest {

  private PrintStream originalOut;
  private ByteArrayOutputStream outputStream;
  private Locale originalLocale;

  /**
   * Sets up the environment before each test.
   * Redirects System.out to a ByteArrayOutputStream and stores the default Locale.
   */
  @BeforeEach
  void setUp() {
    originalOut = System.out;
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    originalLocale = Locale.getDefault();
  }

  /**
   * Restores the original System.out and Locale after each test.
   */
  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    Locale.setDefault(originalLocale);
  }

  /**
   * Tests the budget display for the year 2026.
   * Verifies the presence of the 2026 header and ministry data.
   */
  @Test
  public void testShowBudget2026() {
    MinistryDataInput data = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(data);

    printer.showBudget("2026");
    String output = outputStream.toString();

    assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2026"),
        "There has to be a header for year 2026");
    assertTrue(output.contains(data.getNames26()[0]),
        "At least the first ministry should be presented");
    assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"),
        "The total budget line should be present");
  }

  /**
   * Tests the budget display for the year 2025.
   * Verifies that the printer correctly handles data for 2025.
   */
  @Test
  public void testShowBudget2025() {
    MinistryDataInput data = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(data);

    printer.showBudget("2025");
    String output = outputStream.toString();

    assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2025"),
        "There has to be a header for year 2025");
    assertTrue(output.contains(data.getNames25()[0]),
        "At least the first ministry should be presented");
    assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"),
        "The total budget line should be present");
  }

  /**
   * Tests the budget display for the year 2024.
   * Verifies the output structure and data accuracy for 2024.
   */
  @Test
  public void testShowBudget2024() {
    MinistryDataInput data = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(data);

    printer.showBudget("2024");
    String output = outputStream.toString();

    assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2024"),
        "There has to be a header for the year 2024");
    assertTrue(output.contains(data.getNames24()[0]),
        "At least the first ministry should be presented");
    assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"),
        "The total budget line should be present");
  }

  /**
   * Tests the budget display for the year 2023.
   * Also ensures that no 'null' strings appear in the generated output.
   */
  @Test
  public void testShowBudget2023() {
    MinistryDataInput data = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(data);

    printer.showBudget("2023");
    String output = outputStream.toString();

    assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2023"),
        "There has to be a header for year 2023");
    assertTrue(output.contains(data.getNames23()[0]),
        "At least the first ministry should be presented");
    assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"),
        "The total budget line should be present");
    assertFalse(output.contains("null"),
        "The output should not contain 'null' values");
  }

  /**
   * Tests the behavior when an invalid or non-existent year is requested.
   * Verifies that the application displays a descriptive error message.
   */
  @Test
  public void testShowBudgetInvalidYear() {
    MinistryDataInput data = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(data);

    printer.showBudget("2022");
    String output = outputStream.toString();

    assertTrue(output.contains("Δεν υπάρχουν δεδομένα για το έτος 2022"),
        "An appropriate message should be shown for invalid year");
  }
}