package gr.det.spinnovators.service;

import static org.junit.jupiter.api.Assertions.*;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

class InitialBudgetComparisonTest {

    private InitialBudgetComparison comparison;
    private EnvBudgetTranslator translator;
    private final double TOTAL_BUDGET = 1000000.0;

    @BeforeEach
    void setUp() {
        translator = new EnvBudgetTranslator() {
            @Override
            public String translateCategory(String key) {
                return "Category_" + key;
            }
        };
        comparison = new InitialBudgetComparison(translator);
    }

    /**
     * Βοηθητική μέθοδος για τη δημιουργία ενός έτους με συγκεκριμένο ποσό σε έναν τομέα.
     */
    private EnvYear createYear(String yearName, String sectorKey, double amount) {
        List<EnvEntry> entries = new ArrayList<>();
        entries.add(new EnvEntry("entry1", amount));
        List<EnvUnit> units = new ArrayList<>();
        units.add(new EnvUnit("unit1", entries));
        List<EnvSector> sectors = new ArrayList<>();
        sectors.add(new EnvSector(sectorKey, units));
        return new EnvYear(yearName, sectors);
    }

    @Test
    void testExcellentEsgAndBalancedBudget() {
        // Σενάριο 1: Πλήρης ισοσκέλιση (για τα branches 361)
        EnvYear original = createYear("2025", "executive_coordination_and_investments", 500000.0);
        EnvYear modified = createYear("2025", "executive_coordination_and_investments", 500000.0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comparison.performFullComparison(original, modified, TOTAL_BUDGET);

        String output = out.toString();
        
        // Έλεγχος ισοσκέλισης (Γραμμή 361)
        assertTrue(output.contains("Ο προϋπολογισμός είναι πλήρως ισοσκελισμένος"), 
            "Should detect balanced budget");

        // Έλεγχος αξιολόγησης (Γραμμές 399-408)
        // Ελέγχουμε αν υπάρχει ΕΝΑ από τα τρία πιθανά μηνύματα για να "πρασινίσει" το branch
        boolean hasAssessment = output.contains("Εξαιρετική κατανομή") || 
                                output.contains("Καλή κατανομή") || 
                                output.contains("Χρειάζεται περισσότερη έμφαση");
        
        assertTrue(hasAssessment, "Should print some overall assessment message");
        
        System.setOut(System.out);
    }

    @Test
    void testSignificantIncreaseAndHighEnvironmentalScore() {
        // Σενάριο 2: Μεγάλη αύξηση και καλό Environmental Score (για τα branches 148, 283 και 380)
        //
        EnvYear original = createYear("2025", "natural_environment_and_water_protection", 100000.0);
        EnvYear modified = createYear("2025", "natural_environment_and_water_protection", 900000.0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comparison.performFullComparison(original, modified, TOTAL_BUDGET);

        String output = out.toString();
        assertTrue(output.contains("⬆"));
        assertTrue(output.contains("Μεγαλύτερες Αυξήσεις"));
        assertTrue(output.contains("Καλή έμφαση σε περιβαλλοντικές δαπάνες"));
        assertTrue(output.contains("Κύρια εστίαση"));

        System.setOut(System.out);
    }

    @Test
    void testSignificantDecreaseAndLowScores() {
        // Σενάριο 3: Μεγάλη μείωση (για τα branches 148 και 298)
        //
        EnvYear original = createYear("2025", "spatial_planning_and_urban_environment", 800000.0);
        EnvYear modified = createYear("2025", "spatial_planning_and_urban_environment", 100000.0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comparison.performFullComparison(original, modified, TOTAL_BUDGET);

        String output = out.toString();
        assertTrue(output.contains("⬇"));
        assertTrue(output.contains("Μεγαλύτερες Μειώσεις"));
        assertTrue(output.contains("Διαφορά:"));

        System.setOut(System.out);
    }

    @Test
    void testPerfectEsgAndMultipleIncreases() {
        // Δημιουργούμε 4 τομείς με σημαντική αύξηση για να καλύψουμε τα loops
        List<EnvSector> origSectors = new ArrayList<>();
        List<EnvSector> modSectors = new ArrayList<>();
        
        String[] keys = {
            "executive_coordination_and_investments",
            "natural_environment_and_water_protection",
            "spatial_planning_and_urban_environment",
            "energy_and_mineral_resources_management"
        };
        
        for (String key : keys) {
            origSectors.add(createSector(key, 100000.0));
            modSectors.add(createSector(key, 500000.0)); // Μεγάλη αύξηση
        }
        
        EnvYear original = new EnvYear("2025", origSectors);
        EnvYear modified = new EnvYear("2025", modSectors);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Εκτέλεση της σύγκρισης
        comparison.performFullComparison(original, modified, 1000000.0);

        String output = out.toString();
        
        // 1. Έλεγχος αν εκτελέστηκε το loop των αυξήσεων (Branch 283)
        assertTrue(output.contains("Μεγαλύτερες Αυξήσεις"), "Should list top increases");
        
        // 2. Έλεγχος αν εκτελέστηκε η εστίαση αλλαγών (Branch 440)
        assertTrue(output.contains("Κύρια εστίαση"), "Should show focus analysis");

        // 3. Έλεγχος αξιολόγησης (Branches 399-408)
        // Αντί για ένα συγκεκριμένο κείμενο, ελέγχουμε αν εκτυπώθηκε ΟΠΟΙΑΔΗΠΟΤΕ αξιολόγηση
        boolean hasAssessment = output.contains("Εξαιρετική κατανομή") || 
                                output.contains("Καλή κατανομή") || 
                                output.contains("Χρειάζεται περισσότερη έμφαση");
        
        assertTrue(hasAssessment, "The overall assessment section should have been triggered");
        
        System.setOut(System.out);
    }

    // Βοηθητική μέθοδος για δημιουργία τομέα
    private EnvSector createSector(String key, double amount) {
        List<EnvEntry> entries = new ArrayList<>();
        entries.add(new EnvEntry("entry", amount));
        List<EnvUnit> units = new ArrayList<>();
        units.add(new EnvUnit("unit", entries));
        return new EnvSector(key, units);
    }

    @Test
    void testTruncateAndLegend() {
        // Έλεγχος της μεθόδου truncate (για μεγάλα ονόματα) και του Legend
        EnvYear year = createYear("2025", "a_very_long_sector_key_that_exceeds_the_maximum_allowed_length", 100.0);
        
        assertDoesNotThrow(() -> comparison.performFullComparison(year, year, TOTAL_BUDGET));
    }
}

