package gr.det.spinnovators.envdatamodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link EnvSector} class.
 *
 * <p>This test class verifies the getter methods and the unit lookup
 * functionality, ensuring correct retrieval of sector data and nested
 * units by their keys.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>Basic getter methods (getJsonKey, getUnits)</li>
 * <li>Successful unit lookup by key using getUnitByKey</li>
 * <li>Null handling for non-existent unit keys</li>
 * <li>Edge case with null key parameter</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvSectorTest {

  /**
   * Tests the basic getter methods of EnvSector.
   * Verifies that the sector key and the list of units are correctly returned.
   */
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

  /**
   * Tests unit retrieval by key for existing units.
   * Verifies that the correct EnvUnit object is returned for valid keys.
   */
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

  /**
   * Tests unit retrieval for keys that do not exist in the sector.
   * Verifies that the method returns null instead of throwing an exception.
   */
  @Test
  public void testGetUnitByKeyNonExisting() {
    EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
    List<EnvUnit> units = Arrays.asList(unit1);

    EnvSector sector = new EnvSector("energy_sector", units);

    assertNull(sector.getUnitByKey("nonexistent"),
        "getUnitByKey should return null for a key that does not exist");
  }

  /**
   * Tests unit retrieval when the provided key is null.
   * Verifies robust handling of null input parameters.
   */
  @Test
  public void testGetUnitByKeyNullKey() {
    EnvUnit unit1 = new EnvUnit("unit1", Collections.emptyList());
    List<EnvUnit> units = Arrays.asList(unit1);

    EnvSector sector = new EnvSector("energy_sector", units);

    assertNull(sector.getUnitByKey(null),
        "getUnitByKey should return null when key is null");
  }
}
