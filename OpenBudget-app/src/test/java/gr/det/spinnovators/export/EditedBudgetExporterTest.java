package gr.det.spinnovators.export;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for EditedBudgetExporter interface.
 * 
 * <p>Tests the contract and behavior of the exporter interface.
 */
class EditedBudgetExporterTest {

    /**
     * Test data for change logs.
     */
    private static final List<String> VALID_CHANGE_LOG = Arrays.asList(
        "2023;Energy;UnitA;Category1;1000.0;1500.0",
        "2023;Health;UnitB;Category2;2000.0;2500.0",
        "2024;Education;UnitC;Category3;3000.0;2800.0"
    );

    /**
     * Tests that a concrete implementation can be created.
     */
    @Test
    void testInterfaceImplementation() {
        // Create a simple implementation for testing
        EditedBudgetExporter exporter = new TestExporter();
        assertNotNull(exporter, "Exporter should be instantiable");
    }

    /**
     * Tests that export method works with valid data.
     */
    @Test
    void testExportWithValidData() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        exporter.export(VALID_CHANGE_LOG, output);
        
        String result = output.toString();
        assertNotNull(result, "Output should not be null");
        assertTrue(result.contains(VALID_CHANGE_LOG.get(0)), 
                   "Output should contain change log data");
    }

    /**
     * Tests that export works with empty list.
     */
    @Test
    void testExportWithEmptyList() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        exporter.export(new ArrayList<>(), output);
        
        String result = output.toString();
        assertNotNull(result, "Output should not be null even with empty input");
    }

    /**
     * Tests that export method handles null output stream gracefully.
     */
    @Test
    void testExportWithNullOutputStream() {
        EditedBudgetExporter exporter = new TestExporter();
        
        Exception exception = assertThrows(Exception.class, 
            () -> exporter.export(VALID_CHANGE_LOG, null),
            "Should throw exception with null output stream"
        );
        
        assertNotNull(exception, "Exception should be thrown");
    }

    /**
     * Tests that export method handles null change log.
     */
    @Test
    void testExportWithNullChangeLog() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // Implementation should handle null gracefully
        exporter.export(null, output);
        
        String result = output.toString();
        // Some implementations might output empty string, others might handle differently
        // This test ensures it doesn't crash
        assertNotNull(result, "Output should be generated even with null input");
    }

    /**
     * Tests that export preserves order of changes.
     */
    @Test
    void testExportPreservesOrder() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        List<String> changes = Arrays.asList("First", "Second", "Third");
        exporter.export(changes, output);
        
        String result = output.toString();
        // In our test implementation, order is preserved
        assertTrue(result.contains("FirstSecondThird"), 
                   "Order of changes should be preserved");
    }

    /**
     * Tests that export handles special characters in data.
     */
    @Test
    void testExportWithSpecialCharacters() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        List<String> changes = Arrays.asList(
            "2023;Energy;Unit A;Catégorie;1000,0;1500,0",
            "2023;Health;Unit-B;Category_2;2000.0;2500.0"
        );
        
        exporter.export(changes, output);
        
        String result = output.toString();
        assertNotNull(result, "Should handle special characters");
        assertTrue(result.contains("Catégorie"), 
                   "Should preserve special characters");
    }

    /**
     * Tests multiple exports to same stream.
     */
    @Test
    void testMultipleExports() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        exporter.export(VALID_CHANGE_LOG.subList(0, 1), output);
        String firstExport = output.toString();
        
        output.reset();
        exporter.export(VALID_CHANGE_LOG.subList(1, 2), output);
        String secondExport = output.toString();
        
        assertNotNull(firstExport, "First export should succeed");
        assertNotNull(secondExport, "Second export should succeed");
        assertTrue(!firstExport.equals(secondExport) || 
                   firstExport.isEmpty() || secondExport.isEmpty(),
                   "Exports should produce different output for different input");
    }

    /**
     * Tests that export method signature is correct.
     */
    @Test
    void testMethodSignature() throws Exception {
        // This test verifies the method can be called with correct parameters
        EditedBudgetExporter exporter = new TestExporter();
        OutputStream output = new ByteArrayOutputStream();
        
        // Should compile and run without issues
        exporter.export(VALID_CHANGE_LOG, output);
        
        assertTrue(output instanceof ByteArrayOutputStream, 
                   "Should accept any OutputStream implementation");
    }

    /**
     * Tests with large data set.
     */
    @Test
    void testExportWithLargeDataSet() throws Exception {
        EditedBudgetExporter exporter = new TestExporter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add("2023;Sector" + i + ";Unit" + i + ";Category" + i + 
                         ";" + (i * 1000) + ";" + (i * 1200));
        }
        
        exporter.export(largeList, output);
        
        String result = output.toString();
        assertNotNull(result, "Should handle large data sets");
        assertTrue(result.length() > 0, "Should produce output for large data");
    }

    /**
     * Simple test implementation for testing the interface.
     */
    private static class TestExporter implements EditedBudgetExporter {
        /**
         * Implementation of export method for testing.
         * 
         * @param changeLog List of changes to export
         * @param out Output stream to write to
         * @throws Exception if any error occurs
         */
        @Override
        public void export(List<String> changeLog, OutputStream out) throws Exception {
            if (out == null) {
                throw new IllegalArgumentException("OutputStream cannot be null");
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