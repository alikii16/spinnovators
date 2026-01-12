package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link EditsApplier} class.
 *
 * <p>Uses simulated user input (via Scanner injections) to navigate the menus
 * and verify the application logic without requiring actual console interaction.</p>
 */
public class EditsApplierTest {

    // Dummy translator for predictable output in tests
    static class DummyTranslator extends EnvBudgetTranslator {
        @Override
        public String translateCategory(String key) {
            return key; // Return the key as-is for easy searching
        }
    }

    /**
     * Tests a full editing cycle: Select Sector -> Select Unit -> Edit Entry -> Revert -> Exit.
     * <p>This scenario ensures we enter multiple methods like selectSector, selectUnit,
     * findAndEditEntryInUnit, and validation logic.</p>
     */
    @Test
    public void testInteractiveEditCycle() {
        // --- 1. Setup Data ---
        // Sector -> Unit -> Entry (Amount: 100.0)
        EnvEntry entry = new EnvEntry("entry1", 100.0);
        EnvUnit unit = new EnvUnit("unit1", List.of(entry));
        EnvSector sector = new EnvSector("sector1", List.of(unit));
        EnvYear year = new EnvYear("2025", List.of(sector));

        // --- 2. Simulate User Input ---
        // The sequence of inputs "fake" a user typing in the console.
        String simulatedInput = 
            "1\n" +       // Select Sector 1
            "1\n" +       // Select Unit 1
            "entry1\n" +  // Search for entry name "entry1"
            "150\n" +     // Change amount to 150 (Budget is now UNBALANCED by +50)
            "1\n" +       // Select Unit 1 again
            "entry1\n" +  // Search entry again
            "100\n" +     // Change back to 100 (Budget is now BALANCED)
            "0\n" +       // Go back from Unit menu
            "0\n";        // Exit main menu (Allowed because balance is 0)

        // Create a Scanner that reads from our string instead of the keyboard
        Scanner mockScanner = new Scanner(
            new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8))
        );

        // --- 3. Run Applier ---
        EditsApplier applier = new EditsApplier(new DummyTranslator(), mockScanner);
        
        assertDoesNotThrow(() -> applier.applyEditsToYear(year));

        // --- 4. Assertions ---
        // The value changed to 150 and back to 100, so it should be 100.
        assertEquals(100.0, entry.getAmount(), 0.001, "Entry amount should be restored to 100.0");
    }

    @Test
    public void testInvalidMenuInput() {
        EnvYear year = new EnvYear("2025", List.of()); // No sectors
        
        // Input: "invalid" (text instead of number), then "0" to exit
        String simulatedInput = "invalid\n0\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        EditsApplier applier = new EditsApplier(new DummyTranslator(), mockScanner);
        
        assertDoesNotThrow(() -> applier.applyEditsToYear(year));
    }
}
