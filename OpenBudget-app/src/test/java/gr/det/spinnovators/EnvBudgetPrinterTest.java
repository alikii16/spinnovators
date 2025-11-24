package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetPrinterTest {

    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() {
        // Capture System.out
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPrintYearlyBudget_yearExists_printsAllData() {
        // Δημιουργούμε translator με πραγματικό αντικείμενο
        EnvBudgetTranslator translator = new EnvBudgetTranslator();

        // Δημιουργούμε sample δεδομένα
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        EnvEntry entry2 = new EnvEntry("equipment_costs", 500.0);
        EnvUnit unit = new EnvUnit("general_secretariat", List.of(entry1, entry2));
        EnvSector sector = new EnvSector("energy", List.of(unit));
        EnvYear year = new EnvYear("2025", List.of(sector));
        EnvBudgetData data = new EnvBudgetData(Map.of("2025", year), Map.of("2025", 1500.0));

        EnvBudgetPrinter printer = new EnvBudgetPrinter(data, translator);

        // Καλούμε τη μέθοδο
        printer.printYearlyBudget("2025");

        // Έλεγχος ότι εμφανίζεται σωστά το output
        String output = outContent.toString();
        assertTrue(output.contains("ΑΝΑΛΥΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ"));
        assertTrue(output.contains("ΤΟΜΕΑΣ: energy"));
        assertTrue(output.contains("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: general_secretariat"));
        assertTrue(output.contains("personnel_costs"));
        assertTrue(output.contains("equipment_costs"));
        assertTrue(output.contains("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: 1,500.00 €"));
    }

    @Test
    public void testPrintYearlyBudget_yearDoesNotExist_printsMessage() {
        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        EnvBudgetData emptyData = new EnvBudgetData(Map.of(), Map.of());

        EnvBudgetPrinter printer = new EnvBudgetPrinter(emptyData, translator);

        printer.printYearlyBudget("2025");

        String output = outContent.toString();
        assertTrue(output.contains("Δεν βρέθηκαν αναλυτικά δεδομένα για το έτος 2025"));
    }
}
