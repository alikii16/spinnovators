
package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class FullBudgetPrinterTest {

    @Test
    public void testShowBudget2025() {
        MinistryDataInput data = new MinistryDataInput();
         FullBudgetPrinter printer = new FullBudgetPrinter(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        printer.ShowBudget("2025");

        String output = outputStream.toString();
        assertTrue(output.contains("--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2025 ---"));
        assertTrue(output.contains(data.getNames25()[0]));
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"));
    }

    @Test
    public void testShowBudget2024() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
       
        printer.ShowBudget("2024");

        String output = outputStream.toString();
        assertTrue(output.contains("--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2024 ---"));
        assertTrue(output.contains(data.getNames24()[0]));
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"));
    }

    @Test
    public void testShowBudget2023() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
       
        printer.ShowBudget("2023");

        String output = outputStream.toString();
        assertTrue(output.contains("--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ 2023 ---"));
        assertTrue(output.contains(data.getNames23()[0]));
        assertTrue(output.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"));
    }

    @Test
    public void testShowBudgetInvalidYear() {
        MinistryDataInput data = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        printer.ShowBudget("2022");

        String output = outputStream.toString();
        assertTrue(output.contains("Δεν υπάρχουν δεδομένα για το έτος 2022"));
    }
}
