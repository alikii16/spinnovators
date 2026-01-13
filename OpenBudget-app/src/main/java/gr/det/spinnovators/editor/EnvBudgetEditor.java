package gr.det.spinnovators.editor;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EditsApplier;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.util.Scanner;

/**
 * Orchestrates the interactive budget editing session for the user.
 * This class manages the user interface logic for selecting a fiscal year
 * and applying modifications to the environmental budget data.
 *
 * <p>The editor validates user input, ensures data integrity, and delegates
 * the actual editing operations to the {@link EditsApplier} service.</p>
 */
public class EnvBudgetEditor {

  private final EnvBudgetData data;
  private final EnvBudgetTranslator translator;

  /**
   * Constructs an EnvBudgetEditor with the necessary data sources and translators.
   *
   * @param data the environmental budget data repository to be edited
   * @param translator the service used to translate or format budget categories
   * @throws NullPointerException if any parameter is null
   */
  public EnvBudgetEditor(EnvBudgetData data, EnvBudgetTranslator translator) {
    if (data == null || translator == null) {
      throw new NullPointerException("Parameters cannot be null");
    }
    this.data = data;
    this.translator = translator;
  }

  /**
   * Initiates an interactive console session for budget modification.
   *
   * @param scanner the Scanner object for reading user input
   */
  public void startEditingSession(Scanner scanner) {
    System.out.printf("%n------------------------------------------------%n");
    System.out.print("Θέλετε να προχωρήσετε σε τροποποίηση του προϋπολογισμού; (ΝΑΙ/ΟΧΙ): ");
    if (!scanner.hasNextLine()) {
      return;
    }
    String answer = scanner.nextLine().trim();

    if (!answer.equalsIgnoreCase("ΝΑΙ")) {
      return;
    }

    System.out.print("Επιλέξτε το έτος που θέλετε να επεξεργαστείτε: (2025/2026): ");
    if (!scanner.hasNextLine()) {
      return;
    }
    String yearInput = scanner.nextLine().trim();
    EnvYear selectedYear = data.getBudgetForYear(yearInput);

    if (selectedYear == null || (!selectedYear.getYear().equals("2025")
          && !selectedYear.getYear().equals("2026"))) {
      System.out.println("Σφάλμα: Δεν βρέθηκαν δεδομένα για το έτος " + yearInput);
      return;
    }

    EditsApplier applier = new EditsApplier(translator, scanner);
    applier.applyEditsToYear(selectedYear, scanner);
  }
}