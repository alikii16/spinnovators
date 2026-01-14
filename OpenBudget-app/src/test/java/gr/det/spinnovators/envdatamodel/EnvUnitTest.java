package gr.det.spinnovators.envdatamodel;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link EnvUnit} class.
 *
 * <p>This test class verifies the getter methods and the entry lookup
 * functionality, ensuring correct retrieval of unit data and nested
 * budget entries by their keys.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>Basic getter methods (getJsonKey, getEntries)</li>
 * <li>Successful entry lookup by key using getEntryByKey</li>
 * <li>Null handling for non-existent entry keys</li>
 * <li>Edge case with null key parameter</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvUnitTest {

  /**
   * Tests the basic getter methods of EnvUnit.
   * Verifies that the unit's JSON key and the list of entries are correctly initialized.
   */
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

  /**
   * Tests entry retrieval by key for existing entries within the unit.
   * Verifies that the correct EnvEntry object is returned for valid keys.
   */
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

  /**
   * Tests entry retrieval for keys that do not exist in the unit.
   * Verifies that the method returns null instead of failing.
   */
  @Test
  public void testGetEntryByKeyNonExisting() {
    EnvEntry entry1 = new EnvEntry("personnel_costs", 1000.0);
    List<EnvEntry> entries = Collections.singletonList(entry1);

    EnvUnit unit = new EnvUnit("general_secretariat", entries);

    // Non-existent key should return null
    assertNull(unit.getEntryByKey("nonexistent"),
        "getEntryByKey should return null for a key that does not exist");
  }

  /**
   * Tests entry retrieval when the provided key is null.
   * Verifies that the class handles null keys gracefully by returning null.
   */
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