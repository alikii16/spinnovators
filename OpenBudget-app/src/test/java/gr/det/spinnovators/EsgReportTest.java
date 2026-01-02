package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gr.det.spinnovators.envdatamodel.EsgReport;

/**
 * Unit tests for EsgReport class.
 */
public class EsgReportTest {

    @Test
    public void testGetters() {
        EsgReport report = new EsgReport(
            "2025",
            1000000.0,
            400000.0, 300000.0, 200000.0, 100000.0,
            90.0, 70.0, 60.0, 85.0
        );

        assertEquals("2025", report.getYear(), "getYear should return correct year");
        assertEquals(1000000.0, report.getTotalBudget(), "getTotalBudget should return correct value");
        assertEquals(400000.0, report.getEnvironmentalAmount(), "getEnvironmentalAmount should return correct value");
        assertEquals(300000.0, report.getSocialAmount(), "getSocialAmount should return correct value");
        assertEquals(200000.0, report.getGovernanceAmount(), "getGovernanceAmount should return correct value");
        assertEquals(100000.0, report.getNeutralAmount(), "getNeutralAmount should return correct value");
        assertEquals(90.0, report.getEnvironmentalScore(), "getEnvironmentalScore should return correct value");
        assertEquals(70.0, report.getSocialScore(), "getSocialScore should return correct value");
        assertEquals(60.0, report.getGovernanceScore(), "getGovernanceScore should return correct value");
        assertEquals(85.0, report.getOverallScore(), "getOverallScore should return correct value");
    }

    @Test
    public void testGetRating() {
        EsgReport excellent = new EsgReport("2025", 0,0,0,0,0,0,0,0,90.0);
        EsgReport good      = new EsgReport("2025", 0,0,0,0,0,0,0,0,65.0);
        EsgReport moderate  = new EsgReport("2025", 0,0,0,0,0,0,0,0,50.0);
        EsgReport poor      = new EsgReport("2025", 0,0,0,0,0,0,0,0,30.0);
        EsgReport critical  = new EsgReport("2025", 0,0,0,0,0,0,0,0,10.0);

        assertEquals("Excellent", excellent.getRating(), "Overall score 90 should return 'Excellent'");
        assertEquals("Good", good.getRating(), "Overall score 65 should return 'Good'");
        assertEquals("Moderate", moderate.getRating(), "Overall score 50 should return 'Moderate'");
        assertEquals("Poor", poor.getRating(), "Overall score 30 should return 'Poor'");
        assertEquals("Critical", critical.getRating(), "Overall score 10 should return 'Critical'");
    }

    @Test
    public void testGetRatingGreek() {
        EsgReport excellent = new EsgReport("2025", 0,0,0,0,0,0,0,0,90.0);
        EsgReport good      = new EsgReport("2025", 0,0,0,0,0,0,0,0,65.0);
        EsgReport moderate  = new EsgReport("2025", 0,0,0,0,0,0,0,0,50.0);
        EsgReport poor      = new EsgReport("2025", 0,0,0,0,0,0,0,0,30.0);
        EsgReport critical  = new EsgReport("2025", 0,0,0,0,0,0,0,0,10.0);

        assertEquals("Άριστη", excellent.getRatingGreek(), "Overall score 90 should return 'Άριστη'");
        assertEquals("Καλή", good.getRatingGreek(), "Overall score 65 should return 'Καλή'");
        assertEquals("Μέτρια", moderate.getRatingGreek(), "Overall score 50 should return 'Μέτρια'");
        assertEquals("Χαμηλή", poor.getRatingGreek(), "Overall score 30 should return 'Χαμηλή'");
        assertEquals("Πολύ Χαμηλή", critical.getRatingGreek(), "Overall score 10 should return 'Πολύ Χαμηλή'");
    }
}
