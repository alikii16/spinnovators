package gr.det.spinnovators;

import gr.det.spinnovators.service.EsgClassifier;
import gr.det.spinnovators.envdatamodel.EsgCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EsgClassifier
 */
public class EsgClassifierTest {

    private EsgClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new EsgClassifier();
    }

    @Test
    void testEntrySpecificClassification() {
        // personnel_costs should be SOCIAL
        EsgCategory result = classifier.classifyEntry("any_sector", "personnel_costs");
        assertEquals(EsgCategory.SOCIAL, result);

        // community_benefits should be SOCIAL
        result = classifier.classifyEntry("any_sector", "community_benefits");
        assertEquals(EsgCategory.SOCIAL, result);

        // purchase_of_goods_and_services should be GOVERNANCE
        result = classifier.classifyEntry("any_sector", "purchase_of_goods_and_services");
        assertEquals(EsgCategory.GOVERNANCE, result);
    }

    @Test
    void testSectorLevelClassification() {
        // natural_environment_and_water_protection → ENVIRONMENTAL
        EsgCategory result = classifier.classifyEntry("natural_environment_and_water_protection", "other_entry");
        assertEquals(EsgCategory.ENVIRONMENTAL, result);

        // executive_coordination_and_investments → GOVERNANCE
        result = classifier.classifyEntry("executive_coordination_and_investments", "other_entry");
        assertEquals(EsgCategory.GOVERNANCE, result);
    }

    @Test
    void testContextDependentEntryInEnvironmentalSector() {
        // credits_under_allocation in an environmental sector → ENVIRONMENTAL
        EsgCategory result = classifier.classifyEntry("natural_environment_and_water_protection", "credits_under_allocation");
        assertEquals(EsgCategory.ENVIRONMENTAL, result);
    }

    @Test
    void testEnergySectorClassification() {
        // credits_under_allocation in energy sector → ENVIRONMENTAL
        EsgCategory result = classifier.classifyEntry("energy_and_mineral_resources_management", "credits_under_allocation");
        assertEquals(EsgCategory.ENVIRONMENTAL, result);

        // permanent_assets in energy sector → ENVIRONMENTAL
        result = classifier.classifyEntry("energy_and_mineral_resources_management", "permanent_assets");
        assertEquals(EsgCategory.ENVIRONMENTAL, result);

        // random entry in energy sector → GOVERNANCE
        result = classifier.classifyEntry("energy_and_mineral_resources_management", "random_entry");
        assertEquals(EsgCategory.GOVERNANCE, result);
    }

    @Test
    void testDefaultNeutralClassification() {
        // Unknown sector and entry → NEUTRAL
        EsgCategory result = classifier.classifyEntry("unknown_sector", "unknown_entry");
        assertEquals(EsgCategory.NEUTRAL, result);
    }

    @Test
    void testGetSectorClassificationName() {
        String name = classifier.getSectorClassificationName("natural_environment_and_water_protection");
        assertEquals("Περιβαλλοντικές", name);

        name = classifier.getSectorClassificationName("executive_coordination_and_investments");
        assertEquals("Διοίκηση", name);

        name = classifier.getSectorClassificationName("energy_and_mineral_resources_management");
        assertEquals("Μικτές (Ε & G)", name);

        name = classifier.getSectorClassificationName("unknown_sector");
        assertEquals("Ουδέτερες", name);
    }
}
