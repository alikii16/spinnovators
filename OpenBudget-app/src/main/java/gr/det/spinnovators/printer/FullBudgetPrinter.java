package gr.det.spinnovators.printer;

import gr.det.spinnovators.data.MinistryDataInput;

/**
 * Formats the full state budget.
 */
public final class FullBudgetPrinter {

  private final MinistryDataInput data;

  public FullBudgetPrinter(final MinistryDataInput dataToUse) {
    this.data = dataToUse;
  }

  /**
   * Generates the full budget string for the specified year.
   *
   * @param year The year to display.
   * @return Formatted string.
   */
  public String getBudgetOutput(final String year) {
    StringBuilder sb = new StringBuilder();
    double totalBudget = 0;
    int size;
    String[] names;
    double[] amounts;

    if ("2026".equals(year)) {
      size = this.data.getSize26();
      names = this.data.getNames26();
      amounts = this.data.getBudgetAmount26();
    } else if ("2025".equals(year)) {
      size = this.data.getSize25();
      names = this.data.getNames25();
      amounts = this.data.getBudgetAmount25();
    } else if ("2024".equals(year)) {
      size = this.data.getSize24();
      names = this.data.getNames24();
      amounts = this.data.getBudgetAmount24();
    } else if ("2023".equals(year)) {
      size = this.data.getSize23();
      names = this.data.getNames23();
      amounts = this.data.getBudgetAmount23();
    } else {
      return "Δεν υπάρχουν δεδομένα για το έτος " + year;
    }

    // Χρήση %n για συμβατότητα με SpotBugs
    sb.append(String.format("%n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ %s ---%n", year));

    for (int i = 0; i < size; i++) {
      sb.append(String.format(" * %-55s: %,.2f €%n", names[i], amounts[i]));
      totalBudget += amounts[i];
    }

    sb.append("----------------------------\n");
    sb.append(String.format(" * %-55s: %,.2f €%n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget));

    return sb.toString();
  }

  /**
   * Helper method for Terminal compatibility.
   * PRINTS the budget to the console.
   */
  public void showBudget(final String year) {
    System.out.println(getBudgetOutput(year));
  }
}
