package gr.det.spinnovators.service;

import org.junit.jupiter.api.Test;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetTranslatorTest {

    @Test
    public void testTranslateCategory_existingKey() {
        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        // Assuming the properties file contains specific keys
        String result = translator.translateCategory("personnel_costs");
        assertNotNull(result);
        // We verify it returns something other than the key itself (meaning translation happened)
        assertNotEquals("personnel_costs", result, "Should translate the key");
    }

    @Test
    public void testTranslateCategory_missingKey_returnsKeyWithSpaces() {
        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        String result = translator.translateCategory("non_existing_key");
        assertEquals("non existing key", result);
    }

    @Test
    public void testTranslateCategory_nullOrEmpty_returnsEmptyString() {
        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        assertEquals("", translator.translateCategory(null));
        assertEquals("", translator.translateCategory(""));
        assertEquals("", translator.translateCategory("   "));
    }

    @Test
    public void testTranslateCategory_withNullBundle() {
        // Coverage Trick: Use the protected constructor to inject NULL.
        // This simulates the case where the properties file is missing.
        EnvBudgetTranslator translator = new EnvBudgetTranslator((ResourceBundle) null);
        
        String result = translator.translateCategory("some_technical_key");
        
        // It should fallback to replacing underscores with spaces
        assertEquals("some technical key", result, "Should handle null bundle gracefully");
    }
    @Test
    public void testLoadBundleSafely_Failure() {
        // Coverage Trick: Call the static loader with a FAKE name.
        // This forces the MissingResourceException to be thrown and caught.
        ResourceBundle result = EnvBudgetTranslator.loadBundleSafely("this_file_does_not_exist_12345");
        
        assertNull(result, "Should return null when the bundle file is missing");
    }
}
