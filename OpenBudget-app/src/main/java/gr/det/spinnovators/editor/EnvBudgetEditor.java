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
  * @param scanner the Scanner object for reading user input
  * @throws NullPointerException if any parameter is null
 * @param translator The service used to translate or format budget categories.
 */

  public EnvBudgetEditor(EnvBudgetData data, EnvBudgetTranslator translator) {
    this.data = data;
    this.translator = translator;
  }

  /**
   * Initiates an interactive console session for budget modification.
   * The method prompts the user for confirmation, validates the selected fiscal year,
   * triggers the editing logic, and finally displays the updated results.
   * 
   * <p>If the user declines or provides invalid input, the method returns
   * without making any changes.</p>
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

    // Choosing the year to edit
    System.out.print("Επιλέξτε το έτος που θέλετε να επεξεργαστείτε: (2025/2026): ");
    if (!scanner.hasNextLine()) {
      return;
    }
    String yearInput = scanner.nextLine().trim();
    EnvYear selectedYear = data.getBudgetForYear(yearInput);

    // Fixed logic check for consistency
    if (selectedYear == null || (!selectedYear.getYear().equals("2025")
          && !selectedYear.getYear().equals("2026"))) {
      System.out.println("Σφάλμα: Δεν βρέθηκαν δεδομένα για το έτος " + yearInput);
      return;
    }

    EditsApplier applier = new EditsApplier(translator, scanner);
    applier.applyEditsToYear(selectedYear);
  }
}
