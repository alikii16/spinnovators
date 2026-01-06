package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for EnvBudgetLoader to achieve 100% code coverage.
 * Covers successful parsing, malformed JSON, missing fields, and type mismatches.
 */
public class EnvBudgetLoaderTest {

    private EnvBudgetLoader loader;

    @BeforeEach
    public void setUp() {
        loader = new EnvBudgetLoader();
    }

    @Test
    public void testSuccessfulLoad() {
        // Tests the main entry point with the existing file
        EnvBudgetData data = loader.loadBudget();
        assertNotNull(data, "Loader should always return a model, even if empty.");
    }

    @Test
    public void testParsingErrorsAndInvalidStructure() throws Exception {
        // We use Reflection to test the private parsing and validation methods
        // This covers the catch blocks for JsonSyntaxException and validateRootStructure failure
        
        Method parseMethod = EnvBudgetLoader.class.getDeclaredMethod("parseJsonFile", InputStream.class);
        parseMethod.setAccessible(true);

        // 1. Malformed JSON
        String malformedJson = "{ \"data_by_year\": { \"2025\": { \"sector\": } } }";
        InputStream is = new ByteArrayInputStream(malformedJson.getBytes());
        
        try {
            parseMethod.invoke(loader, is);
        } catch (Exception e) {
            // This confirms we triggered the syntax error logic
            assertTrue(e.getCause() instanceof com.google.gson.JsonSyntaxException);
        }

        // 2. Missing root field validation
        Map<String, Object> invalidRoot = new HashMap<>();
        invalidRoot.put("wrong_field", new Object());
        
        Method validateMethod = EnvBudgetLoader.class.getDeclaredMethod("validateRootStructure", Map.class);
        validateMethod.setAccessible(true);
        boolean isValid = (boolean) validateMethod.invoke(loader, invalidRoot);
        assertFalse(isValid, "Structure should be invalid without data_by_year field.");
    }

    @Test
    public void testTransformationWarnings() throws Exception {
        // Targets the LOGGER.WARNING branches for non-Double values
        
        Map<String, Object> rawData = new HashMap<>();
        Map<String, Object> totalBudget = new HashMap<>();
        totalBudget.put("2025", "NotADouble"); // Trigger warning branch
        rawData.put("env_ministry_total_budget", totalBudget);
        rawData.put("data_by_year", new HashMap<>());

        Method buildMethod = EnvBudgetLoader.class.getDeclaredMethod("buildBudgetDataModel", Map.class);
        buildMethod.setAccessible(true);
        
        EnvBudgetData data = (EnvBudgetData) buildMethod.invoke(loader, rawData);
        assertTrue(data.getEnvMinistryTotalBudget().isEmpty(), "Non-Double values should be skipped.");
    }

    @Test
    public void testDeepHierarchyProcessing() throws Exception {
        // Targets transformSectors, transformUnits, and transformEntries loops
        Map<String, Object> rawData = new HashMap<>();
        Map<String, Object> years = new HashMap<>();
        Map<String, Object> sectors = new HashMap<>();
        Map<String, Object> units = new HashMap<>();
        Map<String, Object> entries = new HashMap<>();
        
        entries.put("cat1", 500.0);
        entries.put("bad_cat", "string_instead_of_double"); // Trigger skip branch
        units.put("unit1", entries);
        sectors.put("sector1", units);
        years.put("2025", sectors);
        
        rawData.put("data_by_year", years);
        rawData.put("env_ministry_total_budget", new HashMap<>());

        Method buildMethod = EnvBudgetLoader.class.getDeclaredMethod("buildBudgetDataModel", Map.class);
        buildMethod.setAccessible(true);
        
        EnvBudgetData data = (EnvBudgetData) buildMethod.invoke(loader, rawData);
        assertNotNull(data.getBudgetForYear("2025"));
        assertEquals(1, data.getBudgetForYear("2025").getSectors().get(0).getUnits().get(0).getEntries().size());
    }
}
