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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Unit tests for InitialBudgetComparison.
 * Uses Recovery Fund logic to boost scores above thresholds.
 */
class InitialBudgetComparisonTest {

    private InitialBudgetComparison comparison;
    private EnvBudgetTranslator translator;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws UnsupportedEncodingException {
        Locale.setDefault(Locale.forLanguageTag("el-GR"));
        System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8.name()));
        
        translator = new EnvBudgetTranslator() {
            @Override
            public String translateCategory(String key) {
                return "Category_" + key;
            }
        };
        comparison = new InitialBudgetComparison(translator);
    }

    /**
     * Test 1: Excellent ESG Score (>60).
     * STRATEGY: Activate "Recovery Fund" logic by using a special Unit Name.
     * Combined with the Energy sector (high impact), this should maximize the score.
     */
    @Test
    void testExcellentEsgAndBalancedBudget() {
        List<EnvSector> sectors = new ArrayList<>();
        
        // Χρησιμοποιούμε "Energy" τομέα + "Recovery Fund" Unit για μέγιστο boost
        // Unit name must contain "recovery_and_resilience" to trigger high-priority logic
        sectors.add(createSpecialSector("energy_and_mineral_resources_management", 
                                      "recovery_and_resilience_unit", 
                                      "green_investments", 
                                      500000.0));

        EnvYear original = new EnvYear("2025", sectors);
        EnvYear modified = new EnvYear("2025", sectors);

        comparison.performFullComparison(original, modified, 500000.0);

        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Ο προϋπολογισμός είναι πλήρως ισοσκελισμένος"), "Balanced budget check");
        
        // Ελέγχουμε αν πετύχαμε το στόχο. Αν αποτύχει, τυπώνει το πραγματικό output για debug.
        if (!output.contains("Εξαιρετική κατανομή") && !output.contains("Καλή κατανομή")) {
             // Fallback assertion: Αν όντως η κλάση έχει ταβάνι το 40, ελέγχουμε ότι ΤΟΥΛΑΧΙΣΤΟΝ τυπώθηκε το report.
             // Αλλά προσπαθούμε για το High Score.
             System.out.println("DEBUG Output for Excellent Test:\n" + output);
        }
        
        // Ελέγχουμε για High Score (ή έστω το καλύτερο δυνατό μήνυμα που δείχνει βελτίωση)
        boolean isHigh = output.contains("Εξαιρετική κατανομή") || output.contains("Καλή κατανομή");
        assertTrue(isHigh, "Expected High ESG score (>60) using Recovery Fund logic.");
    }

    /**
     * Test 2: Middle ESG Score (40-60).
     * STRATEGY: 100% "Natural Environment" via standard budget (no Recovery Fund).
     * Based on logs, standard Env gives ~40 points. This sits right on the edge.
     * We add a small amount of Recovery Fund to push it safely into the 40-60 range.
     */
    @Test
    void testMiddleEsgScore() {
        List<EnvSector> sectors = new ArrayList<>();
        
        // 90% Standard Environment (Base Score ~36)
        sectors.add(createSpecialSector("natural_environment_and_water_protection", 
                                      "standard_unit", 
                                      "preservation", 
                                      90000.0));
        
        // 10% Recovery Fund Energy (Boost Score)
        sectors.add(createSpecialSector("energy_and_mineral_resources_management", 
                                      "recovery_and_resilience_unit", 
                                      "green_energy", 
                                      10000.0));
        
        EnvYear year = new EnvYear("2025", sectors);
        
        comparison.performFullComparison(year, year, 100000.0);
        
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        
        boolean hitMiddle = output.contains("Καλή κατανομή με περιθώρια βελτίωσης") || 
                            output.contains("Συνεχίστε να επενδύετε");
                            
        assertTrue(hitMiddle, "Should hit the middle ground ESG score (40-60).");
    }

    /**
     * Test 3: Low ESG (<40).
     */
    @Test
    void testLowEsgAndSocialScores() {
        EnvYear year = createYear("2025", "executive_coordination_and_investments", 100000.0);
        comparison.performFullComparison(year, year, 100000.0);
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        
        assertTrue(output.contains("Χρειάζεται περισσότερη έμφαση στη βιωσιμότητα"));
        assertTrue(output.contains("Οι κοινωνικές δαπάνες είναι χαμηλές"));
    }

    @Test
    void testZeroOriginalAmount() {
        EnvYear original = createYear("2025", "new_sector", 0.0);
        EnvYear modified = createYear("2025", "new_sector", 1000.0);
        comparison.performFullComparison(original, modified, 1000.0);
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("0,0%"));
    }

    @Test
    void testMultipleChanges() {
        List<EnvSector> origSectors = new ArrayList<>();
        List<EnvSector> modSectors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            origSectors.add(createSpecialSector("sector_inc_" + i, "u", "e", 100.0));
            modSectors.add(createSpecialSector("sector_inc_" + i, "u", "e", 200.0));
            origSectors.add(createSpecialSector("sector_dec_" + i, "u", "e", 200.0));
            modSectors.add(createSpecialSector("sector_dec_" + i, "u", "e", 100.0));
        }
        EnvYear original = new EnvYear("2025", origSectors);
        EnvYear modified = new EnvYear("2025", modSectors);
        comparison.performFullComparison(original, modified, 10000.0);
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Μεγαλύτερες Αυξήσεις"));
        assertTrue(output.contains("Μεγαλύτερες Μειώσεις"));
    }

    @Test
    void testTruncateAndLegend() {
        EnvYear year = createYear("2025", "very_long_sector_name_that_exceeds_limits_for_testing_truncation_logic_abcdefg", 100.0);
        assertDoesNotThrow(() -> comparison.performFullComparison(year, year, 100.0));
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("..."));
    }

    // --- Helper Methods ---

    // Legacy helper
    private EnvYear createYear(String yearName, String sectorKey, double amount) {
        return new EnvYear(yearName, List.of(createSpecialSector(sectorKey, "unit_1", "entry_1", amount)));
    }

    // Advanced Helper: Allows setting Unit Name (Crucial for Recovery Fund logic)
    private EnvSector createSpecialSector(String sectorKey, String unitKey, String entryKey, double amount) {
        List<EnvEntry> entries = new ArrayList<>();
        entries.add(new EnvEntry(entryKey, amount));
        
        List<EnvUnit> units = new ArrayList<>();
        units.add(new EnvUnit(unitKey, entries));
        
        return new EnvSector(sectorKey, units);
    }
}
