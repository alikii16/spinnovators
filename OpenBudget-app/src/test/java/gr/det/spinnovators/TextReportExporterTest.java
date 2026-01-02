package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import gr.det.spinnovators.export.TextReportExporter;
import gr.det.spinnovators.export.EditedBudgetExporter;

/**
 * Unit tests for TextReportExporter class.
 */
public class TextReportExporterTest {

    @Test
    public void testExportWithChangeLog() throws Exception {
        EditedBudgetExporter exporter = new TextReportExporter();

        List<String> changeLog = List.of(
            "2025;Energy;Unit1;Personnel Costs;1000;1200",
            "2025;Water;Unit2;Infrastructure;500;400"
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        exporter.export(changeLog, out);

        String output = out.toString(StandardCharsets.UTF_8);

        // Header checks
        assertTrue(output.contains("ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ"),
            "Output should contain the official header");
        assertTrue(output.contains("ΑΝΑΦΟΡΑ ΤΡΟΠΟΠΟΙΗΣΗΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ"),
            "Output should contain the report title");

        // Year from first entry
        assertTrue(output.contains("Οικονομικό Έτος:    2025"),
            "Output should display the correct budget year");

        // Check first change
        assertTrue(output.contains("Energy > Unit1"),
            "Output should contain first sector > unit line");
        assertTrue(output.contains("Personnel Costs"),
            "Output should contain first category name");
        assertTrue(output.contains("1,000.00 €"),
            "Output should contain old value of first change");
        assertTrue(output.contains("1,200.00 €"),
            "Output should contain new value of first change");

        // Check second change
        assertTrue(output.contains("Water > Unit2"),
            "Output should contain second sector > unit line");
        assertTrue(output.contains("Infrastructure"),
            "Output should contain second category name");
        assertTrue(output.contains("500.00 €"),
            "Output should contain old value of second change");
        assertTrue(output.contains("400.00 €"),
            "Output should contain new value of second change");
    }

    @Test
    public void testExportWithEmptyList() throws Exception {
        EditedBudgetExporter exporter = new TextReportExporter();

        List<String> changeLog = List.of();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        exporter.export(changeLog, out);

        String output = out.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("(Δεν πραγματοποιήθηκαν αλλαγές"),
            "Output should display fallback message for empty change log");
    }

    @Test
    public void testExportWithNullList() throws Exception {
        EditedBudgetExporter exporter = new TextReportExporter();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        exporter.export(null, out);

        String output = out.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("(Δεν πραγματοποιήθηκαν αλλαγές"),
            "Output should display fallback message for null change log");
    }
}
