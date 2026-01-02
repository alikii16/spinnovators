package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;

/**
 * Unit tests for EnvSector class.
 * Tests getters and the getUnitByKey helper method.
 */
public class EnvSectorTest {

    @Test
    public void testGetters() {
        EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
        EnvUnit unit2 = new EnvUnit("unit2", Collections.emptyList());
        List<EnvUnit> units = Arrays.asList(unit1, unit2);

        EnvSector sector = new EnvSector("energy_sector", units);

        assertEquals("energy_sector", sector.getJsonKey(),
            "getJsonKey should return the correct key");
        assertEquals(units, sector.getUnits(),
            "getUnits should return the correct list of units");
    }

    @Test
    public void testGetUnitByKeyExisting() {
        EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
        EnvUnit unit2 = new EnvUnit("unit2", Collections.emptyList());
        List<EnvUnit> units = Arrays.asList(unit1, unit2);

        EnvSector sector = new EnvSector("energy_sector", units);

        assertEquals(unit1, sector.getUnitByKey("unit1"),
            "getUnitByKey should return the correct unit for key 'unit1'");
        assertEquals(unit2, sector.getUnitByKey("unit2"),
            "getUnitByKey should return the correct unit for key 'unit2'");
    }

    @Test
    public void testGetUnitByKeyNonExisting() {
        EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
        List<EnvUnit> units = Arrays.asList(unit1);

        EnvSector sector = new EnvSector("energy_sector", units);

        assertNull(sector.getUnitByKey("nonexistent"),
            "getUnitByKey should return null for a key that does not exist");
    }

    @Test
    public void testGetUnitByKeyNullKey() {
        EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
        List<EnvUnit> units = Arrays.asList(unit1);

        EnvSector sector = new EnvSector("energy_sector", units);

        assertNull(sector.getUnitByKey(null),
            "getUnitByKey should return null when key is null");
    }
}
