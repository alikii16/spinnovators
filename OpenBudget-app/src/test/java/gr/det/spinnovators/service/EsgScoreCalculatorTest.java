package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgCategory;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.service.EsgScoreCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EsgScoreCalculatorTest {

    @Test
    void testCalculateReport() {
        // Create entries
        EnvEntry e1 = new EnvEntry("personnel_costs", 1000.0); // SOCIAL
        EnvEntry e2 = new EnvEntry("credits_under_allocation", 2000.0); // ENVIRONMENTAL
        EnvEntry e3 = new EnvEntry("purchase_of_goods_and_services", 500.0); // GOVERNANCE

        // Create units
        EnvUnit unit1 = new EnvUnit("unit1", List.of(e1, e2));
        EnvUnit unit2 = new EnvUnit("unit2", List.of(e3));

        // Create sectors
        EnvSector sector1 = new EnvSector("natural_environment_and_water_protection", List.of(unit1));
        EnvSector sector2 = new EnvSector("executive_coordination_and_investments", List.of(unit2));

        // Create year
        EnvYear year2025 = new EnvYear("2025", List.of(sector1, sector2));

        // Total budget
        double totalBudget = 10000.0;

        // Create calculator
        EsgScoreCalculator calculator = new EsgScoreCalculator();

        // Calculate report
        EsgReport report = calculator.calculateReport(year2025, totalBudget);

        // Check year and total budget
        assertEquals("2025", report.getYear());
        assertEquals(totalBudget, report.getTotalBudget());

        // Check amounts by category
        assertEquals(2000.0, report.getEnvironmentalAmount(), 0.001);
        assertEquals(1000.0, report.getSocialAmount(), 0.001);
        assertEquals(500.0, report.getGovernanceAmount(), 0.001);
        assertEquals(0.0, report.getNeutralAmount(), 0.001);

        // Check individual scores
        assertEquals(20.0, report.getEnvironmentalScore(), 0.001); // 2000 / 10000 * 100
        assertEquals(10.0, report.getSocialScore(), 0.001);        // 1000 / 10000 * 100
        assertEquals(5.0, report.getGovernanceScore(), 0.001);    // 500 / 10000 * 100

        // Check overall score = 20*0.4 + 10*0.3 + 5*0.3 = 8+3+1.5 = 12.5
        assertEquals(12.5, report.getOverallScore(), 0.001);
    }

    @Test
    void testCalculateScoreDifference() {
        // Prepare a simple before and after report
        EsgReport before = new EsgReport("2025", 10000.0,
                2000, 1000, 500, 0,
                20, 10, 5, 12.5);

        EsgReport after = new EsgReport("2025", 10000.0,
                3000, 1000, 500, 0,
                30, 10, 5, 15.5);

        EsgScoreCalculator calculator = new EsgScoreCalculator();

        double diff = calculator.calculateScoreDifference(before, after);

        // Difference = 15.5 - 12.5 = 3.0
        assertEquals(3.0, diff, 0.001);
    }
}
