package gr.det.spinnovators.service;

import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link EnvBudgetTranslator} class.
 *
 * <p>This test suite verifies the translation logic for budget category keys,
 * ensuring proper localization when a resource bundle is present and correct
 * fallback behavior (underscore to space replacement) when it is not.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvBudgetTranslatorTest {

  /**
   * Tests translation for a key that exists in the resource bundle.
   * Verifies that the returned string is the actual translation and not the key.
   */
  @Test
  public void testTranslateCategory_existingKey() {
    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    // Assuming the properties file contains specific keys
    String result = translator.translateCategory("personnel_costs");
    assertNotNull(result);
    // We verify it returns something other than the key itself (meaning translation happened)
    assertNotEquals("personnel_costs", result, "Should translate the key");
  }

  /**
   * Tests fallback behavior for a key that is missing from the bundle.
   * Verifies that the translator returns the key with underscores replaced by spaces.
   */
  @Test
  public void testTranslateCategory_missingKey_returnsKeyWithSpaces() {
    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    String result = translator.translateCategory("non_existing_key");
    assertEquals("non existing key", result);
  }

  /**
   * Tests the handling of null or empty input keys.
   * Verifies that the method returns an empty string instead of null or error.
   */
  @Test
  public void testTranslateCategory_nullOrEmpty_returnsEmptyString() {
    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    assertEquals("", translator.translateCategory(null));
    assertEquals("", translator.translateCategory(""));
    assertEquals("", translator.translateCategory("   "));
  }

  /**
   * Tests the translation logic when the ResourceBundle is null.
   * Simulates a missing properties file using the protected constructor to ensure
   * the fallback mechanism works correctly.
   */
  @Test
  public void testTranslateCategory_withNullBundle() {
    // Coverage Trick: Use the protected constructor to inject NULL.
    // This simulates the case where the properties file is missing.
    EnvBudgetTranslator translator = new EnvBudgetTranslator((ResourceBundle) null);

    String result = translator.translateCategory("some_technical_key");

    // It should fallback to replacing underscores with spaces
    assertEquals("some technical key", result, "Should handle null bundle gracefully");
  }

  /**
   * Tests the safe loading mechanism for resource bundles.
   * Verifies that if a non-existent bundle name is provided, the method
   * catches the exception and returns null.
   */
  @Test
  public void testLoadBundleSafely_Failure() {
    // Coverage Trick: Call the static loader with a FAKE name.
    // This forces the MissingResourceException to be thrown and caught.
    ResourceBundle result = EnvBudgetTranslator.loadBundleSafely("this_file_does_not_exist_12345");

    assertNull(result, "Should return null when the bundle file is missing");
  }
}