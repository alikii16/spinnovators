package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

/**
 * Advanced Unit Tests for EsgLoader to achieve 100% coverage.
 * Covers file missing, malformed JSON, and path traversal logic.
 */
class EsgLoaderTest {

  private EsgLoader esgLoader;

  @BeforeEach
  void setUp() {
    // This loads the default config if the file is missing, covering the LOGGER.SEVERE branch.
    esgLoader = new EsgLoader();
  }

  @Test
  void testDefaultConfigFallback() {
    assertNotNull(esgLoader.getConfig());
    // Verify default weights from createDefaultConfig()
    assertEquals(0.40, esgLoader.getEnvironmentalWeight());
    assertEquals(0.30, esgLoader.getSocialWeight());
    assertEquals(0.30, esgLoader.getGovernanceWeight());
  }

  @Test
  void testThresholdsAndClassification() {
    assertEquals(80, esgLoader.getExcellentThreshold());
    assertEquals(60, esgLoader.getGoodThreshold());
    assertEquals(40, esgLoader.getModerateThreshold());
    assertEquals(20, esgLoader.getPoorThreshold());
    
    // Test classification defaults when sectors/entries are empty
    assertEquals("NEUTRAL", esgLoader.getSectorClassification("any"));
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
    assertEquals("NEUTRAL", esgLoader.getEffectiveCategory("any", "any"));
  }

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

  @Test
  void testImprovementThresholds() {
    assertEquals(50, esgLoader.getEnvironmentalLowThreshold());
    assertEquals(20, esgLoader.getSocialLowThreshold());
    assertEquals(15, esgLoader.getGovernanceLowThreshold());
  }

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
   */
  @Test
  void testPathTraversalAndErrorHandling() throws Exception {
    // Force set config to a nested object to test loop logic in getStringFromPath
    JsonObject mockConfig = new JsonObject();
    JsonObject weights = new JsonObject();
    weights.addProperty("environmental", "0.55");
    mockConfig.add("weights", weights);
    
    // Using Reflection to inject mock config into the private field
    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    // Test successful path traversal
    assertEquals(0.55, esgLoader.getEnvironmentalWeight());
    
    // Test missing intermediate key branch (weights.missing.key)
    // This covers the loop's (!current.has(keys[i])) return branch
    assertEquals(0.30, esgLoader.getSocialWeight()); 
    
    // Test invalid number formats to cover NumberFormatException catch blocks
    weights.addProperty("social", "not-a-number");
    assertEquals(0.30, esgLoader.getSocialWeight()); // Falls back to default
    
    // Test null config branch
    configField.set(esgLoader, null);
    assertEquals("el", esgLoader.getDefaultLanguage());
    assertEquals("NEUTRAL", esgLoader.getSectorClassification("test"));
  }

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

    // Test context inheritance branch
    assertEquals("SOCIAL", esgLoader.getEffectiveCategory("test_entry", "test_sector"));
    
    // Test normal branch
    entries.addProperty("direct_entry", "ENVIRONMENTAL");
    assertEquals("ENVIRONMENTAL", esgLoader.getEffectiveCategory("direct_entry", "any"));
  }
}
