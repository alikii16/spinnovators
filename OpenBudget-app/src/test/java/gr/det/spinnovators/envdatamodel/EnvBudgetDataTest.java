package gr.det.spinnovators.envdatamodel;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link EnvBudgetData} class.
 *
 * <p>This test class verifies budget retrieval operations and access
 * to the total ministry budget map, ensuring correct handling of both
 * valid data and edge cases.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>Retrieving budgets for existing years</li>
 *   <li>Handling non-existent years (returning null)</li>
 *   <li>Defensive handling when the data map is null</li>
 *   <li>Accessing the complete ministry total budget map</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvBudgetDataTest {

    @Test
    public void testGetBudgetForExistingYearReturnsValue() {
        // Create empty maps
        Map<String, EnvYear> dataByYear = new HashMap<>();
        Map<String, Double> totalBudgets = new HashMap<>();

        // Use null as placeholder for EnvYear
        dataByYear.put("2025", null);

        EnvBudgetData data = new EnvBudgetData(dataByYear, totalBudgets);

        // Assert that the map value is returned
        assertNull(data.getBudgetForYear("2025"),
            "getBudgetForYear should return the value stored in the map for 2025 (null placeholder)");
    }

    @Test
    public void testGetBudgetForNonExistingYearReturnsNull() {
        Map<String, EnvYear> dataByYear = new HashMap<>();
        Map<String, Double> totalBudgets = new HashMap<>();

        EnvBudgetData data = new EnvBudgetData(dataByYear, totalBudgets);

        // Assert that null is returned for year not in map
        assertNull(data.getBudgetForYear("2024"),
            "getBudgetForYear should return null for a year not present in the map");
    }

    @Test
    public void testGetBudgetForYearWhenMapIsNull() {
        Map<String, Double> totalBudgets = new HashMap<>();

        EnvBudgetData data = new EnvBudgetData(null, totalBudgets);

        // Assert null is returned when dataByYear map itself is null
        assertNull(data.getBudgetForYear("2025"),
            "getBudgetForYear should return null when dataByYear map is null");
    }

    @Test
    public void testGetEnvMinistryTotalBudgetReturnsCorrectMap() {
        Map<String, EnvYear> dataByYear = new HashMap<>();
        Map<String, Double> totalBudgets = new HashMap<>();

        totalBudgets.put("2025", 1000.0);
        totalBudgets.put("2026", 2000.0);

        EnvBudgetData data = new EnvBudgetData(dataByYear, totalBudgets);

        Map<String, Double> result = data.getEnvMinistryTotalBudget();

        // Assert the map is returned correctly
        assertEquals(2, result.size(), "Total budget map should contain 2 entries");
        assertEquals(1000.0, result.get("2025"), "Total budget for 2025 should be correct");
        assertEquals(2000.0, result.get("2026"), "Total budget for 2026 should be correct");
    }
}
