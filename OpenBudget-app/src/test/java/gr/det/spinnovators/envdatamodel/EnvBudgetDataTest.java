package gr.det.spinnovators.envdatamodel;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link EnvBudgetData} class.
 *
 * <p>This test suite ensures 100% code coverage by verifying:
 * <ul>
 * <li>Initialization with valid Maps (Main Constructor).</li>
 * <li>Initialization with null Maps (Defensive copying logic).</li>
 * <li>Initialization with a List of EnvYear objects (Auxiliary Constructor).</li>
 * <li>Initialization with a null List (Edge case handling).</li>
 * <li>Data retrieval methods.</li>
 * </ul>
 * </p>
 */
class EnvBudgetDataTest {

  // --- Tests for Main Constructor (Map, Map) ---

  /**
   * Test Case: Main constructor with valid, non-null maps.
   * Verifies that data is correctly stored and retrievable.
   */
  @Test
  void testMainConstructorWithValidData() {
    // Prepare mock data
    Map<String, EnvYear> yearMap = new HashMap<>();
    EnvYear year2025 = new EnvYear("2025", new ArrayList<>());
    yearMap.put("2025", year2025);

    Map<String, Double> budgetMap = new HashMap<>();
    budgetMap.put("2025", 5000.0);

    // Initialize
    EnvBudgetData data = new EnvBudgetData(yearMap, budgetMap);

    // Assertions
    assertNotNull(data.getBudgetForYear("2025"), "Should retrieve the year object.");
    assertEquals("2025", data.getBudgetForYear("2025").getYear());
    assertEquals(5000.0, data.getEnvMinistryTotalBudget().get("2025"));
  }

  /**
   * Test Case: Main constructor with NULL arguments.
   * <p>Covers the defensive logic: <code>(dataByYear != null) ? ... : new HashMap<>()</code></p>
   */
  @Test
  void testMainConstructorWithNulls() {
    // Initialize with nulls
    EnvBudgetData data = new EnvBudgetData(null, null);

    // Assertions - should not throw NullPointerException
    assertNull(data.getBudgetForYear("2025"), "Map should be empty, returning null for any key.");
    assertTrue(data.getEnvMinistryTotalBudget().isEmpty(), "Total budget map should be empty.");
  }

  // --- Tests for Auxiliary Constructor (List<EnvYear>) ---

  /**
   * Test Case: List constructor with a valid list of years.
   * Verifies that the list is correctly converted into the internal Map.
   */
  @Test
  void testListConstructorWithValidList() {
    // Prepare list
    List<EnvYear> yearList = new ArrayList<>();
    yearList.add(new EnvYear("2025", new ArrayList<>()));
    yearList.add(new EnvYear("2026", new ArrayList<>()));

    // Initialize
    EnvBudgetData data = new EnvBudgetData(yearList);

    // Assertions
    assertNotNull(data.getBudgetForYear("2025"), "2025 should be mapped.");
    assertNotNull(data.getBudgetForYear("2026"), "2026 should be mapped.");
    assertNull(data.getBudgetForYear("2030"), "Non-existent year should return null.");
        
    // Verify total budget map is initialized (even if empty)
    assertNotNull(data.getEnvMinistryTotalBudget(), "Total budget map should not be null.");
    assertTrue(data.getEnvMinistryTotalBudget().isEmpty(), "Total budget map should be empty for this constructor.");
  }

  /**
   * Test Case: List constructor with a NULL list.
   * <p>Covers the check: <code>if (yearsList != null)</code></p>
   */
  @Test
  void testListConstructorWithNullList() {
    // Initialize with null list
    EnvBudgetData data = new EnvBudgetData((List<EnvYear>) null);

    // Assertions
    assertNull(data.getBudgetForYear("2025"), "Internal map should be empty.");
    assertNotNull(data.getEnvMinistryTotalBudget(), "Total budget map should be initialized to empty.");
  }

  // --- Tests for Getters & Immutability ---

  /**
   * Test Case: Verify data retrieval for non-existent years.
   */
  @Test
  void testGetBudgetForUnknownYear() {
    EnvBudgetData data = new EnvBudgetData(new HashMap<>(), new HashMap<>());
    assertNull(data.getBudgetForYear("2099"), "Should return null for unknown year.");
  }

  /**
   * Test Case: Verify unmodifiability of the total budget map.
   * Attempts to modify the returned map should throw an exception.
   */
  @Test
  void testGetEnvMinistryTotalBudgetIsUnmodifiable() {
    Map<String, Double> budgetMap = new HashMap<>();
    budgetMap.put("2025", 100.0);
        
    EnvBudgetData data = new EnvBudgetData(new HashMap<>(), budgetMap);
    Map<String, Double> retrievedMap = data.getEnvMinistryTotalBudget();

    // Assert read access
    assertEquals(100.0, retrievedMap.get("2025"));

    // Assert write access is forbidden
    assertThrows(UnsupportedOperationException.class, () -> {
      retrievedMap.put("2026", 200.0);
    }, "Should throw exception when trying to modify unmodifiable map.");
  }
}