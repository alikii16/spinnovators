package gr.det.spinnovators;

import gr.det.spinnovators.printer.EsgPrinter;
import gr.det.spinnovators.envdatamodel.EsgReport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EsgPrinter.
 */
public class EsgPrinterTest {

    private final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
    private EsgPrinter printer;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        printer = new EsgPrinter();
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testPrintReportBasic() {
        // Create a simple ESG report
        EsgReport report = new EsgReport(
                "2026",
                1_000_000,
                400_000, 200_000, 300_000, 100_000,
                40, 20, 30, 30
        );

        printer.printReport(report);
        String output = outputStream.toString();

        assertTrue(output.contains("2026"), "Output should contain the year");
        assertTrue(output.contains("Συνολικός Προϋπολογισμός"), "Output should contain total budget line");
        assertTrue(output.contains("Environmental"), "Output should contain Environmental category");
        assertTrue(output.contains("Social"), "Output should contain Social category");
        assertTrue(output.contains("Governance"), "Output should contain Governance category");
        assertTrue(output.contains("Neutral"), "Output should contain Neutral category");
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟ ESG SCORE"), "Output should contain overall ESG score");
    }

    @Test
    void testPrintComparisonPositiveChange() {
        EsgReport before = new EsgReport(
                "2026",
                1_000_000,
                400_000, 200_000, 300_000, 100_000,
                40, 20, 30, 30
        );

        EsgReport after = new EsgReport(
                "2026",
                1_000_000,
                500_000, 250_000, 350_000, 100_000,
                50, 25, 35, 40
        );

        printer.printComparison(before, after);
        String output = outputStream.toString();

        assertTrue(output.contains("ESG Score Πριν"), "Output should contain 'before' score");
        assertTrue(output.contains("ESG Score Μετά"), "Output should contain 'after' score");
        assertTrue(output.contains("ΒΕΛΤΙΩΣΗ"), "Output should indicate improvement");
    }

    @Test
    void testPrintCompactSummary() {
        EsgReport report = new EsgReport(
                "2026",
                1_000_000,
                400_000, 200_000, 300_000, 100_000,
                40, 20, 30, 30
        );

        printer.printCompactSummary(report);
        String output = outputStream.toString();

        assertTrue(output.contains("ESG"), "Output should contain ESG keyword");
        assertTrue(output.contains("Score"), "Output should contain Score");
        assertTrue(output.contains("E:"), "Output should contain Environmental percentage");
        assertTrue(output.contains("S:"), "Output should contain Social percentage");
        assertTrue(output.contains("G:"), "Output should contain Governance percentage");
    }
}
