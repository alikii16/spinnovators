package gr.det.spinnovators.service;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides translation services for budget categories and keys.
 *
 * <p>This class is responsible for converting English-based JSON keys into their
 * official Greek descriptions by reading from a properties resource file
 * (env_budget_translations.properties).</p>
 *
 * <p>It ensures that the user interface displays readable Greek terminology
 * instead of technical identifiers. For example, "personnel_costs" becomes
 * "Δαπάνες Προσωπικού".</p>
 *
 * <p>If the properties file is missing or a key is not found, the class provides
 * a fallback mechanism that formats the key into a readable string.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvBudgetTranslator {

  // The name of the properties file located in src/main/resources
  private static final String BUNDLE_NAME = "env_budget_translations";
  // ResourceBundle holds the key-translations
  private final ResourceBundle categoryBundle;

  /**
   * Constructs an EnvBudgetTranslator and initializes the resource bundle.
   *
   * <p>Attempts to load the 'env_budget_translations.properties' file from
   * the resources folder. If the file is missing, the bundle is set to null,
   * and the class provides a fallback display mechanism.</p>
   *
   * <p>The constructor uses silent failure handling to prevent application crashes
   * if the resource file is unavailable, allowing the frontend to continue operating
   * with fallback translations.</p>
   */
  public EnvBudgetTranslator() {
    // Pass the real bundle name
    this(loadBundleSafely(BUNDLE_NAME));
  }
  
  /**
   * Protected constructor for testing purposes.
   * Allows injecting a specific bundle or null to simulate failure scenarios.
   * * @param bundle The ResourceBundle to use, or null to simulate missing file.
   */
  protected EnvBudgetTranslator(ResourceBundle bundle) {
    this.categoryBundle = bundle;
  }

  /**
   * Helper method to load the bundle safely and handle exceptions.
   * Used by the default constructor.
   * * @param bundleName The name of the properties file to load.
   */
  static ResourceBundle loadBundleSafely(String bundleName) {
    try {
      return ResourceBundle.getBundle(bundleName);
    } catch (MissingResourceException e) {
      // Silent failure cause of frontend logic requirement
      return null;
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
