package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import java.util.Scanner;

public class EnvBudgetEditor {

  private final EnvBudgetData data;
  private final EnvBudgetTranslator translator;
  private final Scanner scanner;

  public EnvBudgetEditor(EnvBudgetData data, EnvBudgetTranslator translator) {
    this.data = data;
    this.translator = translator;
    this.scanner = new Scanner(System.in);
  }

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
  }
}
