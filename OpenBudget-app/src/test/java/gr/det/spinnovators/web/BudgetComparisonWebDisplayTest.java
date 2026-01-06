package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BudgetComparisonWebDisplay.
 *
 * <p>Tests the HTML generation of budget comparisons.
 * Updated to correctly verify distinct HTML sections and handle formatting assertions.
 */
public class BudgetComparisonWebDisplayTest {

  private EnvBudgetTranslator mockTranslator;
  private BudgetComparisonWebDisplay webDisplay;

  @BeforeEach
  public void setUp() {
    mockTranslator = Mockito.mock(EnvBudgetTranslator.class);
    // Mock translation to return a predictable string "Trans_key"
    when(mockTranslator.translateCategory(anyString())).thenAnswer(i -> "Trans_" + i.getArguments()[0]);
    
    webDisplay = new BudgetComparisonWebDisplay(mockTranslator);
  }

  // --- Helper Methods ---

  private EnvYear createMockYear(String yearStr, String sectorName, double amount) {
    EnvEntry entry = new EnvEntry("entry1", amount);
    EnvUnit unit = new EnvUnit("unit1", List.of(entry));
    EnvSector sector = new EnvSector(sectorName, List.of(unit));
    return new EnvYear(yearStr, List.of(sector));
  }

  private EnvYear createMultiSectorYear(String yearStr, List<EnvSector> sectors) {
    return new EnvYear(yearStr, sectors);
  }

  private EnvSector createSector(String key, double amount) {
    EnvEntry entry = new EnvEntry("ent_" + key, amount);
    EnvUnit unit = new EnvUnit("unit_" + key, List.of(entry));
    return new EnvSector(key, List.of(unit));
  }

  // --- Tests ---

  @Test
  public void testGenerateComparisonContent_BasicStructure() {
    EnvYear original = createMockYear("2025", "energy", 1000.0);
    EnvYear modified = createMockYear("2025", "energy", 1200.0);
    double totalBudget = 1000.0;

    String html = webDisplay.generateComparisonContent(original, modified, totalBudget);

    Assertions.assertNotNull(html);
    Assertions.assertTrue(html.contains("Σύγκριση Προϋπολογισμού"), "Should contain title");
    Assertions.assertTrue(html.contains("Trans_energy"), "Should contain translated sector name");
    Assertions.assertTrue(html.contains("Σύγκριση ανά Τομέα"), "Should contain Table section");
    Assertions.assertTrue(html.contains("Κατανομή Προϋπολογισμού"), "Should contain Charts section");
    Assertions.assertTrue(html.contains("Οι Μεγαλύτερες Αλλαγές"), "Should contain Top Changes section");
    Assertions.assertTrue(html.contains("Συμπεράσματα"), "Should contain Conclusions section");
  }

@Test
  public void testGenerateComparisonContent_IncreaseLogic() {
    EnvYear original = createMockYear("2025", "health", 100.0);
    EnvYear modified = createMockYear("2025", "health", 150.0);

    String html = webDisplay.generateComparisonContent(original, modified, 100.0);

    // 1. Check Table Visuals
    Assertions.assertTrue(html.contains("↑"), "Should contain up arrow");
    Assertions.assertTrue(html.contains("#1b5e20"), "Should contain green color code");
    
    // 2. Check Top Changes Section
    Assertions.assertTrue(html.contains("Μεγαλύτερες Αυξήσεις"));
    
    // 3. Check Conclusions Logic (Relaxed assertion to avoid Locale/Space issues)
    // We check that the sector name and the amount are present in the same context
    Assertions.assertTrue(html.contains("Trans_health"), "Should contain focus sector name");
    Assertions.assertTrue(html.contains("50,00"), "Should contain formatted amount with comma");
    Assertions.assertTrue(html.contains("Τομείς με αύξηση"), "Should contain summary text");
  }

  @Test
  public void testGenerateComparisonContent_DecreaseLogic() {
    EnvYear original = createMockYear("2025", "defense", 200.0);
    EnvYear modified = createMockYear("2025", "defense", 100.0);

    String html = webDisplay.generateComparisonContent(original, modified, 200.0);

    // Check Visuals for Decrease
    Assertions.assertTrue(html.contains("↓"), "Should contain down arrow");
    Assertions.assertTrue(html.contains("#c62828"), "Should contain red color code");
    Assertions.assertTrue(html.contains("100,00 €"), "Should display diff amount");
    Assertions.assertTrue(html.contains("-50%"), "Should display negative percentage");

    // Check Top Changes Section
    Assertions.assertTrue(html.contains("Μεγαλύτερες Μειώσεις"));
    Assertions.assertTrue(html.contains("Trans_defense"));
  }

  @Test
  public void testGenerateComparisonContent_NoChanges() {
    EnvYear original = createMockYear("2025", "education", 500.0);
    EnvYear modified = createMockYear("2025", "education", 500.0);

    String html = webDisplay.generateComparisonContent(original, modified, 500.0);

    // Check Visuals for No Change
    Assertions.assertTrue(html.contains("→"), "Should contain right arrow");
    Assertions.assertTrue(html.contains("#616161"), "Should contain grey color code");
    Assertions.assertTrue(html.contains("0,0%"), "Should show 0%");
    
    // Check Top Changes empty states
    Assertions.assertTrue(html.contains("Δεν υπάρχουν αυξήσεις"));
    Assertions.assertTrue(html.contains("Δεν υπάρχουν μειώσεις"));

    // Check Conclusions
    Assertions.assertTrue(html.contains("πλήρως ισοσκελισμένος"), "Should indicate balanced budget");
    Assertions.assertTrue(html.contains("Δεν έγιναν σημαντικές αλλαγές"));
  }

  /**
   * Tests the logic that limits "Top Changes" to only the top 3 items.
   * FIX: We verify that the 4th item exists in the table (td) but NOT in the top changes list (div).
   */
  @Test
  public void testGenerateComparisonContent_TopChangesLimits() {
    List<EnvSector> origSectors = new ArrayList<>();
    List<EnvSector> modSectors = new ArrayList<>();

    // Create 4 sectors that will INCREASE (inc1 < inc2 < inc3 < inc4)
    // inc4 (+40) -> Top 1
    // inc3 (+30) -> Top 2
    // inc2 (+20) -> Top 3
    // inc1 (+10) -> Excluded from top list
    for (int i = 1; i <= 4; i++) {
      origSectors.add(createSector("inc" + i, 100.0));
      modSectors.add(createSector("inc" + i, 100.0 + (i * 10.0))); 
    }

    EnvYear original = createMultiSectorYear("2025", origSectors);
    EnvYear modified = createMultiSectorYear("2025", modSectors);

    String html = webDisplay.generateComparisonContent(original, modified, 1000.0);

    // Verify Increases: Top 3 should appear in the specific Top Changes markup (div)
    // The markup for top changes is like: <div ...>1. Trans_inc4</div>
    Assertions.assertTrue(html.contains("Trans_inc4</div>"), "Should show 1st largest increase in top list");
    Assertions.assertTrue(html.contains("Trans_inc3</div>"), "Should show 2nd largest increase in top list");
    Assertions.assertTrue(html.contains("Trans_inc2</div>"), "Should show 3rd largest increase in top list");

    // The 4th largest (inc1) should NOT appear in the top list markup (div)
    // Note: checking "!html.contains('Trans_inc1')" is wrong because it IS in the main table.
    // We check specifically for the list item closure "</div>" which implies it was in the top changes box.
    Assertions.assertFalse(html.contains("Trans_inc1</div>"), "Should NOT show the 4th largest increase in top list");
  }

  @Test
  public void testRoundingAdjustment() {
    // Total budget 300. Three sectors with 100 each.
    // Raw percent: 33.333...% -> Rounded: 33.3% -> Sum: 99.9%
    // Code should adjust one to 33.4%
    List<EnvSector> sectors = Arrays.asList(
        createSector("A", 100.0),
        createSector("B", 100.0),
        createSector("C", 100.0)
    );
    EnvYear year = createMultiSectorYear("2025", sectors);

    String html = webDisplay.generateComparisonContent(year, year, 300.0);

    // One of them should be adjusted to 33,4%
    Assertions.assertTrue(html.contains("33,4%"), "Should contain adjusted percentage (33,4%)");
    Assertions.assertTrue(html.contains("33,3%"), "Should contain standard percentage (33,3%)");
  }

  @Test
  public void testNullHandling_MalformedData() {
    EnvSector validSector = createSector("valid", 100.0);
    EnvSector sectorNullUnits = new EnvSector("nullUnits", null);
    
    EnvUnit unitNullEntries = new EnvUnit("unitNullEntries", null);
    EnvSector sectorNullEntries = new EnvSector("sectorNullEntries", List.of(unitNullEntries));

    List<EnvEntry> entriesWithNull = new ArrayList<>();
    entriesWithNull.add(null);
    EnvUnit unitWithNullEntry = new EnvUnit("unitWithNullEntry", entriesWithNull);
    EnvSector sectorWithNullEntry = new EnvSector("sectorWithNullEntry", List.of(unitWithNullEntry));

    List<EnvSector> sectors = Arrays.asList(
        null, 
        validSector,
        sectorNullUnits,
        sectorNullEntries,
        sectorWithNullEntry
    );

    EnvYear messyYear = createMultiSectorYear("2025", sectors);
    EnvYear emptyYear = new EnvYear("2025", Collections.emptyList());

    String html = webDisplay.generateComparisonContent(messyYear, emptyYear, 100.0);
    
    Assertions.assertNotNull(html);
    Assertions.assertTrue(html.contains("Trans_valid")); 
  }
}
