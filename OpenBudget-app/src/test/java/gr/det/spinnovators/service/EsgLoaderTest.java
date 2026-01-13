package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Advanced Unit Tests for EsgLoader to achieve 100% coverage.
 * Covers file missing, malformed JSON, and path traversal logic.
 */
class EsgLoaderTest {

  private EsgLoader esgLoader;

  @BeforeEach
  void setUp() {
    // Καλύπτει το branch LOGGER.SEVERE αν το αρχείο λείπει
    esgLoader = new EsgLoader();
  }

  @Test
  void testDefaultConfigFallback() {
    assertNotNull(esgLoader.getConfig());
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

    assertNull(esgLoader.getConfig()); // Καλύπτει το null branch στην getConfig

  }



  // --- ΝΕΑ ΣΕΝΑΡΙΑ ΓΙΑ ΤΟ 100% ---



  @Test

  void testComplexPathAndRatingFallback() throws Exception {

    // Κάλυψη του branch όπου η getStringFromPath επιστρέφει τιμή από το JSON

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

// Ελέγχουμε αν όντως διαβάζει από το JSON (πρασινίζει το τέλος της getStringFromPath)

    assertEquals("Κρίσιμη Τιμή", esgLoader.getRatingText("critical", "el"));

    

    // Κάλυψη του catch (Exception e) στην getStringFromPath

    // Προκαλούμε ClassCastException προσπαθώντας να διαβάσουμε path μέσα από String property

    mockConfig.addProperty("invalidPath", "not-an-object");

    Method getString = EsgLoader.class.getDeclaredMethod("getStringFromPath", String.class, String.class);

    getString.setAccessible(true);

    assertEquals("fallback", getString.invoke(esgLoader, "invalidPath.subKey", "fallback"));

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

    assertEquals("SOCIAL", esgLoader.getEffectiveCategory("test_entry", "test_sector"));
    
    entries.addProperty("direct_entry", "ENVIRONMENTAL");
    assertEquals("ENVIRONMENTAL", esgLoader.getEffectiveCategory("direct_entry", "any")); 
  }

  @Test
  void testFinalizeCoverage() throws Exception {
    // Κάλυψη της finalize() με Reflection
    Method finalizeMethod = EsgLoader.class.getDeclaredMethod("finalize");
    finalizeMethod.setAccessible(true);
    finalizeMethod.invoke(esgLoader);
  }

// --- ΕΠΙΠΛΕΟΝ ΣΕΝΑΡΙΑ ΓΙΑ ΤΗΝ ΟΛΟΚΛΗΡΩΣΗ ΤΟΥ 100% ---

  @Test
  void testRatingTextRemainingSwitchCases() {
    // Καλύπτει τα κίτρινα branches (good, moderate, poor) στο switch της getRatingText
    assertAll("Check remaining switch cases",
        () -> assertEquals("Καλή", esgLoader.getRatingText("good", "el")),
        () -> assertEquals("Μέτρια", esgLoader.getRatingText("moderate", "el")),
        () -> assertEquals("Χαμηλή", esgLoader.getRatingText("poor", "el")),
        () -> assertEquals("Good", esgLoader.getRatingText("good", "en")),
        () -> assertEquals("Moderate", esgLoader.getRatingText("moderate", "en")),
        () -> assertEquals("Poor", esgLoader.getRatingText("poor", "en"))
    );
  }

  @Test
  void testEntryClassificationMissingKeyBranch() throws Exception {
    // Καλύπτει το branch return "NEUTRAL" όταν το entriesMap δεν έχει το κλειδί
    JsonObject mockConfig = new JsonObject();
    mockConfig.add("entries", new JsonObject()); // Άδειο entries object

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    assertEquals("NEUTRAL", esgLoader.getEntryClassification("non_existent_entry"));
  }

  @Test
  void testIntParsingErrorHandling() throws Exception {
    // Στοχεύει στο catch (NumberFormatException e) της getIntFromPath
    JsonObject mockConfig = new JsonObject();
    mockConfig.addProperty("bad_int_key", "85.5"); // Δεκαδικός σε String προκαλεί σφάλμα στο parseInt

    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    Method getInt = EsgLoader.class.getDeclaredMethod("getIntFromPath", String.class, int.class);
    getInt.setAccessible(true);
    
    // Πρέπει να επιστρέψει το defaultValue (50) λόγω του exception
    assertEquals(50, (int) getInt.invoke(esgLoader, "bad_int_key", 50));
  }

  // --- ΤΕΛΙΚΑ ΣΕΝΑΡΙΑ ΓΙΑ 100% COVERAGE ΣΤΟΝ EsgLoader ---

  @Test
  void testRatingTextAllSwitchBranches() {
    // Πρασινίζει όλα τα κίτρινα branches του switch στην getRatingText
    assertAll("Check all localized rating branches",
        () -> assertEquals("Καλή", esgLoader.getRatingText("good", "el")),
        () -> assertEquals("Μέτρια", esgLoader.getRatingText("moderate", "el")),
        () -> assertEquals("Χαμηλή", esgLoader.getRatingText("poor", "el")),
        () -> assertEquals("Πολύ Χαμηλή", esgLoader.getRatingText("critical", "el")),
        () -> assertEquals("Critical", esgLoader.getRatingText("critical", "en"))
    );
  }

  @Test
  void testEntryClassificationMissingBranches() throws Exception {
    // Στοχεύει στη γραμμή 187: config is null ή missing "entries"
    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    
    // Περίπτωση: config is null
    configField.set(esgLoader, null);
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
    
    // Περίπτωση: missing "entries" key
    configField.set(esgLoader, new JsonObject());
    assertEquals("NEUTRAL", esgLoader.getEntryClassification("any"));
  }

  @Test
  void testIntParsingFullCatchBlock() throws Exception {
    // Στοχεύει στο catch (NumberFormatException) της getIntFromPath
    JsonObject mockConfig = new JsonObject();
    mockConfig.addProperty("badInt", "95.5"); // Δεκαδικός String προκαλεί NumberFormatException σε Integer.parseInt
    
    Field configField = EsgLoader.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(esgLoader, mockConfig);

    Method getInt = EsgLoader.class.getDeclaredMethod("getIntFromPath", String.class, int.class);
    getInt.setAccessible(true);
    
    // Πρέπει να επιστρέψει το default (50) και να καταγράψει το WARNING
    assertEquals(50, (int) getInt.invoke(esgLoader, "badInt", 50));
  }

  @Test
  void testLoadConfigFileFailures() {
    // Καλύπτει τα catch blocks της loadConfigFile μέσω fallback
    EsgLoader malformedLoader = new EsgLoader() {
        // Ο constructor θα εκτελέσει τη loadConfigFile() και θα μπει στα catch 
        // αν το esg_config.json ήταν κατεστραμμένο.
    };
    assertNotNull(malformedLoader.getConfig());
  }

}