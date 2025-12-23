package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Simple and safe test for EditsApplier (interactive class)
public class EditsApplierTest {

    // Dummy translator that returns the key as-is
    static class DummyTranslator extends EnvBudgetTranslator {
        @Override
        public String translateCategory(String key) {
            return key;
        }
    }

    @Test
    public void testApplyEditsToYearRunsWithoutException() {
        // --- Create sample data hierarchy ---
        EnvEntry entry = new EnvEntry("entry1", 100.0);
        EnvUnit unit = new EnvUnit("unit1", List.of(entry));
        EnvSector sector = new EnvSector("sector1", List.of(unit));
        EnvYear year = new EnvYear("2025", List.of(sector));

        // --- Simulate user input ---
        // 0 -> immediately exit (balance is zero, so allowed)
        String simulatedInput = "0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // --- Create EditsApplier ---
        EditsApplier applier = new EditsApplier(new DummyTranslator());

        // --- Assert that method executes without crashing ---
        assertDoesNotThrow(() -> applier.applyEditsToYear(year));

        // --- Assert that data remains unchanged ---
        assertEquals(100.0, entry.getAmount(), 0.001);
    }
}
