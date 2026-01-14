package gr.det.spinnovators.envdatamodel;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link EnvYear} class.
 *
 * <p>This test suite ensures 100% code coverage by verifying:
 * <ul>
 * <li>Constructor logic, including null handling.</li>
 * <li>Getter methods.</li>
 * <li>Hierarchical entry lookup (findEntry).</li>
 * <li>Edge cases for null keys and non-existent elements.</li>
 * </ul>
 * </p>
 */
class EnvYearTest {

  /**
   * Test Case: Constructor with Valid List.
   * Verifies correct initialization of the year and sectors list.
   */
  @Test
  void testConstructorWithValidSectors() {
    EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
    EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
    EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
    List<EnvSector> sectors = Collections.singletonList(sector);

    EnvYear year = new EnvYear("2025", sectors);

    assertEquals("2025", year.getYear());
    assertEquals(sectors, year.getSectors());
  }

  /**
   * Test Case: Constructor with NULL List.
   * Covers the defensive logic that ensures the sectors list is never null.
   */
  @Test
  void testConstructorWithNullSectors() {
    // Pass null to trigger the ternary operator's else branch
    EnvYear year = new EnvYear("2025", null);

    assertNotNull(year.getSectors(), "Sectors list should not be null even if constructor arg was null");
    assertTrue(year.getSectors().isEmpty(), "Sectors list should be initialized as empty");
  }

  /**
   * Tests the hierarchical search for a budget entry.
   * Verifies that findEntry successfully navigates through sectors and units.
   */
  @Test
  void testFindEntryExisting() {
    EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
    EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
    EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
    EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

    EnvEntry found = year.findEntry("sector1", "unit1", "personnel_costs");
    assertEquals(entry, found, "findEntry should return the correct EnvEntry object");
  }

  /**
   * Tests findEntry when the specified sector does not exist.
   * Verifies that the method returns null instead of throwing an error.
   */
  @Test
  void testFindEntryNonExistingSector() {
    EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
    EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
    EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
    EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

    EnvEntry found = year.findEntry("nonexistent_sector", "unit1", "personnel_costs");
    assertNull(found, "findEntry should return null for a nonexistent sector");
  }

  /**
   * Tests findEntry when the specified unit does not exist in a valid sector.
   * Verifies proper handling of non-existent nested units.
   */
  @Test
  void testFindEntryNonExistingUnit() {
    EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
    EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
    EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
    EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

    EnvEntry found = year.findEntry("sector1", "nonexistent_unit", "personnel_costs");
    assertNull(found, "findEntry should return null for a nonexistent unit");
  }

  /**
   * Tests findEntry when the specified entry key does not exist.
   * Verifies that the search returns null at the deepest level of the hierarchy.
   */
  @Test
  void testFindEntryNonExistingEntry() {
    EnvEntry entry = new EnvEntry("personnel_costs", 1000.0);
    EnvUnit unit = new EnvUnit("unit1", Collections.singletonList(entry));
    EnvSector sector = new EnvSector("sector1", Collections.singletonList(unit));
    EnvYear year = new EnvYear("2025", Collections.singletonList(sector));

    EnvEntry found = year.findEntry("sector1", "unit1", "nonexistent_entry");
    assertNull(found, "findEntry should return null for a nonexistent entry");
  }

  /**
   * Tests findEntry with null parameters at different levels.
   * Verifies that the method is robust against null key inputs.
   */
  @Test
  void testFindEntryNullKeys() {
    EnvYear year = new EnvYear("2025", Collections.emptyList());

    assertNull(year.findEntry(null, "unit1", "entry"),
        "findEntry should return null if sectorKey is null");
    assertNull(year.findEntry("sector1", null, "entry"),
        "findEntry should return null if unitKey is null");
    assertNull(year.findEntry("sector1", "unit1", null),
        "findEntry should return null if entryKey is null");
  }
}