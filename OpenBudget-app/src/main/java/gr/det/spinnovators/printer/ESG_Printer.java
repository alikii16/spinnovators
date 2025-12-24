package gr.det.spinnovators.printer;

import java.util.Locale;

import gr.det.spinnovators.envdatamodel.ESG_Report;

/**
 * Prints formatted ESG sustainability reports to the console.
 *
 * <p>Creates visually appealing terminal output with progress bars,
 * color indicators, and improvement suggestions.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class ESG_Printer {

    private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
    private static final int BAR_WIDTH = 20;

    /**
     * Prints a complete ESG sustainability report.
     *
     * @param report The ESG report to print
     */
    public void printReport(ESG_Report report) {
        printHeader();
        printBasicInfo(report);
        printCategoryBreakdown(report);
        printOverallScore(report);
        printImprovementSuggestions(report);
        printFooter();
    }

    /**
     * Prints a comparison between two reports (before and after changes).
     *
     * @param before ESG report before changes
     * @param after ESG report after changes
     */
    public void printComparison(ESG_Report before, ESG_Report after) {
        double scoreDiff = after.getOverallScore() - before.getOverallScore();

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("        ΑΝΑΛΥΣΗ ΕΠΙΠΤΩΣΗΣ ΑΛΛΑΓΩΝ ΣΤΟ ESG");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        System.out.printf(HELLENIC_LOCALE, "ESG Score Πριν:  %.2f / 100\n",
            before.getOverallScore());
        System.out.printf(HELLENIC_LOCALE, "ESG Score Μετά:  %.2f / 100\n",
            after.getOverallScore());

        String arrow = scoreDiff > 0 ? "⬆️" : (scoreDiff < 0 ? "⬇️" : "→");
        String message = scoreDiff > 0 ? "ΒΕΛΤΙΩΣΗ" :
                        (scoreDiff < 0 ? "ΕΠΙΔΕΙΝΩΣΗ" : "ΚΑΜΙΑ ΑΛΛΑΓΗ");

        System.out.printf(HELLENIC_LOCALE, "\nΔιαφορά: %s %.2f points %s\n",
            arrow, Math.abs(scoreDiff), message);

        // Category breakdown
        System.out.println("\n┌─── Λεπτομέρειες Αλλαγών ───────────────────┐");

        printCategoryComparison(" Environmental",
            before.getEnvironmentalScore(), after.getEnvironmentalScore());
        printCategoryComparison(" Social",
            before.getSocialScore(), after.getSocialScore());
        printCategoryComparison(" Governance",
            before.getGovernanceScore(), after.getGovernanceScore());

        System.out.println("└────────────────────────────────────────────┘\n");

        // Feedback message
        if (scoreDiff > 2.0) {
            System.out.println(" Εξαιρετικά! Η αλλαγή βελτιώνει σημαντικά τη βιωσιμότητα!");
        } else if (scoreDiff > 0) {
            System.out.println(" Καλή αλλαγή! Μικρή βελτίωση στη βιωσιμότητα.");
        } else if (scoreDiff < -2.0) {
            System.out.println(" ΠΡΟΣΟΧΗ: Η αλλαγή επιδεινώνει σημαντικά τη βιωσιμότητα!");
        } else if (scoreDiff < 0) {
            System.out.println(" Η αλλαγή μειώνει ελαφρώς τη βιωσιμότητα.");
        } else {
            System.out.println(" Η αλλαγή δεν επηρεάζει το ESG score.");
        }
    }

    /**
     * Prints the header of the ESG report.
     */
    private void printHeader() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("        ESG SUSTAINABILITY REPORT");
        System.out.println("     Αξιολόγηση Βιωσιμότητας Προϋπολογισμού");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Prints basic information about the budget year and total amount.
     */
    private void printBasicInfo(ESG_Report report) {
        System.out.println("Έτος: " + report.getYear());
        System.out.printf(HELLENIC_LOCALE, "Συνολικός Προϋπολογισμός: %,.2f €\n\n",
            report.getTotalBudget());
    }

    /**
     * Prints the breakdown by ESG category with progress bars.
     */
    private void printCategoryBreakdown(ESG_Report report) {
        System.out.println("┌────────────────────────────────────────────────┐");

        printCategoryLine("Environmental (E)",
            report.getEnvironmentalAmount(),
            report.getEnvironmentalScore());

        printCategoryLine("Social (S)",
            report.getSocialAmount(),
            report.getSocialScore());

        printCategoryLine("Governance (G)",
            report.getGovernanceAmount(),
            report.getGovernanceScore());

        if (report.getNeutralAmount() > 0) {
            printCategoryLine("Neutral",
                report.getNeutralAmount(),
                (report.getNeutralAmount() / report.getTotalBudget()) * 100);
        }

        System.out.println("└────────────────────────────────────────────────┘\n");
    }

    /**
     * Prints a single category line with amount, percentage, and progress bar.
     */
    private void printCategoryLine(String label, double amount, double percentage) {
        String bar = createProgressBar(percentage);
        System.out.printf(HELLENIC_LOCALE,
            "│ %-22s %5.1f%%  %s │\n", label, percentage, bar);
        System.out.printf(HELLENIC_LOCALE,
            "│    Δαπάνες: %,18.2f €%8s│\n", amount, "");
        System.out.println("│                                                │");
    }

    /**
     * Creates a text-based progress bar.
     */
    private String createProgressBar(double percentage) {
        int filled = (int) Math.round((percentage / 100.0) * BAR_WIDTH);
        int empty = BAR_WIDTH - filled;

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        for (int i = 0; i < empty; i++) {
            bar.append("░");
        }

        return bar.toString();
    }

    /**
     * Prints the overall ESG score with rating.
     */
    private void printOverallScore(ESG_Report report) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf(HELLENIC_LOCALE,
            "  ΣΥΝΟΛΙΚΟ ESG SCORE: %.2f / 100\n", report.getOverallScore());
        System.out.printf("  Αξιολόγηση: %s (%s βιωσιμότητα)\n",
            report.getRating(),
            report.getRatingGreek());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Prints improvement suggestions based on current scores.
     */
    private void printImprovementSuggestions(ESG_Report report) {
        System.out.println("Συμβουλές Βελτίωσης:");

        if (report.getEnvironmentalScore() < 50) {
            System.out.println("  • Αυξήστε τις δαπάνες για ΑΠΕ και προστασία περιβάλλοντος");
        }

        if (report.getSocialScore() < 20) {
            System.out.println("  • Εξετάστε αύξηση των κοινωνικών παροχών");
        }

        if (report.getGovernanceScore() < 15) {
            System.out.println("  • Ενισχύστε τη διοικητική υποδομή");
        }

        if (report.getOverallScore() >= 60) {
            System.out.println(" Καλή δουλειά! Συνεχίστε την προσπάθεια!");
        }

        System.out.println();
    }

    /**
     * Prints the footer.
     */
    private void printFooter() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Prints a comparison line for a single category.
     */
    private void printCategoryComparison(String label, double before, double after) {
        double diff = after - before;
        String arrow = diff > 0 ? "⬆" : (diff < 0 ? "⬇" : "→");

        System.out.printf(HELLENIC_LOCALE,
            "│ %-20s %5.1f%% → %5.1f%% (%s%.1f%%) │\n",
            label, before, after, arrow, Math.abs(diff));
    }

    /**
     * Prints a compact summary (for use in web interface).
     */
    public void printCompactSummary(ESG_Report report) {
        System.out.printf(HELLENIC_LOCALE,
            "[ESG] Score: %.1f/100 | E: %.1f%% | S: %.1f%% | G: %.1f%% \n",
            report.getOverallScore(),
            report.getEnvironmentalScore(),
            report.getSocialScore(),
            report.getGovernanceScore(),
            report.getRatingGreek());
    }
}
