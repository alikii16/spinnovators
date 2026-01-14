package gr.det.spinnovators.export;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link TextReportExporter} class.
 *
 * <p>This test suite ensures the correct generation of text reports under
 * various conditions, including standard operation, partial data handling,
 * and robust error recovery during parsing.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>Standard full data exports (6-field entries)</li>
 * <li>Partial data handling (3-5 fields) for backward compatibility</li>
 * <li>Exception handling for malformed numeric values</li>
 * <li>Empty or null log lists</li>
 * <li>Automatic truncation of long category names for layout consistency</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
class TextReportExporterTest {

  /**
   * Tests exporting a report with a standard, valid change log.
   * Verifies that the title, year, categories, and calculated differences 
   * are correctly present in the output.
   */
  @Test
  void testExportWithValidData() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    List<String> changeLog = new ArrayList<>();
    changeLog.add("2025;Clean Energy;Renewables;Wind Turbines;1000.00;1500.50");
    changeLog.add("2025;Water;Rivers;Cleaning;500.00;400.00");

    exporter.export(changeLog, outputStream);

    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ"), "Missing Title");
    assertTrue(result.contains("2025"), "Missing Year");
    assertTrue(result.contains("Wind Turbines"), "Missing Category");
    assertTrue(result.contains("+500.50 €"), "Missing Diff");
  }

  /**
   * Tests the exporter behavior when the change log is empty.
   * Verifies that an appropriate placeholder message is displayed.
   */
  @Test
  void testExportEmptyLog() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    exporter.export(new ArrayList<>(), outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("Δεν πραγματοποιήθηκαν αλλαγές"),
        "Should show empty message");
  }

  /**
   * Tests the exporter behavior when a null log is passed.
   * Verifies that the system handles the null pointer safely.
   */
  @Test
  void testExportNullLog() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    exporter.export(null, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("Δεν πραγματοποιήθηκαν αλλαγές"),
        "Should handle null log safely");
    assertTrue(result.contains("----"), "Year should be placeholder");
  }

  /**
   * Tests fallback logic for entries with partial data (3 fields instead of 6).
   * Verifies that the exporter still prints the available information.
   */
  @Test
  void testExportPartialDataFallback() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    List<String> changeLog = new ArrayList<>();
    changeLog.add("2025;PartialSector;PartialUnit");

    exporter.export(changeLog, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("PartialSector"), "Should print raw entry for partial data");
  }

  /**
   * Tests the handling of non-numeric strings in numeric fields.
   * Verifies that the NumberFormatException is caught and reported in the text.
   */
  @Test
  void testExportWithMalformedNumbers() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    List<String> changeLog = new ArrayList<>();
    changeLog.add("2025;Sector;Unit;Cat;NotANumber;AlsoNot");

    exporter.export(changeLog, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("Error parsing values"),
        "Should catch exception and print error tag");
  }

  /**
   * Tests that entries with too few fields (less than 3) are skipped.
   */
  @Test
  void testExportInvalidDataSkipped() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    List<String> changeLog = new ArrayList<>();
    changeLog.add("2025;BrokenData");

    exporter.export(changeLog, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertFalse(result.contains("BrokenData"), "Should skip invalid data (< 3 parts)");
  }

  /**
   * Tests the year extraction when an entry contains only delimiters.
   */
  @Test
  void testYearExtractionEdgeCase() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    List<String> changeLog = new ArrayList<>();
    changeLog.add(";;;;;");

    exporter.export(changeLog, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("----"), "Should fall back to placeholder year");
  }

  /**
   * Verifies that extremely long category names are truncated with ellipses.
   */
  @Test
  void testExportLongCategoryTruncation() throws UnsupportedEncodingException {
    TextReportExporter exporter = new TextReportExporter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    String longCategory = "ThisIsAVeryLongCategoryNameThatNeedsTruncation";
    List<String> changeLog = new ArrayList<>();
    changeLog.add("2025;Sec;Unit;" + longCategory + ";100;200");

    exporter.export(changeLog, outputStream);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("..."), "Should add ellipsis");
    assertFalse(result.contains(longCategory), "Should truncate long text");
  }
}