package gr.det.spinnovators.export;

import gr.det.spinnovators.export.TextReportExporter;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    
        assertTrue(result.contains("ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ"), "Λείπει ο τίτλος");
        assertTrue(result.contains("2025"), "Λείπει το έτος");
  
        assertTrue(result.contains("Clean Energy > Renewables"));
        assertTrue(result.contains("Wind Turbines"));
        
    
        assertTrue(result.contains("1,000.00 €"), "Λάθος παλιό ποσό");
        assertTrue(result.contains("1,500.50 €"), "Λάθος νέο ποσό");
        assertTrue(result.contains("+500.50 €"), "Λάθος διαφορά (+)");


        assertTrue(result.contains("Water > Rivers"));
        assertTrue(result.contains("-100.00 €"), "Λάθος αρνητική διαφορά");
    }

    @Test
    void testExportEmptyLog() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        exporter.export(new ArrayList<>(), outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("Δεν πραγματοποιήθηκαν αλλαγές"), 
            "Πρέπει να εμφανίζει ενημερωτικό μήνυμα όταν η λίστα είναι κενή");
    }


    @Test
    void testExportLongCategoryTruncation() throws UnsupportedEncodingException {
        TextReportExporter exporter = new TextReportExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        String longCategory = "Αυτό είναι ένα πάρα πολύ μεγάλο όνομα κατηγορίας που πρέπει να κοπεί";
        List<String> changeLog = new ArrayList<>();
        changeLog.add("2025;Sec;Unit;" + longCategory + ";100;200");

        exporter.export(changeLog, outputStream);
        String result = outputStream.toString(StandardCharsets.UTF_8.name());

        assertTrue(result.contains("..."), "Δεν προστέθηκαν τα αποσιωπητικά (...)");
        assertFalse(result.contains(longCategory), "Το μεγάλο κείμενο έπρεπε να έχει κοπεί");
    }
}
