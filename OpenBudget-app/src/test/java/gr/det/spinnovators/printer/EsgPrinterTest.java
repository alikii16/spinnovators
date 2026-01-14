package gr.det.spinnovators.printer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gr.det.spinnovators.envdatamodel.EsgReport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link EsgPrinter} class.
 *
 * <p>This test suite is designed to achieve 100% Branch Coverage by simulating
 * all possible score ranges and comparison scenarios, including:
 * <ul>
 * <li>Major/Minor improvements and deteriorations.</li>
 * <li>Low scores triggering specific suggestions.</li>
 * <li>High scores triggering praise.</li>
 * <li>Neutral budget presence/absence.</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EsgPrinterTest {

  private EsgPrinter printer;
  private ByteArrayOutputStream outputStream;

  /**
   * Sets up the test environment.
   * Redirects System.out to a stream with UTF-8 encoding to correctly 
   * capture and verify Greek characters in the console output.
   */
  @BeforeEach
  void setUp() throws Exception {
    printer = new EsgPrinter();
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8.name()));
  }

  /**
   * Helper method to create dummy ESG reports for testing.
   */
  private EsgReport createReport(double env, double soc, double gov, double neutral, double overall) {
    return new EsgReport(
        "2026",
        1_000_000,
        300_000, 300_000, 300_000, neutral,
        env, soc, gov, overall
    );
  }

  /**
   * Tests the report printing with low scores across all categories.
   * Verifies that all relevant improvement suggestions are triggered.
   */
  @Test
  void testPrintReport_LowScores() {
    // Env < 50, Soc < 20, Gov < 15, Overall < 60
    EsgReport report = createReport(40, 10, 10, 100_000, 30);

    printer.printReport(report);
    String output = outputStream.toString();

    assertTrue(output.contains("Αυξήστε τις δαπάνες για ΑΠΕ"));
    assertTrue(output.contains("αύξηση των κοινωνικών παροχών"));
    assertTrue(output.contains("Ενισχύστε τη διοικητική υποδομή"));
    assertTrue(output.contains("Neutral"));
  }

  /**
   * Tests the report printing with high scores.
   * Verifies that praise is given and suggestions are skipped.
   * Also checks branch coverage for zero Neutral budget.
   */
  @Test
  void testPrintReport_HighScores_NoNeutral() {
    EsgReport report = createReport(60, 30, 30, 0, 65);

    printer.printReport(report);
    String output = outputStream.toString();

    assertTrue(output.contains("Καλή δουλειά!"));
    assertFalse(output.contains("Neutral"), "Should not print Neutral line if amount is 0");
  }

  /**
   * Tests a comparison scenario with a major improvement in scores.
   */
  @Test
  void testComparison_MajorImprovement() {
    EsgReport before = createReport(50, 50, 50, 0, 50);
    EsgReport after = createReport(55, 55, 55, 0, 55);

    printer.printComparison(before, after);
    String output = outputStream.toString();

    assertTrue(output.contains("Εξαιρετικά! Η αλλαγή βελτιώνει σημαντικά"));
    assertTrue(output.contains("⬆️"));
  }

  /**
   * Tests a comparison scenario with a minor improvement in scores.
   */
  @Test
  void testComparison_MinorImprovement() {
    EsgReport before = createReport(50, 50, 50, 0, 50);
    EsgReport after = createReport(51, 51, 51, 0, 51);

    printer.printComparison(before, after);
    String output = outputStream.toString();

    assertTrue(output.contains("Καλή αλλαγή! Μικρή βελτίωση"));
  }

  /**
   * Tests a comparison scenario with a major deterioration in scores.
   */
  @Test
  void testComparison_MajorDeterioration() {
    EsgReport before = createReport(50, 50, 50, 0, 50);
    EsgReport after = createReport(45, 45, 45, 0, 45);

    printer.printComparison(before, after);
    String output = outputStream.toString();

    assertTrue(output.contains("ΠΡΟΣΟΧΗ: Η αλλαγή επιδεινώνει σημαντικά"));
    assertTrue(output.contains("⬇️"));
  }

  /**
   * Tests a comparison scenario with a minor deterioration in scores.
   */
  @Test
  void testComparison_MinorDeterioration() {
    EsgReport before = createReport(50, 50, 50, 0, 50);
    EsgReport after = createReport(49, 49, 49, 0, 49);

    printer.printComparison(before, after);
    String output = outputStream.toString();

    assertTrue(output.contains("Η αλλαγή μειώνει ελαφρώς"));
  }

  /**
   * Tests a comparison scenario where there is no change in scores.
   */
  @Test
  void testComparison_NoChange() {
    EsgReport before = createReport(50, 50, 50, 0, 50);
    EsgReport after = createReport(50, 50, 50, 0, 50);

    printer.printComparison(before, after);
    String output = outputStream.toString();

    assertTrue(output.contains("Η αλλαγή δεν επηρεάζει το ESG score"));
    assertTrue(output.contains("→"));
  }

  /**
   * Tests the printing of a compact summary of the ESG report.
   */
  @Test
  void testPrintCompactSummary() {
    EsgReport report = createReport(50, 25, 25, 0, 45);
    printer.printCompactSummary(report);
    String output = outputStream.toString();

    assertTrue(output.contains("[ESG]"));
    assertTrue(output.contains("E: 50,0%"));
  }
}