package gr.det.spinnovators.envdatamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link EsgCategory} enum.
 *
 * <p>This test class verifies that all ESG (Environmental, Social, and
 * Governance) category enum constants return the correct English and Greek
 * names through their getter methods.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>English name retrieval for all ESG categories</li>
 *   <li>Greek name retrieval for all ESG categories</li>
 *   <li>Verification of ENVIRONMENTAL category names</li>
 *   <li>Verification of SOCIAL category names</li>
 *   <li>Verification of GOVERNANCE category names</li>
 *   <li>Verification of NEUTRAL category names</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
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
