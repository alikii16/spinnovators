package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvBudgetPrinterTest {

    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() {
        // Capture System.out output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPrintYearlyBudget_withMockedTranslator() {

        // Create a mock translator so we can control the returned text.
        EnvBudgetTranslator translator = Mockito.mock(EnvBudgetTranslator.class);

        // Define mock translations (return the same key to keep output predictable).
        Mockito.when(translator.translateCategory("energy")).thenReturn("energy");
        Mockito.when(translator.translateCategory("general_secretariat")).thenReturn("general_secretariat");
        Mockito.when(translator.translateCategory("personnel_costs")).thenReturn("personnel_costs");
        Mockito.when(translator.translateCategory("equipment_costs")).thenReturn("equipment_costs");

        // Prepare sample budget data.
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        EnvEntry entry2 = new EnvEntry("equipment_costs", 500.0);

        EnvUnit unit = new EnvUnit("general_secretariat", List.of(entry1, entry2));
        EnvSector sector = new EnvSector("energy", List.of(unit));
        EnvYear year = new EnvYear("2025", List.of(sector));

        EnvBudgetData data = new EnvBudgetData(Map.of("2025", year), Map.of());

        // Create the printer using mocked translator.
        EnvBudgetPrinter printer = new EnvBudgetPrinter(data, translator);

        // Execute the method.
        printer.printYearlyBudget("2025");

        // Capture console output.
        String output = outContent.toString();

        // Validate printed values.
        assertTrue(output.contains("ΤΟΜΕΑΣ: energy"));
        assertTrue(output.contains("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: general_secretariat"));
        assertTrue(output.contains("personnel_costs"));
        assertTrue(output.contains("equipment_costs"));
        assertTrue(output.contains("1.500,00 €"));
    }
}
