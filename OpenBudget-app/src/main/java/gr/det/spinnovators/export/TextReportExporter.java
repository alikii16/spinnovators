package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports a formatted text report resembling an official government document.
 * This class parses the raw log strings to display hierarchical data 
 * (Year -> Sector -> Unit -> Category) with visual alignment using ASCII characters.
 */

public class TextReportExporter implements EditedBudgetExporter {
  /**
   * Generates a structured text report and writes it to the output stream.
   *
   * @param changeLog The list of change records containing raw data separated by semicolons.
   * 
   * @param out       The destination output stream.
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
            writer.printf(" %d. %s > %s%n", (i + 1), sector, unit);
             
            // Line 2: Specific Category change with arrows
            writer.printf("     └── %-30s :  %,14.2f €  --->  %,14.2f €  (%s%,.2f €)%n", 
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
   * Appends "..." if the text is truncated.
   *
   * @param text      The input string to shorten.
   * 
   * @param maxLength The maximum allowed characters.
   * 
   * @return The shortened string or the original if it fits within maxLength.
   */
  private String shorten(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength - 3) + "...";
  }
}
