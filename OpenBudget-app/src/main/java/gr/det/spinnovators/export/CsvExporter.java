package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Exports changes to a CSV file (Excel compatible).
 * Uses standard Java I/O (PrintWriter) as per course curriculum.
 */
public class CsvExporter implements EditedBudgetExporter {
    @Override
    public void export(List<String> changeLog, OutputStream out) {
        // Use try-with-resources for auto-closing
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            // Write BOM for Excel to recognize UTF-8 correctly
            writer.write('\ufeff');
            
            // Header
            writer.println("Περιγραφή Αλλαγής;Παλιό Ποσό;Νέο Ποσό");

            for (String entry : changeLog) {
                writer.println(entry);
            }
            writer.flush();
        }
    }
}
