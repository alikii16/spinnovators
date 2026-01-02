package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.util.List;

/**
 * Interface for exporting budget change reports.
 *
 * <p>This interface follows the Strategy Pattern, allowing the application
 * to switch between different export formats (e.g., CSV, TXT) dynamically.
 */
public interface EditedBudgetExporter {

  /**
   * Exports the list of budget changes to the specified output stream.
   *
   * @param changeLog A list of strings, where each string represents a change record.
   * The expected format is: "Year;Sector;Unit;Category;OldAmount;NewAmount".
   *
   * @param out The {@link OutputStream} where the report will be written.
   * @throws Exception If an Input/Output error occurs during the export process.
   */
  void export(List<String> changeLog, OutputStream out) throws Exception;
}
