package gr.det.spinnovators.editor;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.printer.EditsPrinter;
import gr.det.spinnovators.service.EditsApplier;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Javadoc comment needed.
 */

public class EnvBudgetEditor {

  private final EnvBudgetData data;
  private final EnvBudgetTranslator translator;
  private final Scanner scanner;

  /**
   * Javadoc comment needed.
   * 
   * @param data
   * 
   * @param translator
   */

  public EnvBudgetEditor(EnvBudgetData data, EnvBudgetTranslator translator) {
    this.data = data;
    this.translator = translator;
    this.scanner = new Scanner(System.in);
  }

  /**
   * Javadoc comment needed
   */

  public void startEditingSession() {
    System.out.println("\n------------------------------------------------");
    System.out.print("Θέλετε να προχωρήσετε σε τροποποίηση του προϋπολογισμού; (ΝΑΙ/ΟΧΙ): ");
    String answer = scanner.nextLine().trim();

    if (!answer.equalsIgnoreCase("ΝΑΙ")) {
      return;
    }

    // Choosing the year to edit
    System.out.print("Επιλέξτε το έτος που θέλετε να επεξεργαστείτε: (2025/2026): ");
    String yearInput = scanner.nextLine().trim();
    EnvYear selectedYear = data.getBudgetForYear(yearInput);

    if ((!selectedYear.equals("2025")) && (!selectedYear.equals("2026"))) {
      System.out.println("Σφάλμα: Δεν βρέθηκαν δεδομένα για το έτος " + yearInput);
      return;
    }

    EditsApplier applier = new EditsApplier(translator);
    applier.applyEditsToYear(selectedYear);

    EditsPrinter printer = new EditsPrinter(translator, yearInput);
    printer.printEditYear(selectedYear);
  }
}
