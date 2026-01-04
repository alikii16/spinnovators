package gr.det.spinnovators.service;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides translation services for budget categories and keys.
 * 
 * <p>This class is responsible for converting English-based JSON keys into their 
 * official Greek descriptions by reading from a properties resource file. 
 *
 * <p>It ensures that the user interface displays readable Greek terminology 
 * instead of technical identifiers.</p>
 */
public class EnvBudgetTranslator {

  // The name of the properties file located in src/main/resources
  private static final String BUNDLE_NAME = "env_budget_translations";
  // ResourceBundle holds the key-translations
  private ResourceBundle categoryBundle;

  /**
   * Constructs an EnvBudgetTranslator and initializes the resource bundle.
   * * <p>Attempts to load the 'env_budget_translations.properties' file from 
   * the resources folder. If the file is missing, the bundle is set to null, 
   * and the class provides a fallback display mechanism.</p>
   */
  public EnvBudgetTranslator() {
    try {
      this.categoryBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    } catch (MissingResourceException e) {
      // Silent failure cause of frontend
      this.categoryBundle = null;
    }
  }

  /**
   * Translates a technical JSON key into its Greek equivalent.
   * * <p>Searches the localized resource bundle for the provided key. If the key 
   * is null, empty, or not found in the bundle, it returns the key itself 
   * with underscores replaced by spaces as a fallback.</p>
   *
   * @param jsonKey The technical identifier string to be translated.
   * @return The translated Greek description or a formatted fallback string.
   */
  public String translateCategory(String jsonKey) {
    // Input Validation
    if (jsonKey == null || jsonKey.trim().isEmpty()) {
      return "";
    }
    // Check if the translation bundle was loaded successfully
    if (categoryBundle != null) {
      try {
        // Attempt to find and return the translation
        return categoryBundle.getString(jsonKey);
      } catch (MissingResourceException e) {
        // Key not found in properties file, fallback below
      }
    }
    // For Safety: Returns the key by replacing underscores with spaces.
    return jsonKey.replace('_', ' ');
  }
}
