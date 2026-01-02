package gr.det.spinnovators.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetTranslatorTest {

    @Test
    public void testTranslateCategory_existingKey() {
        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        // Αν το αρχείο properties έχει το key "personnel_costs" → "Αμοιβές Προσωπικού"
        String result = translator.translateCategory("personnel_costs");
        assertNotNull(result);
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
}
