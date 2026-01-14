package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EsgCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the {@link EsgClassifier} class.
 *
 * <p>This test suite ensures that the classification logic strictly follows the
 * priority hierarchy defined in the business rules. It aims for 100% branch
 * coverage by testing all possible paths in the decision-making process.</p>
 *
 * <p>The classification priority is as follows:
 * <ol>
 * <li>Entry-specific Rules (Highest Priority)</li>
 * <li>Recovery Fund Special Cases</li>
 * <li>Unit-level Classifications</li>
 * <li>Sector-level Classifications</li>
 * <li>Energy Sector Specifics (Lowest Priority)</li>
 * </ol>
 * </p>
 *
 * @author Spinnovators Team
 * @version 2.0
 */
class EsgClassifierTest {

  private EsgClassifier classifier;

  /**
   * Initializes the EsgClassifier instance before each test.
   */
  @BeforeEach
  void setUp() {
    classifier = new EsgClassifier();
  }

  // ==================================================================================
  // PRIORITY 1: ENTRY-SPECIFIC CLASSIFICATIONS
  // ==================================================================================

  /**
   * Tests classification for entries that have a fixed ESG category regardless
   * of the sector or unit they belong to.
   */
  @Test
  @DisplayName("Priority 1: Specific entries should override all other rules")
  void testEntrySpecificClassification() {
    // "personnel_costs" is strictly SOCIAL
    assertEquals(EsgCategory.SOCIAL,
        classifier.classifyEntry("any_sector", "personnel_costs"),
        "Personnel costs should always be classified as SOCIAL.");

    // "community_benefits" is strictly SOCIAL
    assertEquals(EsgCategory.SOCIAL,
        classifier.classifyEntry("any_sector", "community_benefits"),
        "Community benefits should always be classified as SOCIAL.");

    // "purchase_of_goods_and_services" is strictly GOVERNANCE
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry("any_sector", "purchase_of_goods_and_services"),
        "Goods and Services should always be classified as GOVERNANCE.");

    // "interest_payments" should be NEUTRAL
    assertEquals(EsgCategory.NEUTRAL,
        classifier.classifyEntry("any_sector", "interest_payments"),
        "Interest payments should be NEUTRAL.");
  }

  // ==================================================================================
  // PRIORITY 2: RECOVERY AND RESILIENCE FUNDS
  // ==================================================================================

  /**
   * Tests the complex logic for Recovery and Resilience Fund expenses.
   * These are classified differently based on the sector they are allocated to.
   */
  @Test
  @DisplayName("Priority 2: Recovery Funds should be classified based on Sector context")
  void testRecoveryFundStrategies() {
    String rfKey = "recovery_and_resilience_fund_expenses";
    String ctxEntry = "credits_under_allocation";

    // Case A: Natural Environment Sector -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("natural_environment_and_water_protection", rfKey, ctxEntry),
        "Recovery funds in Natural Environment sector must be ENVIRONMENTAL.");

    // Case B: Spatial Planning Sector -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("spatial_planning_and_urban_environment", rfKey, ctxEntry),
        "Recovery funds in Spatial Planning sector must be ENVIRONMENTAL.");

    // Case C: Energy Sector -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("energy_and_mineral_resources_management", rfKey, ctxEntry),
        "Recovery funds in Energy sector must be ENVIRONMENTAL.");

    // Case D: Executive/Governance Sector -> GOVERNANCE
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry("executive_coordination_and_investments", rfKey, ctxEntry),
        "Recovery funds in Executive sector must be GOVERNANCE.");

    // Case E: Forestry/Parks -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("forests_and_natural_environment", rfKey, ctxEntry),
        "Recovery funds in Forestry must be ENVIRONMENTAL.");
  }

  /**
   * Verifies that the classifier can detect Recovery Fund keywords within
   * longer entry strings.
   */
  @Test
  @DisplayName("Priority 2: Overloaded method detects Recovery Funds via entry key string")
  void testOverloadedMethodWithRecoveryKey() {
    EsgCategory result = classifier.classifyEntry(
        "natural_environment_and_water_protection",
        "some_recovery_and_resilience_program_alpha"
    );
    assertEquals(EsgCategory.ENVIRONMENTAL, result,
        "Should detect recovery fund keyword in entry string.");
  }

  // ==================================================================================
  // PRIORITY 3: UNIT-LEVEL CLASSIFICATIONS
  // ==================================================================================

  /**
   * Tests unit-level classification rules, specifically focusing on
   * specialized secretariats and general ministerial units.
   */
  @Test
  @DisplayName("Priority 3: Unit-level rules including specialized secretariats")
  void testUnitLevelClassification() {
    // Case: Waste Management -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("any_sector", "general_secretariat_for_waste_management", "random"),
        "Waste Management Secretariat must be ENVIRONMENTAL.");

    // Case: Water Management -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("any_sector", "general_secretariat_for_natural_environment_and_waters", "random"),
        "Water Management Secretariat must be ENVIRONMENTAL.");

    // Case: Ministerial Units -> GOVERNANCE
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry("any_sector", "other_ministerial_units", "random"),
        "Generic ministerial units must be GOVERNANCE.");
  }

  /**
   * Verifies that context-dependent entries correctly inherit the ESG category
   * from their parent Unit.
   */
  @Test
  @DisplayName("Priority 3: Context-dependent entries inherit Unit category")
  void testUnitLevelWithContextDependency() {
    String envUnit = "general_secretariat_for_waste_management";
    // "transfers" is context-dependent, should be ENVIRONMENTAL here
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry("any_sector", envUnit, "transfers"),
        "Transfers in an Environmental Unit should be ENVIRONMENTAL.");
  }

  // ==================================================================================
  // PRIORITY 4: SECTOR-LEVEL CLASSIFICATIONS
  // ==================================================================================

  /**
   * Tests sector-level classification for broad categories like Spatial Planning
   * and Executive Coordination.
   */
  @Test
  @DisplayName("Priority 4: Sector-level rules and inheritance")
  void testSectorLevelClassification() {
    String envSector = "spatial_planning_and_urban_environment";
    String govSector = "executive_coordination_and_investments";

    // Spatial Planning entries default to ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry(envSector, "unknown_unit", "random_entry"));

    // Executive Coordination entries default to GOVERNANCE
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry(govSector, "unknown_unit", "random_entry"));
  }

  // ==================================================================================
  // PRIORITY 5: ENERGY SECTOR SPECIFICS
  // ==================================================================================

  /**
   * Tests the Energy and Mineral Resources sector, which has unique rules
   * for splitting administrative and investment costs.
   */
  @Test
  @DisplayName("Priority 5: Energy sector complex rule set")
  void testEnergySectorDeepDive() {
    String energySector = "energy_and_mineral_resources_management";

    // Credits/Investments in Energy -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry(energySector, "credits_under_allocation"));

    // Assets in Energy -> ENVIRONMENTAL
    assertEquals(EsgCategory.ENVIRONMENTAL,
        classifier.classifyEntry(energySector, "permanent_assets"));

    // General administrative in Energy -> GOVERNANCE
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry(energySector, "administrative_fees"));
  }

  // ==================================================================================
  // UTILITIES & DEFAULTS
  // ==================================================================================

  /**
   * Verifies that the localization of sector classification strings returns
   * the correct Greek labels.
   */
  @Test
  @DisplayName("Utility: Verify Greek sector name localization")
  void testGetSectorClassificationName() {
    assertEquals("Περιβαλλοντικές",
        classifier.getSectorClassificationName("natural_environment_and_water_protection"));

    assertEquals("Μικτές (Ε & G)",
        classifier.getSectorClassificationName("energy_and_mineral_resources_management"));

    assertEquals("Διοίκηση",
        classifier.getSectorClassificationName("executive_coordination_and_investments"));

    assertEquals("Ουδέτερες",
        classifier.getSectorClassificationName("non_existent_sector"));
  }

  /**
   * Tests the default behavior when no rules match.
   */
  @Test
  @DisplayName("Default: Unknown inputs result in NEUTRAL")
  void testDefaultNeutralClassification() {
    assertEquals(EsgCategory.NEUTRAL,
        classifier.classifyEntry("unknown", "unknown", "unknown"));
  }

  // ==================================================================================
  // BRANCH COVERAGE & EDGE CASES
  // ==================================================================================

  /**
   * Tests the behavior when unitKey or entryKey are null to ensure null-safety.
   */
  @Test
  @DisplayName("Branch Coverage: Null inputs handling")
  void testNullInputs() {
    assertNotNull(classifier.classifyEntry("sector", null, "entry"));
    assertNotNull(classifier.classifyEntry("sector", "unit", null));
  }

  /**
   * Tests the overloaded method fallback when the recovery keyword is not present.
   */
  @Test
  @DisplayName("Branch Coverage: Overload fallback logic")
  void testOverloadFallback() {
    assertEquals(EsgCategory.GOVERNANCE,
        classifier.classifyEntry("executive_coordination_and_investments", "simple_entry"),
        "Should fall back to standard logic if keyword is missing.");
  }
}