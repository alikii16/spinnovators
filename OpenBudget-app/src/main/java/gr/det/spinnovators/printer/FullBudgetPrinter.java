package gr.det.spinnovators.printer;

import gr.det.spinnovators.data.MinistryDataInput;

/**
 * Javadoc comment needed.
 */
public final class FullBudgetPrinter {

  /**
   * Creates variable data that receives the data from class MinistryDataInput.
   */
  private MinistryDataInput data;

  /**
   * Constructor for FullBudgetPrinter.
   *
   * @param dataToUse The MinistryDataInput object containing budget data.
   */
  public FullBudgetPrinter(final MinistryDataInput dataToUse) {
    this.data = dataToUse;
  }

  /**
   * Displays the full budget for the specified year.
   *
   * @param year The year for which to display the budget. (e.g. 2023, 2024, 2025).
   */
  public void showBudget(final String year) {
    // checks if the characters are the same,
    // not in the same diirection-string.
    double totalBudget;

    if ("2026".equals(year)) {

      totalBudget = 0;

      System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
      int size = this.data.getSize26();
      String[] names = this.data.getNames26();
      double[] amounts = this.data.getBudgetAmount26();

      for (int i = 0; i < size; i++) {
        // modifications for display of colon, euro sign,
        // correct display of amounts and line break
        System.out.printf("%s: %,.2f €\n", names[i], amounts[i]);
        totalBudget = totalBudget + amounts[i];
      }

      System.out.println("-------------------------");
      System.out.printf("%s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

    } else if ("2025".equals(year)) {

      totalBudget = 0;

      System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
      int size = this.data.getSize25();
      String[] names = this.data.getNames25();
      double[] amounts = this.data.getBudgetAmount25();

      for (int i = 0; i < size; i++) {
        System.out.printf(" * %-55s: %,.2f €\n", names[i], amounts[i]);
        totalBudget = totalBudget + amounts[i];
      }
      System.out.println("----------------------------");
      System.out.printf(" * %-55s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

    } else if ("2024".equals(year)) {

      totalBudget = 0;

      System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
      int size = this.data.getSize24();
      String[] names = this.data.getNames24();
      double[] amounts = this.data.getBudgetAmount24();

      for (int i = 0; i < size; i++) {
        System.out.printf(" * %-55s: %,.2f €\n", names[i], amounts[i]);
        totalBudget = totalBudget + amounts[i];
      }

      System.out.println("----------------------------");
      System.out.printf(" * %-55s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

    } else if ("2023".equals(year)) {

      totalBudget = 0;

      System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
      int size = this.data.getSize23();
      String[] names = this.data.getNames23();
      double[] amounts = this.data.getBudgetAmount23();

      for (int i = 0; i < size; i++) {
        System.out.printf(" * %-55s: %,.2f €\n", names[i], amounts[i]);
        totalBudget = totalBudget + amounts[i];
      }

      System.out.println("----------------------------");
      System.out.printf(" * %-55s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

    } else {

      System.out.println("Δεν υπάρχουν δεδομένα για το έτος " + year);

    }
  }
}
