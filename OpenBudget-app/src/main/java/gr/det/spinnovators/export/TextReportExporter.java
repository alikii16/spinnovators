package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports a formatted text report resembling an official document.
 */
public class TextReportExporter implements EditedBudgetExporter {
    @Override
    public void export(List<String> changeLog, OutputStream out) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            
            writer.println("==============================================================");
            writer.println("             ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ            ");
            writer.println("          ΕΠΙΣΗΜΗ ΑΝΑΦΟΡΑ ΤΡΟΠΟΠΟΙΗΣΗΣ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ         ");
            writer.println("==============================================================");
            writer.println();
            writer.println("Ημερομηνία: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            writer.println();
            writer.println("------------------------- ΛΙΣΤΑ ΑΛΛΑΓΩΝ ----------------------");
            writer.println();

            if (changeLog == null || changeLog.isEmpty()) {
                writer.println("   (Δεν πραγματοποιήθηκαν αλλαγές σε αυτή τη συνεδρία)");
            } else {
                for (int i = 0; i < changeLog.size(); i++) {
                    writer.printf("%d. %s%n", i + 1, changeLog.get(i));
                }
            }
        }
    }
}
