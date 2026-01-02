package gr.det.spinnovators.envdatamodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gr.det.spinnovators.envdatamodel.EsgCategory;

/**
 * Unit tests for EsgCategory enum.
 */
public class EsgCategoryTest {

    @Test
    public void testGetNameEn() {
        assertEquals("Environmental", EsgCategory.ENVIRONMENTAL.getNameEn(),
            "ENVIRONMENTAL should have correct English name");
        assertEquals("Social", EsgCategory.SOCIAL.getNameEn(),
            "SOCIAL should have correct English name");
        assertEquals("Governance", EsgCategory.GOVERNANCE.getNameEn(),
            "GOVERNANCE should have correct English name");
        assertEquals("Neutral", EsgCategory.NEUTRAL.getNameEn(),
            "NEUTRAL should have correct English name");
    }

    @Test
    public void testGetNameEl() {
        assertEquals("Περιβαλλοντικές", EsgCategory.ENVIRONMENTAL.getNameEl(),
            "ENVIRONMENTAL should have correct Greek name");
        assertEquals("Κοινωνικές", EsgCategory.SOCIAL.getNameEl(),
            "SOCIAL should have correct Greek name");
        assertEquals("Διοίκηση", EsgCategory.GOVERNANCE.getNameEl(),
            "GOVERNANCE should have correct Greek name");
        assertEquals("Ουδέτερες", EsgCategory.NEUTRAL.getNameEl(),
            "NEUTRAL should have correct Greek name");
    }
}
