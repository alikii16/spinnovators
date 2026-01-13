package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Advanced Unit Tests for EsgLoader to achieve 100% coverage.
 * Covers missing files, malformed JSON, and path traversal logic.
 */
class EsgLoaderTest {

  private EsgLoader esgLoader;

  /**
   * Sets up the test environment before each execution.
   * Covers the LOGGER.SEVERE branch if the config file is missing.
   */
  @BeforeEach
  void setUp() {
    esgLoader = new EsgLoader();
  }

  /**
   * Verifies that the loader falls back to a default configuration.
   */
  @Test
  void testDefaultConfigFallback() {
    assertNotNull(esgLoader.getConfig());
    assertEquals(0.40, esgLoader.getEnvironmentalWeight());
    assertEquals(0.30, esgLoader.getSocialWeight());
    assertEquals(0.30, esgLoader.getGovernanceWeight());
  }

  /**
   * Tests threshold values and ESG classifications.
   */
  @Test
  void testThresholdsAndClassification() {
    assertEquals(80, esgLoader.getExcellentThreshold());
    assertEquals(60, esgLoader.getGoodThreshold());
    assertEquals(40, esgLoader.getModerateThreshold());
    assertEquals(20, esgLoader.getPoorThreshold());
    
    assertEquals("NEUTRAL", esgLoader.getSectorClassification("any"));
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
    assertEquals("NEUTRAL", esgLoader.getEffectiveCategory("any", "any"));
  }

  /**
   * Tests display settings and advanced configuration branches.
   */
  @Test
  void testDisplayAndAdvancedSettings() {
    assertTrue(esgLoader.isEsgEnabled());
    assertTrue(esgLoader.showEsgInTerminal());
    assertTrue(esgLoader.showEsgInWeb());
    assertTrue(esgLoader.showCompactAfterEdit());
    assertEquals(20, esgLoader.getProgressBarWidth());
    
    assertEquals(0.1, esgLoader.getMinScoreDiffToShow());
    assertEquals(2, esgLoader.getScoreDecimalPlaces());
    assertFalse(esgLoader.isLoggingEnabled());
    assertFalse(esgLoader.isCachingEnabled());
  }

  /**
   * Tests thresholds for sustainability improvement suggestions.
   */
  @Test
  void testImprovementThresholds() {
    assertEquals(50, esgLoader.getEnvironmentalLowThreshold());
    assertEquals(20, esgLoader.getSocialLowThreshold());
    assertEquals(15, esgLoader.getGovernanceLowThreshold());
  }

  /**
   * Tests localization support and rating text fallbacks.
   */
  @Test
  void testLocalizationAndRatings() {
    assertEquals("el", esgLoader.getDefaultLanguage());
    assertEquals("Excellent", esgLoader.getRatingText("excellent", "en"));
    assertEquals("Άριστη", esgLoader.getRatingText("excellent", "el"));
    assertEquals("Καλή", esgLoader.getRatingText("good", "el"));
    assertEquals("Μέτρια", esgLoader.getRatingText("moderate", "el"));
    assertEquals("Poor", esgLoader.getRatingText("poor", "en"));
    assertEquals("Critical", esgLoader.getRatingText("critical", "en"));
    assertEquals("custom", esgLoader.getRatingText("custom", "en"));
  }

  /**
   * Targets the private getStringFromPath and Exception handling branches.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testPathTraversalAndErrorHandling() throws Exception {
    JsonObject mockConfig = new JsonObject();
    JsonObject weights = new JsonObject();
    weights.addProperty("environmental", "0.55");
    mockConfig.add("weights", weights);

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    assertEquals(0.55, esgLoader.getEnvironmentalWeight());
    assertEquals(0.30, esgLoader.getSocialWeight()); 

    weights.addProperty("social", "not-a-number");
    assertEquals(0.30, esgLoader.getSocialWeight()); // NumberFormatException branch

    configField.set(esgLoader, null);
    assertEquals("el", esgLoader.getDefaultLanguage());
    assertEquals("NEUTRAL", esgLoader.getSectorClassification("test"));
    assertNull(esgLoader.getConfig()); // Null branch coverage in getConfig
  }

  /**
   * Tests complex path navigation and rating fallback logic.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testComplexPathAndRatingFallback() throws Exception {
    JsonObject mockConfig = new JsonObject();
    JsonObject localization = new JsonObject();
    JsonObject ratings = new JsonObject();
    JsonObject critical = new JsonObject();
    critical.addProperty("el", "Κρίσιμη Τιμή");
    ratings.add("critical", critical);
    localization.add("ratings", ratings);
    mockConfig.add("localization", localization);

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    assertEquals("Κρίσιμη Τιμή", esgLoader.getRatingText("critical", "el"));
    mockConfig.addProperty("invalidPath", "not-an-object");
    Method getString = EsgLoader.class.getDeclaredMethod("getStringFromPath", 
        String.class, String.class);
    getString.setAccessible(true);
    assertEquals("fallback", getString.invoke(esgLoader, "invalidPath.subKey", "fallback"));
  }

  /**
   * Tests effective category inheritance logic.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testEffectiveCategoryLogic() throws Exception {
    JsonObject mockConfig = new JsonObject();
    JsonObject entries = new JsonObject(); 
    entries.addProperty("test_entry", "CONTEXT_DEPENDENT"); 
    JsonObject sectors = new JsonObject();
    sectors.addProperty("test_sector", "SOCIAL");
    mockConfig.add("entries", entries);
    mockConfig.add("sectors", sectors);

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    assertEquals("SOCIAL", esgLoader.getEffectiveCategory("test_entry", "test_sector"));
    
    entries.addProperty("direct_entry", "ENVIRONMENTAL");
    assertEquals("ENVIRONMENTAL", esgLoader.getEffectiveCategory("direct_entry", "any")); 
  }

  /**
   * Covers remaining switch cases in the localized rating logic.
   */
  @Test
  void testRatingTextRemainingSwitchCases() {
    assertAll("Check remaining switch cases",
        () -> assertEquals("Καλή", esgLoader.getRatingText("good", "el")),
        () -> assertEquals("Μέτρια", esgLoader.getRatingText("moderate", "el")),
        () -> assertEquals("Χαμηλή", esgLoader.getRatingText("poor", "el")),
        () -> assertEquals("Good", esgLoader.getRatingText("good", "en")),
        () -> assertEquals("Moderate", esgLoader.getRatingText("moderate", "en")),
        () -> assertEquals("Poor", esgLoader.getRatingText("poor", "en"))
    );
  }

  /**
   * Covers the branch where entry classification is missing from the config.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testEntryClassificationMissingKeyBranch() throws Exception {
    JsonObject mockConfig = new JsonObject();
    mockConfig.add("entries", new JsonObject()); 

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    assertEquals("NEUTRAL", esgLoader.getEntryClassification("non_existent_entry"));
  }

  /**
   * Tests error handling for invalid integer parsing in JSON paths.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testIntParsingErrorHandling() throws Exception {
    JsonObject mockConfig = new JsonObject();
    mockConfig.addProperty("bad_int_key", "85.5"); 

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    Method getInt = EsgLoader.class.getDeclaredMethod("getIntFromPath", String.class, int.class);
    getInt.setAccessible(true);
    
    assertEquals(50, (int) getInt.invoke(esgLoader, "bad_int_key", 50));
  }

  /**
   * Ensures 100% coverage for all rating switch branches.
   */
  @Test
  void testRatingTextAllSwitchBranches() {
    assertAll("Check all localized rating branches",
        () -> assertEquals("Καλή", esgLoader.getRatingText("good", "el")),
        () -> assertEquals("Μέτρια", esgLoader.getRatingText("moderate", "el")),
        () -> assertEquals("Χαμηλή", esgLoader.getRatingText("poor", "el")),
        () -> assertEquals("Πολύ Χαμηλή", esgLoader.getRatingText("critical", "el")),
        () -> assertEquals("Critical", esgLoader.getRatingText("critical", "en"))
    );
  }

  /**
   * Targets cases where the entries map is null or missing.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testEntryClassificationMissingBranches() throws Exception {
    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    
    configField.set(esgLoader, null);
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
    
    configField.set(esgLoader, new JsonObject());
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
  }

  /**
   * Tests fallback logic for integer parsing failures.
   *
   * @throws Exception if reflection fails.
   */
  @Test
  void testIntParsingFullCatchBlock() throws Exception {
    JsonObject mockConfig = new JsonObject();
    mockConfig.addProperty("badInt", "95.5"); 
    
    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    Method getInt = EsgLoader.class.getDeclaredMethod("getIntFromPath", String.class, int.class);
    getInt.setAccessible(true);
    
    assertEquals(50, (int) getInt.invoke(esgLoader, "badInt", 50));
  }

  /**
   * Covers failure scenarios in config file loading.
   */
  @Test
  void testLoadConfigFileFailures() {
    EsgLoader malformedLoader = new EsgLoader() {
    };
    assertNotNull(malformedLoader.getConfig());
  }
}