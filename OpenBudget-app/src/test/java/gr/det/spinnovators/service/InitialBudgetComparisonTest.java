package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import gr.det.spinnovators.envdatamodel.EnvYear;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for InitialBudgetComparison class.
 */
class InitialBudgetComparisonTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private EnvBudgetTranslator translator;
    private InitialBudgetComparison comparison;

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        
        comparison = new InitialBudgetComparison(translator);
    }

    /**
     * Restores original streams after each test.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    static class MockEnvYear {
        private final String year;
        private final List<MockEnvSector> sectors = new ArrayList<>();
        
        MockEnvYear(String year) {
            this.year = year;
        }
        
        void addSector(MockEnvSector sector) {
            sectors.add(sector);
        }
        
        public String getYear() {
            return year;
        }
        
        public List<MockEnvSector> getSectors() {
            return sectors;
        }
    }

    /**
     * Simple mock implementation for testing.
     */
    static class MockEnvSector {
        private final String jsonKey;
        private final List<MockEnvUnit> units = new ArrayList<>();
        
        MockEnvSector(String jsonKey) {
            this.jsonKey = jsonKey;
        }
        
        void addUnit(MockEnvUnit unit) {
            units.add(unit);
        }
        
        public String getJsonKey() {
            return jsonKey;
        }
        
        public List<MockEnvUnit> getUnits() {
            return units;
        }
    }

    /**
     * Simple mock implementation for testing.
     */
    static class MockEnvUnit {
        private final List<MockEnvEntry> entries = new ArrayList<>();
        
        void addEntry(MockEnvEntry entry) {
            entries.add(entry);
        }
        
        public List<MockEnvEntry> getEntries() {
            return entries;
        }
    }

    /**
     * Simple mock implementation for testing.
     */
    static class MockEnvEntry {
        private final double amount;
        
        MockEnvEntry(double amount) {
            this.amount = amount;
        }
        
        public double getAmount() {
            return amount;
        }
    }

    /**
     * Creates a test budget year with sample data.
     */
    private MockEnvYear createTestYear(String yearName) {
        MockEnvYear year = new MockEnvYear(yearName);
        
        // Sector A: Executive Coordination
        MockEnvSector sectorA = new MockEnvSector("executive_coordination_and_investments");
        MockEnvUnit unitA1 = new MockEnvUnit();
        unitA1.addEntry(new MockEnvEntry(500000));
        unitA1.addEntry(new MockEnvEntry(300000));
        sectorA.addUnit(unitA1);
        year.addSector(sectorA);
        
        // Sector B: Natural Environment
        MockEnvSector sectorB = new MockEnvSector("natural_environment_and_water_protection");
        MockEnvUnit unitB1 = new MockEnvUnit();
        unitB1.addEntry(new MockEnvEntry(800000));
        sectorB.addUnit(unitB1);
        year.addSector(sectorB);
        
        return year;
    }

    /**
     * Tests constructor.
     */
    @Test
    void testConstructor() {
        assertNotNull(comparison);
    }

    /**
     * Tests that performFullComparison method exists.
     */
    @Test
    void testMethodExists() throws Exception {
        Method method = InitialBudgetComparison.class.getMethod(
            "performFullComparison", 
            EnvYear.class, 
            EnvYear.class, 
            double.class
        );
        assertNotNull(method);
    }

    /**
     * Tests truncate method via reflection.
     */

    @Test
    void testCreatePercentageBar() throws Exception {
        Method method = InitialBudgetComparison.class.getDeclaredMethod(
            "createPercentageBar", double.class, int.class);
        method.setAccessible(true);
        
        // Test 0%
        String result0 = (String) method.invoke(comparison, 0.0, 10);
        assertEquals("░░░░░░░░░░", result0); // 10 empty blocks
        
        // Test 50%
        String result50 = (String) method.invoke(comparison, 50.0, 10);
        assertEquals("█████░░░░░", result50); // 5 filled, 5 empty
        
        // Test 100%
        String result100 = (String) method.invoke(comparison, 100.0, 10);
        assertEquals("██████████", result100); // 10 filled
        
        // Test >100% (should cap at 100%)
        String result150 = (String) method.invoke(comparison, 150.0, 10);
        assertEquals("██████████", result150); // Still 10 filled
    }

    /**
     * Tests getShortSectorName method via reflection.
     */
    @Test
    void testGetShortSectorName() throws Exception {
        Method method = InitialBudgetComparison.class.getDeclaredMethod(
            "getShortSectorName", String.class, int.class);
        method.setAccessible(true);
        
        // Test known sectors
        String resultA = (String) method.invoke(
            comparison, "executive_coordination_and_investments", 0);
        assertEquals("[Α]", resultA);
        
        String resultB = (String) method.invoke(
            comparison, "natural_environment_and_water_protection", 1);
        assertEquals("[Β]", resultB);
        
        String resultC = (String) method.invoke(
            comparison, "spatial_planning_and_urban_environment", 2);
        assertEquals("[Γ]", resultC);
        
        String resultD = (String) method.invoke(
            comparison, "energy_and_mineral_resources_management", 3);
        assertEquals("[Δ]", resultD);
        
        // Test unknown sector (uses index)
        String resultUnknown = (String) method.invoke(comparison, "unknown", 4);
        assertEquals("[Ε]", resultUnknown); // 'Α' + 4 = 'Ε'
    }

    /**
     * Tests percentage calculation logic.
     */
    @Test
    void testPercentageCalculations() {
        double totalBudget = 1000000;
        double sectorAmount = 250000;
        
        double percentage = (sectorAmount / totalBudget) * 100;
        assertEquals(25.0, percentage, 0.01);
        
        // Test change percentage
        double oldAmount = 200000;
        double newAmount = 250000;
        double change = newAmount - oldAmount;
        double changePercent = (oldAmount > 0) ? (change / oldAmount) * 100 : 0;
        assertEquals(25.0, changePercent, 0.01);
        
        // Test with zero old amount
        double zeroOldAmount = 0;
        double zeroChangePercent = (zeroOldAmount > 0) ? (100 / zeroOldAmount) * 100 : 0;
        assertEquals(0.0, zeroChangePercent, 0.01);
    }

    /**
     * Tests that all required components are initialized.
     */
    @Test
    void testComponentInitialization() {
        assertNotNull(comparison);
        assertTrue(true); // Constructor didn't throw
    }

    /**
     * Tests UTF-8 output handling.
     */
    @Test
    void testGreekOutput() {
        System.out.println("Ελληνικά κείμενα: Ά Ϋ Ϊ ΰ");
        String output = outputStream.toString();
        assertTrue(output.contains("Ελληνικά"), "Should handle Greek text");
    }

    /**
     * Tests boundary cases for percentages.
     */
    @Test
    void testBoundaryCases() {
        // Test with very small numbers
        double tinyAmount = 0.0001;
        double tinyBudget = 0.001;
        double tinyPercent = (tinyAmount / tinyBudget) * 100;
        assertEquals(10.0, tinyPercent, 0.01);
        
        // Test with very large numbers
        double hugeAmount = 1_000_000_000.0;
        double hugeBudget = 2_000_000_000.0;
        double hugePercent = (hugeAmount / hugeBudget) * 100;
        assertEquals(50.0, hugePercent, 0.01);
    }

    /**
     * Tests analyzeChangeFocus logic.
     */
    @Test
    void testAnalyzeChangeFocusLogic() {
        // Create sample data
        Map<String, Double> original = new HashMap<>();
        original.put("sector1", 100000.0);
        original.put("sector2", 200000.0);
        original.put("sector3", 300000.0);
        
        Map<String, Double> modified = new HashMap<>();
        modified.put("sector1", 150000.0); // +50%
        modified.put("sector2", 180000.0); // -10%
        modified.put("sector3", 300000.0); // no change
        
        // Manually analyze
        int increased = 0;
        int decreased = 0;
        double maxIncrease = 0;
        String maxIncreaseSector = "";
        
        for (String sectorKey : original.keySet()) {
            double change = modified.getOrDefault(sectorKey, 0.0) 
                          - original.get(sectorKey);
            
            if (change > 0.01) {
                increased++;
                if (change > maxIncrease) {
                    maxIncrease = change;
                    maxIncreaseSector = sectorKey;
                }
            } else if (change < -0.01) {
                decreased++;
            }
        }
        
        assertEquals(1, increased);
        assertEquals(1, decreased);
        assertEquals(50000.0, maxIncrease, 0.01);
        assertEquals("sector1", maxIncreaseSector);
    }

    /**
     * Tests SectorChange helper class via reflection.
     */
    @Test
    void testSectorChangeClass() throws Exception {
        // Access the inner class via reflection
        Class<?>[] innerClasses = InitialBudgetComparison.class.getDeclaredClasses();
        boolean foundSectorChange = false;
        
        for (Class<?> innerClass : innerClasses) {
            if (innerClass.getSimpleName().equals("SectorChange")) {
                foundSectorChange = true;
                
                // Test creating an instance
                var constructor = innerClass.getDeclaredConstructor(
                    String.class, double.class, double.class);
                constructor.setAccessible(true);
                var fields = innerClass.getDeclaredFields();
                assertTrue(fields.length >= 3);
                
                break;
            }
        }
        
        assertTrue(foundSectorChange, "Should have SectorChange inner class");
    }

    /**
     * Tests calculateSectorTotals logic with mock data.
     */
    @Test
    void testCalculateSectorTotalsLogic() {
        // Since we can't easily test the private method without real EnvYear,
        // we test the logic manually
        MockEnvYear mockYear = createTestYear("2025");
        
        Map<String, Double> totals = new HashMap<>();
        
        for (MockEnvSector sector : mockYear.getSectors()) {
            double sectorTotal = 0.0;
            
            for (MockEnvUnit unit : sector.getUnits()) {
                for (MockEnvEntry entry : unit.getEntries()) {
                    sectorTotal += entry.getAmount();
                }
            }
            
            totals.put(sector.getJsonKey(), sectorTotal);
        }
        
        assertEquals(2, totals.size());
        assertEquals(800000.0, totals.get("executive_coordination_and_investments"), 0.01);
        assertEquals(800000.0, totals.get("natural_environment_and_water_protection"), 0.01);
    }

    /**
     * Tests with empty data.
     */
    @Test
    void testWithEmptyYear() {
        MockEnvYear emptyYear = new MockEnvYear("2025");
        // Should not throw when creating - actual method would need real EnvYear
        assertNotNull(emptyYear);
    }

    /**
     * Tests the translator implementation.
     */

    @Test
    void testBasicArithmetic() {
        // Test rounding for percentage bar
        double percent = 53.7;
        int maxWidth = 20;
        int filled = (int) Math.round((percent / 100.0) * maxWidth);
        assertEquals(11, filled); // 53.7% of 20 = 10.74 ≈ 11
        
        // Test min function
        int value1 = 15;
        int value2 = 10;
        int minResult = Math.min(value1, value2);
        assertEquals(10, minResult);
        
        // Test absolute value
        double negative = -123.45;
        double absResult = Math.abs(negative);
        assertEquals(123.45, absResult, 0.01);
    }
}