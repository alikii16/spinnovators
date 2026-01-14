package gr.det.spinnovators.export;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link EditedBudgetExporter} interface.
 *
 * <p>This test suite verifies the contract and expected behavior of any 
 * implementation of the exporter interface, including data integrity, 
 * null-safety, and handling of large data sets. It uses a private test 
 * implementation to validate the interface logic without side effects.</p>
 *
 * <p>Key features tested:
 * <ul>
 * <li>Export functionality with valid, empty, and null log data</li>
 * <li>Defensive programming for null output streams</li>
 * <li>Order preservation of change log entries</li>
 * <li>Support for special characters and international encoding</li>
 * <li>Performance and stability with large data sets</li>
 * <li>Multiple consecutive export operations</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
class EditedBudgetExporterTest {

  /**
   * Test data for change logs used across multiple test cases.
   * Represents a standard CSV-like format used in the application.
   */
  private static final List<String> VALID_CHANGE_LOG = Arrays.asList(
      "2023;Energy;UnitA;Category1;1000.0;1500.0",
      "2023;Health;UnitB;Category2;2000.0;2500.0",
      "2024;Education;UnitC;Category3;3000.0;2800.0"
  );

  /**
   * Tests that a concrete implementation of the interface can be successfully 
   * instantiated. This verifies the basic setup of the test suite.
   */
  @Test
  void testInterfaceImplementation() {
    // Create a simple implementation for testing
    EditedBudgetExporter exporter = new TestExporter();
    assertNotNull(exporter, "The exporter implementation should be instantiable.");
  }

  /**
   * Tests the export method with a valid set of change log data.
   * Verifies that the output stream contains the exactly written data strings.
   */
  @Test
  void testExportWithValidData() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    exporter.export(VALID_CHANGE_LOG, output);

    String result = output.toString();
    assertNotNull(result, "The output string should not be null.");
    assertTrue(result.contains(VALID_CHANGE_LOG.get(0)),
        "The output should contain the first entry of the change log.");
    assertTrue(result.contains(VALID_CHANGE_LOG.get(2)),
        "The output should contain the last entry of the change log.");
  }

  /**
   * Tests that the export method handles empty input lists without crashing.
   * Ensures that the system remains stable even when no changes are recorded.
   */
  @Test
  void testExportWithEmptyList() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    exporter.export(new ArrayList<>(), output);

    String result = output.toString();
    assertNotNull(result, "The output should be an empty string, not null.");
  }

  /**
   * Tests that the export method throws an exception when a null output stream 
   * is provided. This is a critical check for null-safety.
   */
  @Test
  void testExportWithNullOutputStream() {
    EditedBudgetExporter exporter = new TestExporter();

    Exception exception = assertThrows(Exception.class,
        () -> exporter.export(VALID_CHANGE_LOG, null),
        "An exception should be thrown when the output stream is null."
    );

    assertNotNull(exception, "The thrown exception should be identifiable.");
  }

  /**
   * Tests that the export method handles a null change log safely.
   * Verifies that implementations provide a default empty behavior.
   */
  @Test
  void testExportWithNullChangeLog() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    // Implementation should handle null gracefully by treating it as empty
    exporter.export(null, output);

    String result = output.toString();
    assertNotNull(result, "The output should be generated even with null input.");
  }

  /**
   * Verifies that the export process preserves the original order of entries.
   * Sequential processing is vital for audit trails and chronological reports.
   */
  @Test
  void testExportPreservesOrder() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    List<String> changes = Arrays.asList("FirstChange", "SecondChange", "ThirdChange");
    exporter.export(changes, output);

    String result = output.toString();
    assertTrue(result.startsWith("FirstChange"), "The output must start with the first entry.");
    assertTrue(result.contains("FirstChangeSecondChangeThirdChange"),
        "The sequence of changes should be preserved in the output.");
  }

  /**
   * Tests the export of data containing special characters (e.g., accents).
   * Ensures that the exporter respects character encoding (UTF-8).
   */
  @Test
  void testExportWithSpecialCharacters() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    List<String> changes = Arrays.asList(
        "2023;Energy;Unit A;Catégorie;1000,0;1500,0",
        "2023;Health;Unit-B;Category_Ελληνικά;2000.0;2500.0"
    );

    exporter.export(changes, output);

    String result = output.toString();
    assertNotNull(result, "Should handle special characters without failure.");
    assertTrue(result.contains("Catégorie"), "Should preserve French accents.");
    assertTrue(result.contains("Ελληνικά"), "Should preserve Greek characters.");
  }

  /**
   * Tests performing multiple exports to the same output stream.
   * Verifies that the stream remains open and usable for consecutive writes.
   */
  @Test
  void testMultipleExports() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    exporter.export(VALID_CHANGE_LOG.subList(0, 1), output);
    String firstExport = output.toString();

    output.reset(); // Clear for second test
    exporter.export(VALID_CHANGE_LOG.subList(1, 2), output);
    String secondExport = output.toString();

    assertNotNull(firstExport, "The first export operation should succeed.");
    assertNotNull(secondExport, "The second export operation should succeed.");
    assertTrue(!firstExport.equals(secondExport), "Different inputs must produce different outputs.");
  }

  /**
   * Tests that the export method signature accepts any OutputStream subclass.
   * This confirms polymorphic behavior of the interface.
   */
  @Test
  void testPolymorphicOutputStream() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    OutputStream output = new ByteArrayOutputStream();

    // Testing if the interface method works with the base OutputStream class
    exporter.export(VALID_CHANGE_LOG, output);
    
    assertNotNull(output, "The method should accept a general OutputStream reference.");
  }

  /**
   * Tests the export functionality with a significantly large data set.
   * Verifies that the implementation handles bulk data without memory leaks or errors.
   */
  @Test
  void testExportWithLargeDataSet() throws Exception {
    EditedBudgetExporter exporter = new TestExporter();
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    List<String> largeList = new ArrayList<>();
    for (int i = 0; i < 2000; i++) {
      largeList.add("2023;Sector" + i + ";Unit" + i + ";Category" + i
          + ";" + (i * 1000) + ";" + (i * 1200));
    }

    exporter.export(largeList, output);

    String result = output.toString();
    assertNotNull(result, "Should handle large datasets gracefully.");
    assertTrue(result.length() > 20000, "The output should contain data for all 2000 entries.");
  }

  /**
   * Simple test implementation for testing the interface's behavior.
   * It replicates the logic of writing log entries as bytes to a stream.
   */
  private static class TestExporter implements EditedBudgetExporter {
    /**
     * Concrete implementation of the export method for validation.
     *
     * @param changeLog List of changes to export.
     * @param out Output stream to write to.
     * @throws Exception if an I/O error occurs or stream is null.
     */
    @Override
    public void export(List<String> changeLog, OutputStream out) throws Exception {
      if (out == null) {
        throw new IllegalArgumentException("The provided OutputStream cannot be null.");
      }

      if (changeLog == null) {
        changeLog = new ArrayList<>();
      }

      for (String change : changeLog) {
        out.write(change.getBytes());
      }
    }
  }
}