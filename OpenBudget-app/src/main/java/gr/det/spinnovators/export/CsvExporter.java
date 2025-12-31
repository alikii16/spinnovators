package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Exports budget changes to a CSV file (Excel compatible).
 * Uses standard Java I/O classes ({@link PrintWriter}) and UTF-8 encoding
 * with a BOM (Byte Order Mark) to ensure correct display of Greek characters in Excel.
 */
public class CsvExporter implements EditedBudgetExporter {
  /**
   * Writes the change log to the output stream in CSV format.
   *
   * @param changeLog The list of change records to export.
   * 
   * @param out       The destination output stream.
   */
  @Override
  public void export(List<String> changeLog, OutputStream out) {
    // Use try-with-resources for auto-closing
    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, 
      StandardCharsets.UTF_8))) {
      // Write BOM so Excel recognizes UTF-8 correctly
      writer.write('\ufeff');
            
      // Header matching the 6 data fieldsS
      writer.println("Περιγραφή Αλλαγής;Παλιό Ποσό;Νέο Ποσό");

      for (String entry : changeLog) {
        writer.println(entry);
      }
      writer.flush();
    }
  }
}
