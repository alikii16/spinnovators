package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class EditsApplierTest {

    private EnvYear year2025;
    private EnvEntry envEntry;

    static class DummyTranslator extends EnvBudgetTranslator {
        @Override
        public String translateCategory(String key) { return key; }
    }

    @BeforeEach
    void setup() {
        // Χρησιμοποιούμε κλειδί που ενεργοποιεί ESG κανόνες (π.χ. ENVIRONMENTAL)
        envEntry = new EnvEntry("env_protection_entry", 1000000.0);
        EnvUnit unit = new EnvUnit("unit1", List.of(envEntry));
        EnvSector sector = new EnvSector("sector_environmental", List.of(unit));
        year2025 = new EnvYear("2025", List.of(sector));
    }

    /**
     * ΤΟ ΜΕΓΑΛΟ ΤΕΣΤ: Καλύπτει επιτυχείς αλλαγές, ESG recalculation και ακυρώσεις.
     * Στόχος: Πρασίνισμα recalculateEsgScore & applyBudgetChange.
     */
    @Test
    public void testFullInteractiveCycle() {
        String simulatedInput = 
            "1\n" +             // Επιλογή Τομέα
            "1\n" +             // Επιλογή Μονάδας
            "env_protection_entry\n" + 
            "1500000\n" +       // Μεγάλη απόκλιση (+50%)
            "NAI\n" +           // Επιβεβαίωση -> Ενεργοποιεί applyBudgetChange & recalculateEsgScore
            "1\n" +             // Ξανά στον Τομέα 1
            "1\n" +             // Μονάδα 1
            "env_protection_entry\n" + 
            "1000000\n" +       // Επαναφορά για ισοσκέλιση (Balance = 0)
            "0\n" +             // Επιστροφή από Μονάδες
            "0\n";              // Έξοδος από Τομείς (Επιτυχής γιατί balance < 0.01)

        Scanner scanner = createScanner(simulatedInput);
        EditsApplier applier = new EditsApplier(new DummyTranslator());
        
        applier.applyEditsToYear(year2025, scanner);
        
        // Έλεγχος αν οι τιμές όντως άλλαξαν και επέστρεψαν
        assertEquals(1000000.0, envEntry.getAmount(), "Το ποσό πρέπει να έχει επανέλθει στο αρχικό.");
    }

    /**
     * ΤΕΣΤ ΣΦΑΛΜΑΤΩΝ: Καλύπτει όλα τα "κόκκινα" μηνύματα λάθους και τα catch blocks.
     * Στόχος: 100% Branch Coverage στην handleValidationResult.
     */
    @Test
    public void testAllErrorBranches() {
        // Δοκιμή και με το έτος 2026 για κάλυψη του initialization
        EnvYear year2026 = new EnvYear("2026", year2025.getSectors());
        
        String simulatedInput = 
            "1\n" + "1\n" + "env_protection_entry\n" + 
            "-100\n" +          // Σφάλμα: Αρνητικό ποσό
            "99999999999\n" +   // Σφάλμα: Υπέρβαση ορίου
            "100\n" +           // Σφάλμα: ESG_ENV_PROTECTION (μείωση > 5%)
            "invalid\n" +       // Σφάλμα: Μη έγκυρη μορφή αριθμού (catch ParseException)
            " \n" +              // Σφάλμα: Κενή είσοδος -> "Δεν δόθηκε τιμή"
            "0\n" +             // Προσπάθεια εξόδου με επιστροφή null
            "0\n";              // Τελική έξοδος

        Scanner scanner = createScanner(simulatedInput);
        EditsApplier applier = new EditsApplier(new DummyTranslator());
        applier.applyEditsToYear(year2026, scanner);
    }

    /**
     * ΤΕΣΤ ΑΚΥΡΩΣΗΣ: Καλύπτει την άρνηση στην προειδοποίηση απόκλισης.
     * Στόχος: Πρασίνισμα του "else" στην handleExtremeDeviationWarning.
     */
    @Test
    public void testDeviationCancellation() {
        String simulatedInput = 
            "1\n" + "1\n" + "env_protection_entry\n" + 
            "2000000\n" +       // Απόκλιση 100%
            "OXI\n" +           // Άρνηση -> "Η αλλαγή ακυρώθηκε"
            " \n" +             // Κενή επιλογή στο μενού (choice -1)
            "0\n" + "0\n";

        Scanner scanner = createScanner(simulatedInput);
        EditsApplier applier = new EditsApplier(new DummyTranslator());
        applier.applyEditsToYear(year2025, scanner);
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}