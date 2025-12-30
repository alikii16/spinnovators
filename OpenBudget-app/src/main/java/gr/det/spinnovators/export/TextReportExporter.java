package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TextReportExporter implements EditedBudgetExporter {
    @Override
    public void export(List<String> changeLog, OutputStream out) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            
            writer.println("==================================================================================");
            writer.println("                     ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ                        ");
            writer.println("                      ΑΝΑΦΟΡΑ ΤΡΟΠΟΠΟΙΗΣΗΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ                         ");
            writer.println("==================================================================================");
            writer.println();
            writer.printf(" Ημερομηνία Έκδοσης: %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            writer.println(" Κατάσταση:          ΕΓΚΡΙΘΗΚΕ");
            writer.println();
            writer.println("-------------------------------- ΛΙΣΤΑ ΑΛΛΑΓΩΝ -----------------------------------");
            writer.println();
            
            writer.printf(" %-4s | %-40s | %15s | %-5s | %15s | %15s%n", 
                "A/A", "ΚΑΤΗΓΟΡΙΑ", "ΠΑΛΙΟ ΠΟΣΟ", "", "ΝΕΟ ΠΟΣΟ", "ΔΙΑΦΟΡΑ");
            writer.println("----------------------------------------------------------------------------------");

            if (changeLog == null || changeLog.isEmpty()) {
                writer.println("   (Δεν πραγματοποιήθηκαν αλλαγές σε αυτή τη συνεδρία)");
            } else {
                for (int i = 0; i < changeLog.size(); i++) {
                    String entry = changeLog.get(i);
                    String[] parts = entry.split(";");
                    
                    if (parts.length >= 3) {
                        String name = parts[0];
                        double oldVal = Double.parseDouble(parts[1].replace(",", "."));
                        double newVal = Double.parseDouble(parts[2].replace(",", "."));
                        double diff = newVal - oldVal;
                        String sign = diff > 0 ? "+" : "";

                        writer.printf(" %-4d | %-40s | %,14.2f € | --->  | %,14.2f € | (%s%,.2f €)%n", 
                                      i + 1, shorten(name, 40), oldVal, newVal, sign, diff);
                    } else {
                        writer.println(entry);
                    }
                }
            }
        }
    }

    private String shorten(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}