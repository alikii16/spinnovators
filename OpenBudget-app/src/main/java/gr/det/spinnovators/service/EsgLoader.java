package gr.det.spinnovators.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads ESG configuration from JSON file.
 *
 * <p>Provides centralized access to ESG weights, thresholds, classifications,
 * and display settings defined in esg_config.json.
 *
 * @author Spinnovators Team
 * 
 * @version 2.0
 */
public class EsgLoader {

  private static final String CONFIG_FILE = "esg_config.json";
  private static final Logger LOGGER = Logger.getLogger(EsgLoader.class.getName());

  private JsonObject config;
  private final Gson gson;

  /**
   * Constructs an ESG config loader and loads the configuration file.
   */
  public EsgLoader() {
    this.gson = new Gson();
    this.config = loadConfigFile();
  }

  /**
   * Loads the ESG configuration JSON file.
   *
   * @return JsonObject containing configuration, or null on failure.
   */
  private JsonObject loadConfigFile() {
    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream(CONFIG_FILE);

    if (inputStream == null) {
      LOGGER.log(Level.SEVERE,
          "ESG config file not found: {0}", CONFIG_FILE);
      return createDefaultConfig();
    }

    try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
      return gson.fromJson(reader, JsonObject.class);
    } catch (JsonSyntaxException e) {
      LOGGER.log(Level.SEVERE,
          "Invalid JSON syntax in ESG config file", e);
      return createDefaultConfig();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE,
          "Error reading ESG config file", e);
      return createDefaultConfig();
    }
  }

  /**
   * Creates a default configuration object if file loading fails.
   *
   * @return JsonObject with default values.
   */
  private JsonObject createDefaultConfig() {
    String defaultJson = """
        {
            "weights": {"environmental": 0.40, "social": 0.30, "governance": 0.30},
            "thresholds": {"excellent": 80, "good": 60, "moderate": 40, "poor": 20},
            "sectors": {},
            "entries": {},
            "display_settings": {"enable_esg": true},
            "localization": {"default_language": "el"}
        }
        """;
    return gson.fromJson(defaultJson, JsonObject.class);
  }

  // ==================== WEIGHTS ====================

  /**
   * Gets the weight for Environmental score.
   *
   * @return Weight value (default: 0.40).
   */
  public double getEnvironmentalWeight() {
    return getDoubleFromPath("weights.environmental", 0.40);
  }

  /**
   * Gets the weight for Social score.
   *
   * @return Weight value (default: 0.30).
   */
  public double getSocialWeight() {
    return getDoubleFromPath("weights.social", 0.30);
  }

  /**
   * Gets the weight for Governance score.
   *
   * @return Weight value (default: 0.30).
   */
  public double getGovernanceWeight() {
    return getDoubleFromPath("weights.governance", 0.30);
  }

  // ==================== THRESHOLDS ====================

  /**
   * Gets the threshold for "Excellent" rating.
   *
   * @return Threshold value (default: 80).
   */
  public int getExcellentThreshold() {
    return getIntFromPath("thresholds.excellent", 80);
  }

  /**
   * Gets the threshold for "Good" rating.
   *
   * @return Threshold value (default: 60).
   */
  public int getGoodThreshold() {
    return getIntFromPath("thresholds.good", 60);
  }

  /**
   * Gets the threshold for "Moderate" rating.
   *
   * @return Threshold value (default: 40).
   */
  public int getModerateThreshold() {
    return getIntFromPath("thresholds.moderate", 40);
  }

  /**
   * Gets the threshold for "Poor" rating.
   *
   * @return Threshold value (default: 20)
   */
  public int getPoorThreshold() {
    return getIntFromPath("thresholds.poor", 20);
  }

  // ==================== CLASSIFICATIONS ====================

  /**
   * Gets the ESG classification for a sector.
   *
   * @param sectorKey The JSON key of the sector.
   *
   * @return Classification string (ENVIRONMENTAL, SOCIAL, GOVERNANCE, MIXED, NEUTRAL).
   */
  public String getSectorClassification(String sectorKey) {
    if (config == null || !config.has("sectors")) {
      return "NEUTRAL";
    }

    JsonObject sectors = config.getAsJsonObject("sectors");
    if (sectors.has(sectorKey)) {
      return sectors.get(sectorKey).getAsString();
    }

    return "NEUTRAL";
  }

  /**
   * Gets the ESG classification for an entry type.
   *
   * @param entryKey The JSON key of the entry.
   * @return Classification string (ENVIRONMENTAL, SOCIAL, GOVERNANCE,
   *  CONTEXT_DEPENDENT, NEUTRAL).
   */
  public String getEntryClassification(String entryKey) {
    if (config == null || !config.has("entries")) {
      return "NEUTRAL";
    }

    JsonObject entries = config.getAsJsonObject("entries");
    if (entries.has(entryKey)) {
      return entries.get(entryKey).getAsString();
    }

    return "NEUTRAL";
  }

  /**
   * Determines the effective ESG category for an entry.
   * If entry is CONTEXT_DEPENDENT, inherits from sector.
   *
   * @param entryKey The entry JSON key.
   * 
   * @param sectorKey The parent sector JSON key.
   *
   * @return The effective ESG category.
   */
  public String getEffectiveCategory(String entryKey, String sectorKey) {
    String entryClass = getEntryClassification(entryKey);

    if ("CONTEXT_DEPENDENT".equals(entryClass)) {
      return getSectorClassification(sectorKey);
    }

    return entryClass;
  }

  // ==================== DISPLAY SETTINGS ====================

  /**
   * Gets the progress bar width for terminal display.
   *
   * @return Width in characters (default: 20)
   */
  public int getProgressBarWidth() {
    return getIntFromPath("display_settings.progress_bar_width", 20);
  }

  /**
   * Checks if ESG reporting is enabled.
   *
   * @return true if enabled (default: true)
   */
  public boolean isEsgEnabled() {
    return getBooleanFromPath("display_settings.enable_esg", true);
  }

  /**
   * Checks if ESG should be shown in terminal.
   *
   * @return true if enabled (default: true)
   */
  public boolean showEsgInTerminal() {
    return getBooleanFromPath("display_settings.show_esg_terminal", true);
  }

  /**
   * Checks if ESG should be shown in web interface.
   *
   * @return true if enabled (default: true)
   */
  public boolean showEsgInWeb() {
    return getBooleanFromPath("display_settings.show_esg_web", true);
  }

  /**
   * Checks if compact ESG summary should be shown after edits.
   *
   * @return true if enabled (default: true)
   */
  public boolean showCompactAfterEdit() {
    return getBooleanFromPath("display_settings.show_compact_after_edit", true);
  }

  // ==================== IMPROVEMENT SUGGESTIONS ====================

  /**
   * Gets threshold for low environmental score warning.
   *
   * @return Threshold value (default: 50)
   */
  public int getEnvironmentalLowThreshold() {
    return getIntFromPath("improvement_suggestions.environmental_low", 50);
  }

  /**
   * Gets threshold for low social score warning.
   *
   * @return Threshold value (default: 20)
   */
  public int getSocialLowThreshold() {
    return getIntFromPath("improvement_suggestions.social_low", 20);
  }

  /**
   * Gets threshold for low governance score warning.
   *
   * @return Threshold value (default: 15)
   */
  public int getGovernanceLowThreshold() {
    return getIntFromPath("improvement_suggestions.governance_low", 15);
  }

  // ==================== LOCALIZATION ====================

  /**
   * Gets the default language code.
   *
   * @return Language code (default: "el")
   */
  public String getDefaultLanguage() {
    return getStringFromPath("localization.default_language", "el");
  }

  /**
   * Gets localized rating text.
   *
   * @param rating The rating level (excellent, good, moderate, poor, critical)
   * 
   * @param language The language code ("el" or "en")
   *
   * @return Localized rating text
   */
  public String getRatingText(String rating, String language) {
    String path = "localization.ratings." + rating + "." + language;

    // Fallback defaults
    String defaultText = switch (rating.toLowerCase()) {
      case "excellent" -> language.equals("el") ? "Άριστη" : "Excellent";
      case "good" -> language.equals("el") ? "Καλή" : "Good";
      case "moderate" -> language.equals("el") ? "Μέτρια" : "Moderate";
      case "poor" -> language.equals("el") ? "Χαμηλή" : "Poor";
      case "critical" -> language.equals("el") ? "Πολύ Χαμηλή" : "Critical";
      default -> rating;
    };

    return getStringFromPath(path, defaultText);
  }

  // ==================== ADVANCED SETTINGS ====================

  /**
   * Gets minimum score difference to show comparison.
   *
   * @return Threshold value (default: 0.1)
   */
  public double getMinScoreDiffToShow() {
    return getDoubleFromPath("advanced_settings.min_score_diff_to_show", 0.1);
  }

  /**
   * Gets decimal places for score display.
   *
   * @return Number of decimal places (default: 2)
   */
  public int getScoreDecimalPlaces() {
    return getIntFromPath("advanced_settings.score_decimal_places", 2);
  }

  /**
   * Checks if detailed logging is enabled.
   *
   * @return true if enabled (default: false)
   */
  public boolean isLoggingEnabled() {
    return getBooleanFromPath("advanced_settings.enable_logging", false);
  }

  /**
   * Checks if ESG calculation caching is enabled.
   *
   * @return true if enabled (default: false)
   */
  public boolean isCachingEnabled() {
    return getBooleanFromPath("advanced_settings.enable_caching", false);
  }

  // ==================== HELPER METHODS ====================

  /**
   * Gets a string value from a dot-notation path in JSON.
   *
   * @param path Dot-separated path (e.g., "weights.environmental")
   * 
   * @param defaultValue Default value if path not found
   *
   * @return The string value or default
   */
  private String getStringFromPath(String path, String defaultValue) {
    if (config == null) {
      return defaultValue;
    }

    try {
      String[] keys = path.split("\\.");
      JsonObject current = config;

      for (int i = 0; i < keys.length - 1; i++) {
        if (!current.has(keys[i])) {
          return defaultValue;
        }
        current = current.getAsJsonObject(keys[i]);
      }

      String lastKey = keys[keys.length - 1];
      if (!current.has(lastKey)) {
        return defaultValue;
      }

      return current.get(lastKey).getAsString();
    } catch (Exception e) {
      LOGGER.log(Level.WARNING,
          "Error reading path: " + path + ", using default", e);
      return defaultValue;
    }
  }

  /**
   * Gets a double value from a dot-notation path in JSON.
   */
  private double getDoubleFromPath(String path, double defaultValue) {
    String value = getStringFromPath(path, String.valueOf(defaultValue));
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      LOGGER.log(Level.WARNING,
          "Invalid double value for path: " + path + ", using default", e);
      return defaultValue;
    }
  }

  /**
   * Gets an integer value from a dot-notation path in JSON.
   */
  private int getIntFromPath(String path, int defaultValue) {
    String value = getStringFromPath(path, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      LOGGER.log(Level.WARNING,
          "Invalid integer value for path: " + path + ", using default", e);
      return defaultValue;
    }
  }

  /**
   * Gets a boolean value from a dot-notation path in JSON.
   */
  private boolean getBooleanFromPath(String path, boolean defaultValue) {
    String value = getStringFromPath(path, String.valueOf(defaultValue));
    return Boolean.parseBoolean(value);
  }

  /**
   * Gets the entire configuration object.
   * 
   * <p>Useful for advanced operations.
   *
   * @return The JsonObject configuration.
   * 
   */
  public JsonObject getConfig() {
    return config;
  }
}
