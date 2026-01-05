
package gr.det.spinnovators.printer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gr.det.spinnovators.data.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class FullBudgetPrinterTest {

    
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;
    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        originalLocale = Locale.getDefault();
        
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    @Test
    public void testShowBudget2026() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);

        printer.showBudget("2026");
        String output = outputStream.toString();

        assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2026"), "There has to be a header for year 2026");
        assertTrue(output.contains(data.getNames26()[0]), "At least the first ministry should be presented");
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"), "The total budget line should be present");

        
       
    }

    @Test
    public void testShowBudget2025() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);

        printer.showBudget("2025");
        String output = outputStream.toString();

        assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2025"), "There has to be a header for year 2025");
        assertTrue(output.contains(data.getNames25()[0]), "At least the first ministry should be presented");
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"), "The total budget line should be present");
        
        
           
    }

    @Test
    public void testShowBudget2024() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);
       
        printer.showBudget("2024");
        String output = outputStream.toString();

        assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2024"), "There has to be a header for the year 2024");
        assertTrue(output.contains(data.getNames24()[0]), "At least the first ministry should be presented");
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"), "The total budget line should be present");

        
        
    }

    @Test
    public void testShowBudget2023() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);
       
        printer.showBudget("2023");
        String output = outputStream.toString();

        assertTrue(output.contains("ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2023"), "There has to be a header for year 2023");
        assertTrue(output.contains(data.getNames23()[0]), "At least the first ministry should be presented");
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"), "The total budget line should be present");

       
       
        assertFalse(output.contains("null"), "The output should not contain 'null' values");
    }

    @Test
    public void testShowBudgetInvalidYear() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);

        printer.showBudget("2022");
        String output = outputStream.toString();

        assertTrue(output.contains("Δεν υπάρχουν δεδομένα για το έτος 2022"), "An appropriate message should be shown for invalid year");
    }


}
