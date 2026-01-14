package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link BudgetComparisonWebDisplay} class.
 *
 * <p>This test class verifies the HTML generation of budget comparisons.
 * It ensures that the generated web content correctly reflects budget changes
 * using visual indicators like arrows and colors, while maintaining
 * a clean and responsive HTML structure.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>HTML Basic structure and sections</li>
 * <li>Increase, Decrease, and Neutral logic visuals</li>
 * <li>Top changes sorting and limit logic</li>
 * <li>Percentage rounding adjustments for charts</li>
 * <li>Robustness against malformed or null data</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 2.0
 */
public class BudgetComparisonWebDisplayTest {

  private EnvBudgetTranslator mockTranslator;
  private BudgetComparisonWebDisplay webDisplay;

  /**
   * Sets up the test environment by mocking the translator component.
   * Mocking ensures that the test focuses solely on HTML generation logic.
   */
  @BeforeEach
  public void setUp() {
    mockTranslator = Mockito.mock(EnvBudgetTranslator.class);
    // Mock translation to return a predictable string "Trans_key"
    when(mockTranslator.translateCategory(anyString()))
        .thenAnswer(i -> "Trans_" + i.getArguments()[0]);

    webDisplay = new BudgetComparisonWebDisplay(mockTranslator);
  }

  // --- Helper Methods ---

  /**
   * Creates a mock year structure for testing purposes.
   */
  private EnvYear createMockYear(String yearStr, String sectorName, double amount) {
    EnvEntry entry = new EnvEntry("entry1", amount);
    EnvUnit unit = new EnvUnit("unit1", List.of(entry));
    EnvSector sector = new EnvSector(sectorName, List.of(unit));
    return new EnvYear(yearStr, List.of(sector));
  }

  /**
   * Creates a multi-sector year structure for testing aggregate logic.
   */
  private EnvYear createMultiSectorYear(String yearStr, List<EnvSector> sectors) {
    return new EnvYear(yearStr, sectors);
  }

  /**
   * Creates a mock sector with a specific key and monetary amount.
   */
  private EnvSector createSector(String key, double amount) {
    EnvEntry entry = new EnvEntry("ent_" + key, amount);
    EnvUnit unit = new EnvUnit("unit_" + key, List.of(entry));
    return new EnvSector(key, List.of(unit));
  }

  // --- Tests ---

  /**
   * Verifies that the generated HTML contains the main required sections
   * and the correct translated sector names.
   */
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

  /**
   * Tests the visual representation of a budget increase.
   * Checks for upward arrows and the specific green color code (#1b5e20).
   */
  @Test
  public void testGenerateComparisonContent_IncreaseLogic() {
    EnvYear original = createMockYear("2025", "health", 100.0);
    EnvYear modified = createMockYear("2025", "health", 150.0);

    String html = webDisplay.generateComparisonContent(original, modified, 100.0);

    Assertions.assertTrue(html.contains("↑"), "Should contain up arrow");
    Assertions.assertTrue(html.contains("#1b5e20"), "Should contain green color code");
    Assertions.assertTrue(html.contains("Trans_health"), "Should contain focus sector name");
    Assertions.assertTrue(html.contains("50,00"), "Should contain formatted amount");
    Assertions.assertTrue(html.contains("Τομείς με αύξηση"), "Should contain summary text");
  }

  /**
   * Tests the visual representation of a budget decrease.
   * Checks for downward arrows and the specific red color code (#c62828).
   */
  @Test
  public void testGenerateComparisonContent_DecreaseLogic() {
    EnvYear original = createMockYear("2025", "defense", 200.0);
    EnvYear modified = createMockYear("2025", "defense", 100.0);

    String html = webDisplay.generateComparisonContent(original, modified, 200.0);

    Assertions.assertTrue(html.contains("↓"), "Should contain down arrow");
    Assertions.assertTrue(html.contains("#c62828"), "Should contain red color code");
    Assertions.assertTrue(html.contains("100,00 €"), "Should display diff amount");
    Assertions.assertTrue(html.contains("-50%"), "Should display negative percentage");
    Assertions.assertTrue(html.contains("Μεγαλύτερες Μειώσεις"), "Should contain header");
  }

  /**
   * Tests the HTML output when no changes are detected.
   * Verifies the use of neutral arrows and empty state messages.
   */
  @Test
  public void testGenerateComparisonContent_NoChanges() {
    EnvYear original = createMockYear("2025", "education", 500.0);
    EnvYear modified = createMockYear("2025", "education", 500.0);

    String html = webDisplay.generateComparisonContent(original, modified, 500.0);

    Assertions.assertTrue(html.contains("→"), "Should contain right arrow");
    Assertions.assertTrue(html.contains("#616161"), "Should contain grey color code");
    Assertions.assertTrue(html.contains("Δεν υπάρχουν αυξήσεις"), "Should show empty increase message");
    Assertions.assertTrue(html.contains("πλήρως ισοσκελισμένος"), "Should indicate balanced budget");
  }

  /**
   * Verifies that only the top 3 increases/decreases are shown in the
   * "Top Changes" summary box to keep the interface clean.
   */
  @Test
  public void testGenerateComparisonContent_TopChangesLimits() {
    List<EnvSector> origSectors = new ArrayList<>();
    List<EnvSector> modSectors = new ArrayList<>();

    for (int i = 1; i <= 4; i++) {
      origSectors.add(createSector("inc" + i, 100.0));
      modSectors.add(createSector("inc" + i, 100.0 + (i * 10.0)));
    }

    EnvYear original = createMultiSectorYear("2025", origSectors);
    EnvYear modified = createMultiSectorYear("2025", modSectors);

    String html = webDisplay.generateComparisonContent(original, modified, 1000.0);

    Assertions.assertTrue(html.contains("Trans_inc4</div>"), "Should show 1st largest increase");
    Assertions.assertTrue(html.contains("Trans_inc3</div>"), "Should show 2nd largest increase");
    Assertions.assertTrue(html.contains("Trans_inc2</div>"), "Should show 3rd largest increase");
    Assertions.assertFalse(html.contains("Trans_inc1</div>"), "Should NOT show 4th item in top list");
  }

  /**
   * Tests the percentage adjustment logic for pie charts.
   * Ensures that rounded values sum exactly to 100.0%.
   */
  @Test
  public void testRoundingAdjustment() {
    List<EnvSector> sectors = Arrays.asList(
        createSector("A", 100.0),
        createSector("B", 100.0),
        createSector("C", 100.0)
    );
    EnvYear year = createMultiSectorYear("2025", sectors);

    String html = webDisplay.generateComparisonContent(year, year, 300.0);

    Assertions.assertTrue(html.contains("33,4%"), "Should contain adjusted percentage");
    Assertions.assertTrue(html.contains("33,3%"), "Should contain standard percentage");
  }

  /**
   * Tests robustness against null or empty data structures.
   * Verifies that the generator can still produce a page even with messy data.
   */
  @Test
  public void testNullHandling_MalformedData() {
    EnvSector validSector = createSector("valid", 100.0);
    EnvSector sectorNullUnits = new EnvSector("nullUnits", null);
    EnvYear messyYear = createMultiSectorYear("2025", Arrays.asList(null, validSector, sectorNullUnits));

    String html = webDisplay.generateComparisonContent(messyYear,
        new EnvYear("2025", Collections.emptyList()), 100.0);

    Assertions.assertNotNull(html);
    Assertions.assertTrue(html.contains("Trans_valid"));
  }
}