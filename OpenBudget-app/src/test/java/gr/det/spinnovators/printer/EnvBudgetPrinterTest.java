package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link EnvBudgetPrinter}.
 *
 * <p>This test suite ensures 100% Branch Coverage by verifying:
 * <ul>
 * <li>Printing a complete yearly budget report (Happy Path).</li>
 * <li>Handling requests for years with no data (Error Path).</li>
 * </ul>
 * </p>
 */
public class EnvBudgetPrinterTest {

    private ByteArrayOutputStream outContent;
    private EnvBudgetTranslator simpleTranslator;

    @BeforeEach
    public void setUp() throws Exception {
        // Capture System.out output in UTF-8
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));

        // Simple Translator implementation for testing (Avoids Mockito dependency)
        simpleTranslator = new EnvBudgetTranslator() {
            @Override
            public String translateCategory(String key) {
                return "Translated_" + key;
            }
        };
    }

    /**
     * Test Case: Print Budget for an Existing Year.
     * Verifies that sectors, units, entries, and totals are printed correctly.
     */
    @Test
    public void testPrintYearlyBudget_Success() {
        // 1. Prepare Data
        EnvEntry entry1 = new EnvEntry("personnel", 1000.0);
        EnvEntry entry2 = new EnvEntry("equipment", 500.0);

        EnvUnit unit = new EnvUnit("unit_secretariat", List.of(entry1, entry2));
        EnvSector sector = new EnvSector("sector_energy", List.of(unit));
        EnvYear year = new EnvYear("2025", List.of(sector));

        Map<String, EnvYear> dataMap = new HashMap<>();
        dataMap.put("2025", year);
        
        EnvBudgetData data = new EnvBudgetData(dataMap, new HashMap<>());

        // 2. Initialize Printer
        EnvBudgetPrinter printer = new EnvBudgetPrinter(data, simpleTranslator);

        // 3. Execute
        printer.printYearlyBudget("2025");

        // 4. Verify Output
        String output = outContent.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Translated_sector_energy"), "Should print translated sector name");
        assertTrue(output.contains("Translated_unit_secretariat"), "Should print translated unit name");
        assertTrue(output.contains("Translated_personnel"), "Should print translated entry name");
        assertTrue(output.contains("1.500,00"), "Should print correct unit total (1000 + 500)");
    }

    /**
     * Test Case: Print Budget for Non-Existent Year.
     * Covers the branch: if (yearlyBudget == null)
     */
    @Test
    public void testPrintYearlyBudget_NoData() {
        // Initialize empty data
        EnvBudgetData emptyData = new EnvBudgetData(new HashMap<>(), new HashMap<>());

        EnvBudgetPrinter printer = new EnvBudgetPrinter(emptyData, simpleTranslator);

        // Execute for unknown year
        printer.printYearlyBudget("2099");

        String output = outContent.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Δεν βρέθηκαν αναλυτικά δεδομένα"), 
            "Should print error message when year is missing");
        assertTrue(output.contains("2099"), 
            "Error message should contain the requested year");
    }
}
