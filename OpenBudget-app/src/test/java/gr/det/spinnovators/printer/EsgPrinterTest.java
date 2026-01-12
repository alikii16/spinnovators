package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EsgReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EsgPrinter}.
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
 */
public class EsgPrinterTest {

    private EsgPrinter printer;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        printer = new EsgPrinter();
        outputStream = new ByteArrayOutputStream();
        // Capture output in UTF-8 to correctly handle Greek characters
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8.name()));
    }

    // --- HELPER METHOD TO CREATE DUMMY REPORTS ---
    private EsgReport createReport(double env, double soc, double gov, double neutral, double overall) {
        // Using the 10-arg constructor as seen in your codebase structure
        return new EsgReport(
            "2026",
            1_000_000,
            300_000, 300_000, 300_000, neutral, // Amounts
            env, soc, gov, overall // Scores
        );
    }

    /**
     * Test Case: Report with Low Scores across the board.
     * Triggers all "Improvement Suggestions".
     */
    @Test
    void testPrintReport_LowScores() {
        // Env < 50, Soc < 20, Gov < 15, Overall < 60
        EsgReport report = createReport(40, 10, 10, 100_000, 30);

        printer.printReport(report);
        String output = outputStream.toString();

        assertTrue(output.contains("Αυξήστε τις δαπάνες για ΑΠΕ")); // Env Suggestion
        assertTrue(output.contains("αύξηση των κοινωνικών παροχών")); // Soc Suggestion
        assertTrue(output.contains("Ενισχύστε τη διοικητική υποδομή")); // Gov Suggestion
        assertTrue(output.contains("Neutral")); // Neutral line exists
    }

    /**
     * Test Case: Report with High Scores.
     * Triggers the "Great Job" message and skips improvement suggestions.
     * Also tests case where Neutral budget is 0 (branch coverage).
     */
    @Test
    void testPrintReport_HighScores_NoNeutral() {
        // Env >= 50, Soc >= 20, Gov >= 15, Overall >= 60
        EsgReport report = createReport(60, 30, 30, 0, 65);

        printer.printReport(report);
        String output = outputStream.toString();

        assertTrue(output.contains("Καλή δουλειά!"));
        assertFalse(output.contains("Neutral"), "Should not print Neutral line if amount is 0");
    }

    /**
     * Test Case: Comparison - Major Improvement (> 2.0).
     */
    @Test
    void testComparison_MajorImprovement() {
        EsgReport before = createReport(50, 50, 50, 0, 50);
        EsgReport after = createReport(55, 55, 55, 0, 55); // +5.0

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("Εξαιρετικά! Η αλλαγή βελτιώνει σημαντικά"));
        assertTrue(output.contains("⬆️"));
    }

    /**
     * Test Case: Comparison - Minor Improvement (> 0 and <= 2.0).
     */
    @Test
    void testComparison_MinorImprovement() {
        EsgReport before = createReport(50, 50, 50, 0, 50);
        EsgReport after = createReport(51, 51, 51, 0, 51); // +1.0

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("Καλή αλλαγή! Μικρή βελτίωση"));
    }

    /**
     * Test Case: Comparison - Major Deterioration (< -2.0).
     */
    @Test
    void testComparison_MajorDeterioration() {
        EsgReport before = createReport(50, 50, 50, 0, 50);
        EsgReport after = createReport(45, 45, 45, 0, 45); // -5.0

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("ΠΡΟΣΟΧΗ: Η αλλαγή επιδεινώνει σημαντικά"));
        assertTrue(output.contains("⬇️"));
    }

    /**
     * Test Case: Comparison - Minor Deterioration (< 0 and >= -2.0).
     */
    @Test
    void testComparison_MinorDeterioration() {
        EsgReport before = createReport(50, 50, 50, 0, 50);
        EsgReport after = createReport(49, 49, 49, 0, 49); // -1.0

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("Η αλλαγή μειώνει ελαφρώς"));
    }

    /**
     * Test Case: Comparison - No Change (0.0).
     */
    @Test
    void testComparison_NoChange() {
        EsgReport before = createReport(50, 50, 50, 0, 50);
        EsgReport after = createReport(50, 50, 50, 0, 50); // 0.0

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("Η αλλαγή δεν επηρεάζει το ESG score"));
        assertTrue(output.contains("→")); // Neutral arrow
    }

    @Test
    void testPrintCompactSummary() {
        EsgReport report = createReport(50, 25, 25, 0, 45);
        printer.printCompactSummary(report);
        String output = outputStream.toString();

        assertTrue(output.contains("[ESG]"));
        assertTrue(output.contains("E: 50,0%"));
    }
}