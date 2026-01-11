package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Exports a formatted text report resembling an official government document.
 * This class parses the raw log strings to display hierarchical data
 * (Year → Sector → Unit → Category) with visual alignment using ASCII characters.
 *
 * <p>The generated report includes a formal header with ministry information,
 * the budget year, and a detailed list of all budget modifications made during
 * the editing session.</p>
 *
 * <p>Each change entry is expected to contain semicolon-separated values in the format:
 * Year;Sector;Unit;Category;OldValue;NewValue</p>
 */
public class TextReportExporter implements EditedBudgetExporter {

  /**
   * Generates a structured text report and writes it to the output stream.
   *
   * <p>The report structure includes:
   * <ul>
   *   <li>Official header with ministry name and report title</li>
   *   <li>Metadata: issue date, budget year, and approval status</li>
   *   <li>Detailed list of changes with hierarchical formatting</li>
   *   <li>Visual indicators showing old values, new values, and differences</li>
   * </ul>
   * </p>
   *
   * <p>If the change log is empty or null, the report indicates that no changes
   * were made during the session.</p>
   *
   * @param changeLog the list of change records containing raw data separated by semicolons,
   *                  expected format: Year;Sector;Unit;Category;OldValue;NewValue
   * @param out the destination output stream where the report will be written
   */
  @Override
  public void export(List<String> changeLog, OutputStream out) {
    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,
        StandardCharsets.UTF_8))) {

      // Extract the budget year from the first entry if available
      String budgetYear = "----";
      if (changeLog != null && !changeLog.isEmpty()) {
        String firstEntry = changeLog.get(0);
        String[] parts = firstEntry.split(";");
        if (parts.length > 0) {
          budgetYear = parts[0];
        }
      }

      // Print Official Header
      writer.println("=========================================================================");
      writer.println("                   ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ                   ");
      writer.println("                   ΑΝΑΦΟΡΑ ΤΡΟΠΟΠΟΙΗΣΗΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ                   ");
      writer.println("=========================================================================");
      writer.println();
      writer.printf(" Ημερομηνία Έκδοσης: %s%n", LocalDateTime.now().format(DateTimeFormatter
          .ofPattern("dd/MM/yyyy HH:mm")));
      writer.printf(" Οικονομικό Έτος:    %s%n", budgetYear);
      writer.println(" Κατάσταση:          ΕΓΚΡΙΘΗΚΕ");
      writer.println();
      writer.println("----------------------------- ΛΙΣΤΑ ΑΛΛΑΓΩΝ -----------------------------");
      writer.println();

      if (changeLog == null || changeLog.isEmpty()) {
        writer.println("   (Δεν πραγματοποιήθηκαν αλλαγές σε αυτή τη συνεδρία)");
      } else {
        for (int i = 0; i < changeLog.size(); i++) {
          String entry = changeLog.get(i);
          String[] parts = entry.split(";");

          // Ensure we have all 6 fields: Year;Sector;Unit;Category;Old;New
          if (parts.length >= 6) {
            String sector = parts[1];
            String unit = parts[2];
            String category = parts[3];

            double oldVal = Double.parseDouble(parts[4]);
            double newVal = Double.parseDouble(parts[5]);
            double diff = newVal - oldVal;
            String sign = diff > 0 ? "+" : "";

            // Line 1: Hierarchy (Sector > Unit)
            writer.printf(Locale.US, " %d. %s > %s%n", (i + 1), sector, unit);

            // Line 2: Specific Category change with arrows
            writer.printf(Locale.US, "     └── %-30s :  %,14.2f €  --->  %,14.2f €  (%s%,.2f €)%n",
                shorten(category, 30), oldVal, newVal, sign, diff);

            writer.println("----------------------------------------------------------------"
                + "---------");
          } else if (parts.length >= 3) {
            // Fallback for older data format
            writer.println(" " + entry);
          }
        }
      }
    }
  }

  /**
   * Helper method to shorten text that exceeds a maximum length.
   * Appends "..." if the text is truncated to indicate omitted content.
   *
   * <p>This method is used to ensure that category names fit within the
   * report's column width constraints while maintaining readability.</p>
   *
   * @param text the input string to potentially shorten
   * @param maxLength the maximum allowed characters including the ellipsis
   * @return the shortened string with "..." appended if truncated,
   *         or the original string if it fits within maxLength
   */
  private String shorten(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength - 3) + "...";
  }
}
