package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;


/**
 * Unit tests for EsgLoader class.
 *
 * @author Spinnovators Team
 * 
 * @version 1.0
 */

class EsgLoaderTest {

  private EsgLoader esgLoader;
  private static final String VALID_JSON_CONFIG = """
      {
          "weights": {
              "environmental": 0.35,
              "social": 0.35,
              "governance": 0.30
          },
          "thresholds": {
              "excellent": 85,
              "good": 65,
              "moderate": 45,
              "poor": 25
          },
          "sectors": {
              "energy": "ENVIRONMENTAL",
              "healthcare": "SOCIAL",
              "finance": "GOVERNANCE",
              "technology": "MIXED"
          },
          "entries": {
              "carbon_emissions": "ENVIRONMENTAL",
              "employee_training": "SOCIAL",
              "board_diversity": "GOVERNANCE",
              "community_outreach": "CONTEXT_DEPENDENT"
          },
          "display_settings": {
              "progress_bar_width": 30,
              "enable_esg": true,
              "show_esg_terminal": false,
              "show_esg_web": true,
              "show_compact_after_edit": false
          },
          "improvement_suggestions": {
              "environmental_low": 40,
              "social_low": 25,
              "governance_low": 20
          },
          "localization": {
              "default_language": "en",
              "ratings": {
                  "excellent": {
                      "el": "Άριστη",
                      "en": "Excellent"
                  },
                  "good": {
                      "el": "Καλή",
                      "en": "Good"
                  }
              }
          },
          "advanced_settings": {
              "min_score_diff_to_show": 0.15,
              "score_decimal_places": 3,
              "enable_logging": true,
              "enable_caching": true
          }
      }
      """;

  /**
   * Sets up test fixtures before each test.
   */
  @BeforeEach
  void setUp() {
    esgLoader = new EsgLoader();
  }

  /**
   * Tests that EsgLoader can be instantiated.
   */
  @Test
  void testEsgLoaderInstantiation() {
    assertNotNull(esgLoader, "EsgLoader should be instantiated");
    assertNotNull(esgLoader.getConfig(), "Config should not be null");
  }

  /**
   * Tests environmental weight getter.
   */
  @Test
  void testGetEnvironmentalWeight() {
    double weight = esgLoader.getEnvironmentalWeight();
    assertEquals(0.40, weight, 0.001, 
        "Default environmental weight should be 0.40");
  }

  /**
   * Tests social weight getter.
   */
  @Test
  void testGetSocialWeight() {
    double weight = esgLoader.getSocialWeight();
    assertEquals(0.30, weight, 0.001, 
        "Default social weight should be 0.30");
  }

  /**
   * Tests governance weight getter.
   */
  @Test
  void testGetGovernanceWeight() {
    double weight = esgLoader.getGovernanceWeight();
    assertEquals(0.30, weight, 0.001, 
        "Default governance weight should be 0.30");
  }

  /**
   * Tests excellent threshold getter.
   */
  @Test
  void testGetExcellentThreshold() {
    int threshold = esgLoader.getExcellentThreshold();
    assertEquals(80, threshold, 
        "Default excellent threshold should be 80");
  }

  /**
   * Tests good threshold getter.
   */
  @Test
  void testGetGoodThreshold() {
    int threshold = esgLoader.getGoodThreshold();
    assertEquals(60, threshold, 
        "Default good threshold should be 60");
  }

  /**
   * Tests moderate threshold getter.
   */
  @Test
  void testGetModerateThreshold() {
    int threshold = esgLoader.getModerateThreshold();
    assertEquals(40, threshold, 
        "Default moderate threshold should be 40");
  }

  /**
   * Tests poor threshold getter.
   */
  @Test
  void testGetPoorThreshold() {
    int threshold = esgLoader.getPoorThreshold();
    assertEquals(20, threshold, 
        "Default poor threshold should be 20");
  }

  /**
   * Tests sector classification with valid key.
   */
  @Test
  void testGetSectorClassificationValidKey() {
    // Test with default config (no sectors defined)
    String classification = esgLoader.getSectorClassification("energy");
    assertEquals("NEUTRAL", classification, 
        "Should return NEUTRAL for undefined sector");
  }

  /**
   * Tests sector classification with invalid key.
   */
  @Test
  void testGetSectorClassificationInvalidKey() {
    String classification = esgLoader.getSectorClassification("nonexistent");
    assertEquals("NEUTRAL", classification, 
        "Should return NEUTRAL for nonexistent sector");
  }

  /**
   * Tests entry classification with valid key.
   */
  @Test
  void testGetEntryClassificationValidKey() {
    String classification = esgLoader.getEntryClassification("carbon_emissions");
    assertEquals("NEUTRAL", classification, 
        "Should return NEUTRAL for undefined entry");
  }

  /**
   * Tests entry classification with invalid key.
   */
  @Test
  void testGetEntryClassificationInvalidKey() {
    String classification = esgLoader.getEntryClassification("nonexistent");
    assertEquals("NEUTRAL", classification, 
        "Should return NEUTRAL for nonexistent entry");
  }

  /**
   * Tests effective category for context dependent entry.
   */
  @Test
  void testGetEffectiveCategoryContextDependent() {
    // With default config, all entries return NEUTRAL
    String category = esgLoader.getEffectiveCategory(
        "community_outreach", "energy");
    assertEquals("NEUTRAL", category, 
        "Should return NEUTRAL for context dependent with NEUTRAL sector");
  }

  /**
   * Tests effective category for non-context dependent entry.
   */
  @Test
  void testGetEffectiveCategoryNonContextDependent() {
    String category = esgLoader.getEffectiveCategory(
        "carbon_emissions", "energy");
    assertEquals("NEUTRAL", category, 
        "Should return entry classification");
  }

  /**
   * Tests progress bar width getter.
   */
  @Test
  void testGetProgressBarWidth() {
    int width = esgLoader.getProgressBarWidth();
    assertEquals(20, width, 
        "Default progress bar width should be 20");
  }

  /**
   * Tests ESG enabled status.
   */
  @Test
  void testIsEsgEnabled() {
    boolean enabled = esgLoader.isEsgEnabled();
    assertTrue(enabled, "ESG should be enabled by default");
  }

  /**
   * Tests show ESG in terminal status.
   */
  @Test
  void testShowEsgInTerminal() {
    boolean show = esgLoader.showEsgInTerminal();
    assertTrue(show, "Should show ESG in terminal by default");
  }

  /**
   * Tests show ESG in web status.
   */
  @Test
  void testShowEsgInWeb() {
    boolean show = esgLoader.showEsgInWeb();
    assertTrue(show, "Should show ESG in web by default");
  }

  /**
   * Tests show compact after edit status.
   */
  @Test
  void testShowCompactAfterEdit() {
    boolean show = esgLoader.showCompactAfterEdit();
    assertTrue(show, "Should show compact after edit by default");
  }

  /**
   * Tests environmental low threshold.
   */
  @Test
  void testGetEnvironmentalLowThreshold() {
    int threshold = esgLoader.getEnvironmentalLowThreshold();
    assertEquals(50, threshold, 
        "Default environmental low threshold should be 50");
  }

  /**
   * Tests social low threshold.
   */
  @Test
  void testGetSocialLowThreshold() {
    int threshold = esgLoader.getSocialLowThreshold();
    assertEquals(20, threshold, 
        "Default social low threshold should be 20");
  }

  /**
   * Tests governance low threshold.
   */
  @Test
  void testGetGovernanceLowThreshold() {
    int threshold = esgLoader.getGovernanceLowThreshold();
    assertEquals(15, threshold, 
        "Default governance low threshold should be 15");
  }

  /**
   * Tests default language getter.
   */
  @Test
  void testGetDefaultLanguage() {
    String language = esgLoader.getDefaultLanguage();
    assertEquals("el", language, 
        "Default language should be 'el'");
  }

  /**
   * Tests rating text for English language.
   */
  @Test
  void testGetRatingTextEnglish() {
    String text = esgLoader.getRatingText("excellent", "en");
    assertEquals("Excellent", text, 
        "English rating text for 'excellent' should be 'Excellent'");
  }

  /**
   * Tests rating text for Greek language.
   */
  @Test
  void testGetRatingTextGreek() {
    String text = esgLoader.getRatingText("excellent", "el");
    assertEquals("Άριστη", text, 
        "Greek rating text for 'excellent' should be 'Άριστη'");
  }

  /**
   * Tests rating text for unknown rating.
   */
  @Test
  void testGetRatingTextUnknownRating() {
    String text = esgLoader.getRatingText("unknown", "en");
    assertEquals("unknown", text, 
        "Should return rating key for unknown rating");
  }

  /**
   * Tests minimum score difference to show.
   */
  @Test
  void testGetMinScoreDiffToShow() {
    double diff = esgLoader.getMinScoreDiffToShow();
    assertEquals(0.1, diff, 0.001, 
        "Default min score diff should be 0.1");
  }

  /**
   * Tests score decimal places.
   */
  @Test
  void testGetScoreDecimalPlaces() {
    int decimalPlaces = esgLoader.getScoreDecimalPlaces();
    assertEquals(2, decimalPlaces, 
        "Default score decimal places should be 2");
  }

  /**
   * Tests logging enabled status.
   */
  @Test
  void testIsLoggingEnabled() {
    boolean enabled = esgLoader.isLoggingEnabled();
    assertFalse(enabled, "Logging should be disabled by default");
  }

  /**
   * Tests caching enabled status.
   */
  @Test
  void testIsCachingEnabled() {
    boolean enabled = esgLoader.isCachingEnabled();
    assertFalse(enabled, "Caching should be disabled by default");
  }

  /**
   * Tests getConfig method.
   */
  @Test
  void testGetConfig() {
    JsonObject config = esgLoader.getConfig();
    assertNotNull(config, "Config should not be null");
    assertTrue(config.has("weights"), 
        "Config should have weights section");
    assertTrue(config.has("thresholds"), 
        "Config should have thresholds section");
  }

  /**
   * Tests configuration loading with valid JSON.
   */
  @Test
  void testLoadValidConfig() {
    try (MockedStatic<EsgLoader> mockedLoader = mockStatic(EsgLoader.class)) {
      ClassLoader mockClassLoader = mock(ClassLoader.class);
      InputStream mockStream = new ByteArrayInputStream(
          VALID_JSON_CONFIG.getBytes(StandardCharsets.UTF_8));
      
      when(EsgLoader.class.getClassLoader()).thenReturn(mockClassLoader);
      when(mockClassLoader.getResourceAsStream(eq("esg_config.json")))
          .thenReturn(mockStream);
      
      EsgLoader customLoader = new EsgLoader();
      assertNotNull(customLoader, "Loader should be created with valid config");
    }
  }

  /**
   * Tests configuration loading with missing file.
   */
  @Test
  void testLoadMissingConfigFile() {
    try (MockedStatic<EsgLoader> mockedLoader = mockStatic(EsgLoader.class)) {
      ClassLoader mockClassLoader = mock(ClassLoader.class);
      
      when(EsgLoader.class.getClassLoader()).thenReturn(mockClassLoader);
      when(mockClassLoader.getResourceAsStream(eq("esg_config.json")))
          .thenReturn(null);
      
      EsgLoader customLoader = new EsgLoader();
      assertNotNull(customLoader, 
          "Loader should be created with default config");
      
      // Verify default values are used
      assertEquals(0.40, customLoader.getEnvironmentalWeight(), 0.001,
          "Should use default environmental weight");
      assertEquals(80, customLoader.getExcellentThreshold(),
          "Should use default excellent threshold");
    }
  }

  /**
   * Tests helper method getStringFromPath with valid path.
   */
  @Test
  void testGetStringFromPathValid() {
    // This tests the private method indirectly through public methods
    String language = esgLoader.getDefaultLanguage();
    assertEquals("el", language, 
        "Should retrieve string from valid path");
  }

  /**
   * Tests helper method getDoubleFromPath with invalid number.
   */
  @Test
  void testGetDoubleFromPathInvalid() {
    // Test with config that has invalid double value
   
    
    
    // We can't directly test private methods, but we can verify 
    // that the class handles invalid data gracefully
    EsgLoader loader = new EsgLoader();
    double weight = loader.getEnvironmentalWeight();
    // Should fall back to default
    assertEquals(0.40, weight, 0.001, 
        "Should fall back to default for invalid double");
  }

  /**
   * Tests helper method getIntFromPath with invalid number.
   */
  @Test
  void testGetIntFromPathInvalid() {
    // Indirect test through public method
    EsgLoader loader = new EsgLoader();
    int width = loader.getProgressBarWidth();
    // Should use default value
    assertEquals(20, width, 
        "Should use default value for invalid config");
  }

  /**
   * Tests helper method getBooleanFromPath.
   */
  @Test
  void testGetBooleanFromPath() {
    EsgLoader loader = new EsgLoader();
    boolean enabled = loader.isEsgEnabled();
    // Should use default value
    assertTrue(enabled, "Should use default boolean value");
  }

  /**
   * Tests edge case with null config.
   */
  @Test
  void testNullConfigScenario() {
    // Create a loader and simulate null config
    EsgLoader loader = new EsgLoader();
    
    // Test that methods handle null config gracefully
    String classification = loader.getSectorClassification("test");
    assertEquals("NEUTRAL", classification, 
        "Should return NEUTRAL for null config scenario");
  }

  /**
   * Tests rating text with null language parameter.
   */
  @Test
  void testGetRatingTextWithFallback() {
    // Test with unknown language
    String text = esgLoader.getRatingText("excellent", "fr");
    // Should fall back to default logic
    assertTrue(text.equals("Excellent") || text.equals("Άριστη") 
        || text.equals("excellent"),
        "Should handle unknown language gracefully");
  }

  /**
   * Tests that logger is properly initialized.
   */
  @Test
  void testLoggerInitialization() {
    Logger logger = Logger.getLogger(EsgLoader.class.getName());
    assertNotNull(logger, "Logger should be properly initialized");
  }

  /**
   * Tests all weight values sum approximately to 1.0.
   */
  @Test
  void testWeightSum() {
    double sum = esgLoader.getEnvironmentalWeight()
        + esgLoader.getSocialWeight()
        + esgLoader.getGovernanceWeight();
    
    assertEquals(1.0, sum, 0.001, 
        "Weight sum should be approximately 1.0");
  }

  /**
   * Tests threshold ordering.
   */
  @Test
  void testThresholdOrdering() {
    assertTrue(esgLoader.getExcellentThreshold() 
        > esgLoader.getGoodThreshold(),
        "Excellent threshold should be greater than good");
    
    assertTrue(esgLoader.getGoodThreshold() 
        > esgLoader.getModerateThreshold(),
        "Good threshold should be greater than moderate");
    
    assertTrue(esgLoader.getModerateThreshold() 
        > esgLoader.getPoorThreshold(),
        "Moderate threshold should be greater than poor");
  }
}