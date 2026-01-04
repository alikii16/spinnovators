package gr.det.spinnovators.envdatamodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;



/**
 * Unit tests for EnvUnit class.
 */
public class EnvUnitTest {

    @Test
    public void testGetters() {
        // Create a single EnvEntry for testing
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        List<EnvEntry> entries = Collections.singletonList(entry);

        // Create EnvUnit
        EnvUnit unit = new EnvUnit("general_secretariat", entries);

        // Test getters
        assertEquals("general_secretariat", unit.getJsonKey(),
            "getJsonKey should return the correct JSON key");
        assertEquals(entries, unit.getEntries(),
            "getEntries should return the correct list of entries");
    }

    @Test
    public void testGetEntryByKeyExisting() {
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        EnvEntry entry2 = new EnvEntry("equipment_costs", 500.0);
        List<EnvEntry> entries = List.of(entry1, entry2);

        EnvUnit unit = new EnvUnit("general_secretariat", entries);

        // Test getting existing entries
        assertEquals(entry1, unit.getEntryByKey("personnel_costs"),
            "getEntryByKey should return the correct entry for 'personnel_costs'");
        assertEquals(entry2, unit.getEntryByKey("equipment_costs"),
            "getEntryByKey should return the correct entry for 'equipment_costs'");
    }

    @Test
    public void testGetEntryByKeyNonExisting() {
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        List<EnvEntry> entries = Collections.singletonList(entry1);

        EnvUnit unit = new EnvUnit("general_secretariat", entries);

        // Non-existent key should return null
        assertNull(unit.getEntryByKey("nonexistent"),
            "getEntryByKey should return null for a key that does not exist");
    }

    @Test
    public void testGetEntryByKeyNullKey() {
        EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
        List<EnvEntry> entries = Collections.singletonList(entry1);

        EnvUnit unit = new EnvUnit("general_secretariat", entries);

        // Null key should return null
        assertNull(unit.getEntryByKey(null),
            "getEntryByKey should return null when the key is null");
    }
}
