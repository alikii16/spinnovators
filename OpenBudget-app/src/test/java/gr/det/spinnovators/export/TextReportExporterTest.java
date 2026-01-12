package gr.det.spinnovators.export;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TextReportExporter}.
 *
 * <p>Ensures correct report generation for:
 * <ul>
 * <li>Standard full data (6 fields).</li>
 * <li>Legacy/Partial data (3-5 fields) [Critical for Branch Coverage].</li>
 * <li>Malformed numbers (NumberFormatException) [Critical for Exception Coverage].</li>
 * <li>Empty or Null logs.</li>
 * <li>Year extraction edge cases.</li>
 * </ul>
 * </p>
 */
class TextReportExporterTest {

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

    @Test
    void testExportEmptyLog() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        exporter.export(new ArrayList<>(), outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("Δεν πραγματοποιήθηκαν αλλαγές"), 
            "Should show empty message");
    }

    @Test
    void testExportNullLog() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        exporter.export(null, outputStream); // Pass null
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("Δεν πραγματοποιήθηκαν αλλαγές"), 
            "Should handle null log safely");
        assertTrue(result.contains("----"), "Year should be placeholder");
    }

    @Test
    void testExportPartialDataFallback() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        List<String> changeLog = new ArrayList<>();
        // Only 3 fields (Year;Sector;Unit) - Should trigger "else if (parts.length >= 3)"
        // This covers the red branch in image_f3a597.png
        changeLog.add("2025;PartialSector;PartialUnit");

        exporter.export(changeLog, outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("PartialSector"), "Should print raw entry for partial data");
    }

    @Test
    void testExportWithMalformedNumbers() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        List<String> changeLog = new ArrayList<>();
        // 6 fields, but values are not numbers -> Triggers NumberFormatException catch block
        changeLog.add("2025;Sector;Unit;Cat;NotANumber;AlsoNot");

        exporter.export(changeLog, outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("Error parsing values"), "Should catch exception and print error tag");
    }

    @Test
    void testExportInvalidDataSkipped() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        List<String> changeLog = new ArrayList<>();
        // Only 2 fields - Should be skipped entirely (implicit else)
        changeLog.add("2025;BrokenData");

        exporter.export(changeLog, outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertFalse(result.contains("BrokenData"), "Should skip invalid data (< 3 parts)");
    }

    @Test
    void testYearExtractionEdgeCase() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        List<String> changeLog = new ArrayList<>();
        // An entry consisting only of delimiters results in an empty array from split()
        changeLog.add(";;;;;"); 

        exporter.export(changeLog, outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("----"), "Should fall back to placeholder year if split results are empty");
    }

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
