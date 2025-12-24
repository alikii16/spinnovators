package gr.det.spinnovators.service;

import java.util.Scanner;


/**
 * Class for validating changes to the budget according to specific constraints.
 * Applies checks for:
 * - Empty input
 * - Negative values
 * - Values exceeding the total ministry budget
 * - Extreme deviations (>30%)
 */
public class BudgetValidator {

  private final Scanner scanner;


  /**
   * Constructor - creates a new BudgetValidator with its own Scanner
   */
  public BudgetValidator() {
    this.scanner = new Scanner(System.in);
  }

  /**
   * Validates a new budget value.
   * If validation fails, requests a new value from the user until a valid value is entered.
   *
   * @param totalBudget Total budget of the ministry for the current year
   * 
   * @param oldAmount Existing (old) value of the category
   * 
   * @param newAmount Initial new value entered by the user
   * 
   * @return The validated new value after all checks
   */
  public double getValidatedNewValue(double totalBudget, double oldValue, double initialNewValue) {
    double currentValue = initialNewValue;
    //ignore in frontend
    System.out.printf("Παλιά τιμή: %,.2f € | Συνολικός προϋπολογισμός Υπουργείου: %,.2f €\n\n",
      oldValue, totalBudget);

    while (true) {
      // CHECK 1: Negative value
      if (currentValue < 0) {
        System.out.println("ΣΦΑΛΜΑ: Η τιμή δεν μπορεί να είναι αρνητική.");
        currentValue = getNewInputFromUser();
        continue;
      }

      // CHECK 2: Value exceeds total budget
      if (currentValue > totalBudget) {
        System.out.printf("ΣΦΑΛΜΑ: Η τιμή (%,.2f €) υπερβαίνει τον συνολικό προϋπολογισμό του Υπουργείου (%,.2f €).\n",
          currentValue, totalBudget);
        currentValue = getNewInputFromUser();
        continue;
      }

      // CHECK 3: Extreme deviation (> 30%)
      if (isExtremeDeviation(oldValue, currentValue)) {
        double deviation = calculateDeviationPercentage(oldValue, currentValue);

        System.out.println("\n==============================================");
        System.out.println("ΠΡΟΕΙΔΟΠΟΙΗΣΗ: ΑΚΡΑΙΑ ΑΛΛΑΓΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ");
        System.out.println("==============================================");
        System.out.printf("Ποσοστιαία μεταβολή: %6.2f%%\n", deviation);
        System.out.println("Η μεταβολή υπερβαίνει το όριο του 30%!");
        System.out.println("==============================================\n");

        System.out.print("Είστε βέβαιος ότι θέλετε να εφαρμόσετε αυτή την τιμή; (ΝΑΙ/ΟΧΙ): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("ΝΑΙ")) {
          System.out.println("Επιβεβαίωση: Η τιμή θα εφαρμοστεί.\n");
          return currentValue;
        } else if (confirmation.equals("ΟΧΙ")) {
          System.out.println("Ακύρωση: Παρακαλώ εισάγετε νέα τιμή.\n");
          currentValue = getNewInputFromUser();
          continue;
        } else {
          System.out.println("Μη έγκυρη απάντηση. Παρακαλώ πληκτρολογήστε 'ΝΑΙ' ή 'ΟΧΙ'.\n");
          currentValue = getNewInputFromUser();
          continue;
        }
      }

      // Value passed all checks successfully
      System.out.println("Η τιμή επικυρώθηκε επιτυχώς!\n");
      return currentValue;
    }
  }

  /**
   * Prompts the user to enter a new value and handles input errors.
   * Checks for empty or non-numeric input.
   *
   * @return A valid numeric value (double)
   */
  private double getNewInputFromUser() {
    while (true) {
      System.out.print("Παρακαλώ εισάγετε νέα τιμή προϋπολογισμού: ");
      String input = scanner.nextLine().trim();

      // Check for empty input
      if (input.isEmpty()) {
        System.out.println("ΣΦΑΛΜΑ: Η τιμή δεν μπορεί να είναι κενή.\n");
        continue;
      }

      // Attempt to convert to a number
      try {
        double value = Double.parseDouble(input);
        return value;
      } catch (NumberFormatException e) {
        System.out.println("ΣΦΑΛΜΑ: Μη έγκυρη μορφή αριθμού. Παρακαλώ προσπαθήστε ξανά.\n");
      }
    }
  }

  /**
   * Checks if the deviation between old and new values is extreme (>30%).
   * 
   * @param oldValue Old value
   * 
   * @param newValue New value
   * 
   * @return true if deviation > 30%, false otherwise
   */
  private boolean isExtremeDeviation(double oldValue, double newValue) {
  // Calculate percentage deviation
    double percentageDeviation = Math.abs((newValue - oldValue) / oldValue) * 100.0;
    return percentageDeviation > 30.0;
  }

  /**
   * Calculates the percentage deviation between old and new values!
   * 
   * @param oldValue Old value
   * 
   * @param newValue New value
   * 
   * @return Deviation percentage (e.g., 25.5 for 25.5%)
   */
  private double calculateDeviationPercentage(double oldValue, double newValue) {

    return Math.abs((newValue - oldValue) / oldValue) * 100.0;
  }

  /**
   * Closes the Scanner when no longer needed.
   */
  public void closeScanner() {
    if (this.scanner != null) {
      this.scanner.close();
    }
  }
}
