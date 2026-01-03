package gr.det.spinnovators.envdatamodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;

import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvEntry;

/**
 * Unit tests for EnvYear class.
 */
public class EnvYearTest {

    @Test
    public void testGetters() {
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
        EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
        List<EnvSector> sectors = Collections.singletonList(sector);

        EnvYear year = new EnvYear("2025", sectors);

        assertEquals("2025", year.getYear(),
            "getYear should return the correct year");
        assertEquals(sectors, year.getSectors(),
            "getSectors should return the correct list of sectors");
    }

    @Test
    public void testFindEntryExisting() {
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
        EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
        EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

        EnvEntry found = year.findEntry("sector1", "unit1", "personnel_costs");
        assertEquals(entry, found, "findEntry should return the correct EnvEntry object");
    }

    @Test
    public void testFindEntryNonExistingSector() {
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
        EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
        EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

        EnvEntry found = year.findEntry("nonexistent_sector", "unit1", "personnel_costs");
        assertNull(found, "findEntry should return null for a nonexistent sector");
    }

    @Test
    public void testFindEntryNonExistingUnit() {
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
        EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
        EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

        EnvEntry found = year.findEntry("sector1", "nonexistent_unit", "personnel_costs");
        assertNull(found, "findEntry should return null for a nonexistent unit");
    }

    @Test
    public void testFindEntryNonExistingEntry() {
        EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
        EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
        EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
        EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

        EnvEntry found = year.findEntry("sector1", "unit1", "nonexistent_entry");
        assertNull(found, "findEntry should return null for a nonexistent entry");
    }

    @Test
    public void testFindEntryNullKeys() {
        EnvYear year = new EnvYear("2025", Collections.emptyList());

        assertNull(year.findEntry(null, "unit1", "entry"),
            "findEntry should return null if sectorKey is null");
        assertNull(year.findEntry("sector1", null, "entry"),
            "findEntry should return null if unitKey is null");
        assertNull(year.findEntry("sector1", "unit1", null),
            "findEntry should return null if entryKey is null");
    }
}
