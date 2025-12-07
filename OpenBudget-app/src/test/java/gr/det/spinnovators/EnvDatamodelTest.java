package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EnvDatamodelTest {

    @Test
    public void testEnvBudgetDataAndHierarchy() {
        // Δημιουργία sample entry
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        EnvEntry entry2 = new EnvEntry("equipment_costs", 500.0);

        // Δημιουργία unit
        EnvUnit unit = new EnvUnit("general_secretariat", List.of(entry1, entry2));

        // Δημιουργία sector
        EnvSector sector = new EnvSector("energy", List.of(unit));

        // Δημιουργία year
        EnvYear year = new EnvYear("2025", List.of(sector));

        // Δημιουργία EnvBudgetData
        EnvBudgetData data = new EnvBudgetData(Map.of("2025", year), Map.of("2025", 1500.0));

        // Έλεγχοι getters
        assertEquals(year, data.getBudgetForYear("2025"));
        assertEquals(1500.0, data.getEnvMinistryTotalBudget().get("2025"));

        assertEquals(sector, year.getSectors().get(0));
        assertEquals(unit, sector.getUnits().get(0));
        assertEquals(entry1, unit.getEntries().get(0));

        // Έλεγχος findEntry
        EnvEntry foundEntry = year.findEntry("energy", "general_secretariat", "personnel_costs");
        assertNotNull(foundEntry);
        assertEquals(1000.0, foundEntry.getAmount());

        // Έλεγχος getUnitByKey και getEntryByKey
        assertEquals(unit, sector.getUnitByKey("general_secretariat"));
        assertNull(sector.getUnitByKey("non_existing_unit"));

        assertEquals(entry1, unit.getEntryByKey("personnel_costs"));
        assertNull(unit.getEntryByKey("non_existing_entry"));
    }

    @Test
    public void testEnvYearFindEntryWithNullKeys() {
        EnvYear year = new EnvYear("2025", List.of());
        assertNull(year.findEntry(null, "unit", "entry"));
        assertNull(year.findEntry("sector", null, "entry"));
        assertNull(year.findEntry("sector", "unit", null));
    }
}
