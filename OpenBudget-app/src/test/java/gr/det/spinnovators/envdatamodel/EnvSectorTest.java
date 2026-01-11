package gr.det.spinnovators.envdatamodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link EnvSector} class.
 *
 * <p>This test class verifies the getter methods and the unit lookup
 * functionality, ensuring correct retrieval of sector data and nested
 * units by their keys.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>Basic getter methods (getJsonKey, getUnits)</li>
 *   <li>Successful unit lookup by key using getUnitByKey</li>
 *   <li>Null handling for non-existent unit keys</li>
 *   <li>Edge case with null key parameter</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
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
