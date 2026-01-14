package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EsgCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link EsgClassifier} class.
 *
 * <p>These tests ensure that the classification logic strictly follows the
 * priority hierarchy defined in the business rules:
 * <ol>
 * <li>Entry-specific Rules (Highest Priority)</li>
 * <li>Recovery Fund Special Cases</li>
 * <li>Unit-level Classifications</li>
 * <li>Sector-level Classifications</li>
 * <li>Energy Sector Specifics (Lowest Priority)</li>
 * </ol>
 */
class EsgClassifierTest {

    private EsgClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new EsgClassifier();
    }

    // ==================================================================================
    // PRIORITY 1: ENTRY-SPECIFIC CLASSIFICATIONS
    // ==================================================================================

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
    }

    // ==================================================================================
    // PRIORITY 2: RECOVERY AND RESILIENCE FUNDS
    // ==================================================================================

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

        // Case C: Energy Sector -> ENVIRONMENTAL (Green Transition)
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry("energy_and_mineral_resources_management", rfKey, ctxEntry),
            "Recovery funds in Energy sector must be ENVIRONMENTAL.");

        // Case D: Executive/Governance Sector -> GOVERNANCE (Digital Transformation)
        assertEquals(EsgCategory.GOVERNANCE, 
            classifier.classifyEntry("executive_coordination_and_investments", rfKey, ctxEntry),
            "Recovery funds in Executive sector must be GOVERNANCE.");

        // Case E: Unknown/Other Sector -> Default to ENVIRONMENTAL
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry("unknown_sector", rfKey, ctxEntry),
            "Recovery funds in unknown sectors should default to ENVIRONMENTAL.");
    }

    @Test
    @DisplayName("Priority 2: Overloaded method detects Recovery Funds via entry key")
    void testOverloadedMethodWithRecoveryKey() {
        // Test the 2-argument overload where the entry key contains the keyword
        EsgCategory result = classifier.classifyEntry(
            "natural_environment_and_water_protection", 
            "some_recovery_and_resilience_program_xyz"
        );
        assertEquals(EsgCategory.ENVIRONMENTAL, result, 
            "Should detect recovery fund keyword in entry string and classify accordingly.");
    }

    // ==================================================================================
    // PRIORITY 3: UNIT-LEVEL CLASSIFICATIONS
    // ==================================================================================

    @Test
    @DisplayName("Priority 3: Unit-level rules including context dependency")
    void testUnitLevelClassification() {
        // Case 1: Specific Unit Mapping (Waste Management -> ENVIRONMENTAL)
        EsgCategory result = classifier.classifyEntry(
            "any_sector", 
            "general_secretariat_for_waste_management", 
            "random_entry"
        );
        assertEquals(EsgCategory.ENVIRONMENTAL, result, 
            "Waste Management Secretariat must be ENVIRONMENTAL regardless of sector.");

        // Case 2: Other Ministerial Units -> GOVERNANCE
        result = classifier.classifyEntry(
            "any_sector", 
            "other_ministerial_units", 
            "transfers"
        );
        assertEquals(EsgCategory.GOVERNANCE, result, 
            "Generic ministerial units must be GOVERNANCE.");
    }

    @Test
    @DisplayName("Priority 3: Context-dependent entries inherit Unit category")
    void testUnitLevelWithContextDependency() {
        String envUnit = "general_secretariat_for_waste_management";
        
        // "transfers" is a context-dependent key. It should inherit the Unit's category (ENVIRONMENTAL)
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry("any_sector", envUnit, "transfers"),
            "Context-dependent entries should inherit the Unit's classification.");
    }

    // ==================================================================================
    // PRIORITY 4: SECTOR-LEVEL CLASSIFICATIONS
    // ==================================================================================

    @Test
    @DisplayName("Priority 4: Sector-level rules and context dependency")
    void testSectorLevelClassification() {
        String envSector = "natural_environment_and_water_protection";
        String govSector = "executive_coordination_and_investments";

        // 1. Standard Entry in Environmental Sector
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry(envSector, "unknown_unit", "standard_entry"));

        // 2. Standard Entry in Governance Sector
        assertEquals(EsgCategory.GOVERNANCE, 
            classifier.classifyEntry(govSector, "unknown_unit", "standard_entry"));

        // 3. Context Dependent Entry ("transfers") in Environmental Sector
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry(envSector, "unknown_unit", "transfers"),
            "Context-dependent entry in Env Sector should be ENVIRONMENTAL.");

        // 4. Context Dependent Entry ("transfers") in Governance Sector
        assertEquals(EsgCategory.GOVERNANCE, 
            classifier.classifyEntry(govSector, "unknown_unit", "transfers"),
            "Context-dependent entry in Gov Sector should be GOVERNANCE.");
    }

    // ==================================================================================
    // PRIORITY 5: ENERGY SECTOR SPECIFICS
    // ==================================================================================

    @Test
    @DisplayName("Priority 5: Energy sector specific rule set")
    void testEnergySectorDeepDive() {
        String energySector = "energy_and_mineral_resources_management";

        // Green Credits -> ENVIRONMENTAL
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry(energySector, "credits_under_allocation"));

        // Transfers -> ENVIRONMENTAL
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry(energySector, "transfers"));

        // Permanent Assets -> ENVIRONMENTAL
        assertEquals(EsgCategory.ENVIRONMENTAL, 
            classifier.classifyEntry(energySector, "permanent_assets"));

        // Fallback for Energy Sector -> GOVERNANCE
        assertEquals(EsgCategory.GOVERNANCE, 
            classifier.classifyEntry(energySector, "random_administrative_cost"),
            "Unclassified entries in Energy sector should fallback to GOVERNANCE.");
    }

    // ==================================================================================
    // UTILITIES & DEFAULTS
    // ==================================================================================

    @Test
    @DisplayName("Default: Unknown inputs should result in NEUTRAL")
    void testDefaultNeutralClassification() {
        EsgCategory result = classifier.classifyEntry("unknown_sector", "unknown_unit", "unknown_entry");
        assertEquals(EsgCategory.NEUTRAL, result);
    }

    @Test
    @DisplayName("Utility: Verify sector name localization")
    void testGetSectorClassificationName() {
        assertEquals("Περιβαλλοντικές", 
            classifier.getSectorClassificationName("natural_environment_and_water_protection"));

        assertEquals("Διοίκηση", 
            classifier.getSectorClassificationName("executive_coordination_and_investments"));

        assertEquals("Μικτές (Ε & G)", 
            classifier.getSectorClassificationName("energy_and_mineral_resources_management"));

        assertEquals("Ουδέτερες", 
            classifier.getSectorClassificationName("unknown_sector"));
    }

    // ==================================================================================
    // EDGE CASES & BRANCH COVERAGE
    // ==================================================================================

    @Test
    @DisplayName("Branch Coverage: Test Null Unit Key to satisfy 'unitKey != null' check")
    void testNullUnitKey() {
        // Triggers the null check inside isRecoveryFundUnit
        EsgCategory result = classifier.classifyEntry("any_sector", null, "simple_entry");
        assertNotNull(result, "Classification should proceed gracefully even with null unit key.");
    }

    @Test
    @DisplayName("Branch Coverage: Test Overloaded Method with Non-Recovery Key")
    void testOverloadWithoutRecoveryKey() {
        // Triggers the false branch of 'if (entryKey.contains("recovery..."))'
        EsgCategory result = classifier.classifyEntry("natural_environment_and_water_protection", "simple_non_recovery_entry");
        assertEquals(EsgCategory.ENVIRONMENTAL, result, 
            "Should fall back to standard logic when keyword is missing.");
    }
}