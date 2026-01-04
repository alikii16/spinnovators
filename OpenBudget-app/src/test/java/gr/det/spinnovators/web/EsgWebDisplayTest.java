package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * Unit test for EsgWebDisplay.
 *
 * <p>Tests the HTML generation of ESG comparison content using dummy data.
 * Ensures the method executes without exceptions and returns non-empty content.</p>
 */
public class EsgWebDisplayTest {

    /**
     * Test generating ESG comparison content.
     */
    @Test
    public void testGenerateEsgComparisonContent() {
        // Create dummy entries
        EnvEntry entry1 = new EnvEntry("entry1", 50.0);
        EnvEntry entry2 = new EnvEntry("entry2", 80.0);

        // Create units
        EnvUnit unit1 = new EnvUnit("unit1", List.of(entry1));
        EnvUnit unit2 = new EnvUnit("unit2", List.of(entry2));

        // Create sectors
        EnvSector sector1 = new EnvSector("executive_coordination_and_investments", List.of(unit1));
        EnvSector sector2 = new EnvSector("natural_environment_and_water_protection", List.of(unit2));

        // Create EnvYear objects
        EnvYear originalYear = new EnvYear("2025", List.of(sector1, sector2));
        EnvYear modifiedYear = new EnvYear("2026", List.of(sector1, sector2));

        // Total budget
        double totalBudget = 200.0;

        // Create EsgWebDisplay
        EsgWebDisplay webDisplay = new EsgWebDisplay();

        // Generate ESG comparison content
        String htmlContent = webDisplay.generateEsgComparisonContent(originalYear, modifiedYear, totalBudget);

        // Simple assertions
        assert htmlContent != null && !htmlContent.isEmpty() : "HTML content should not be empty";

        // Optional: print for manual inspection
        System.out.println(htmlContent);
    }
}
