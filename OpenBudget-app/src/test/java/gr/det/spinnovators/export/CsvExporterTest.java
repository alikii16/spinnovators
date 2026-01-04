package gr.det.spinnovators.export;

import gr.det.spinnovators.export.CsvExporter;
import gr.det.spinnovators.export.EditedBudgetExporter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * Unit tests for CsvExporter class.
 */
public class CsvExporterTest {

  @Test
  public void testExportWritesBOMAndHeaderAndEntries() throws Exception {
    EditedBudgetExporter exporter = new CsvExporter();

    List<String> changeLog = List.of(
      "Increase budget;1000;1200",
      "Decrease budget;500;400"
    );

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    exporter.export(changeLog, out);

    String output = out.toString(StandardCharsets.UTF_8);

    // Check BOM
    assertTrue(output.startsWith("\ufeff"), "Output should start with BOM");

    // Check header
    assertTrue(output.contains("Περιγραφή Αλλαγής;Παλιό Ποσό;Νέο Ποσό"),
        "Output should contain the correct CSV header");

    // Check entries
    assertTrue(output.contains("Increase budget;1000;1200"),
        "Output should contain the first change log entry");
    assertTrue(output.contains("Decrease budget;500;400"),
        "Output should contain the second change log entry");

    // Optional: check number of lines
    String[] lines = output.toString().split("\\R");
    assertEquals(3, lines.length, "Output should have 3 lines: BOM+header+2 entries");
  }
}
