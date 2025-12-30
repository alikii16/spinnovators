package gr.det.spinnovators.export;

import java.io.OutputStream;
import java.util.List;

/**
 * Interface for exporting budget change reports.
 * Uses the Strategy Pattern to allow different export formats (CSV, TXT).
 */
public interface EditedBudgetExporter {
    void export(List<String> changeLog, OutputStream out) throws Exception;
}
