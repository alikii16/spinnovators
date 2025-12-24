package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetEditorTest {

    @Test
    public void testBudgetHierarchyAndEdgeCasesWithUpdate() {
        // --- Create entries with different values including zero, negative, and large amounts ---
        EnvEntry entry1 = new EnvEntry("cost_positive", 100.0);
        EnvEntry entry2 = new EnvEntry("cost_zero", 0.0);
        EnvEntry entry3 = new EnvEntry("cost_negative", -50.0);
        EnvEntry entry4 = new EnvEntry("cost_large", 1_000_000.0);

        // --- Create a unit that contains all entries ---
        EnvUnit unit = new EnvUnit("unit1", List.of(entry1, entry2, entry3, entry4));

        // --- Create a sector that contains the unit ---
        EnvSector sector = new EnvSector("sector1", List.of(unit));

        // --- Create a year that contains the sector ---
        EnvYear year = new EnvYear("2025", List.of(sector));

        // --- Create EnvBudgetData with the year and initial total budget using mutable maps ---
        EnvBudgetData data = new EnvBudgetData(
                new HashMap<>(Map.of("2025", year)),
                new HashMap<>(Map.of("2025", 1_050_050.0))
        );

        // --- Assertions for basic structure ---
        assertEquals(year, data.getBudgetForYear("2025"));
        assertEquals(1_050_050.0, data.getEnvMinistryTotalBudget().get("2025"));

        assertEquals(sector, year.getSectors().get(0));
        assertEquals(unit, sector.getUnits().get(0));

        // --- Assertions for entry amounts and edge numeric values ---
        assertEquals(100.0, unit.getEntryByKey("cost_positive").getAmount());
        assertEquals(0.0, unit.getEntryByKey("cost_zero").getAmount());
        assertEquals(-50.0, unit.getEntryByKey("cost_negative").getAmount());
        assertEquals(1_000_000.0, unit.getEntryByKey("cost_large").getAmount());

        // --- Check that findEntry works correctly ---
        assertEquals(entry1, year.findEntry("sector1", "unit1", "cost_positive"));
        assertEquals(entry2, year.findEntry("sector1", "unit1", "cost_zero"));
        assertEquals(entry3, year.findEntry("sector1", "unit1", "cost_negative"));
        assertEquals(entry4, year.findEntry("sector1", "unit1", "cost_large"));

        // --- Check that non-existing entry returns null ---
        assertNull(year.findEntry("sector1", "unit1", "nonexistent"));

        // --- Point 4: Check EnvBudgetData update behavior ---
        // Update the total budget for 2025
        data.getEnvMinistryTotalBudget().put("2025", 2_000_000.0);
        // Assert that the new value is correctly updated
        assertEquals(2_000_000.0, data.getEnvMinistryTotalBudget().get("2025"));
    }
}
