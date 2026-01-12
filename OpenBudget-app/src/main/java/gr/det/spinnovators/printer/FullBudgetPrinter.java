package gr.det.spinnovators.printer;

import gr.det.spinnovators.data.MinistryDataInput;

/**
 * Formats and displays the full state budget for all Greek government ministries.
 * This utility class generates formatted budget reports for fiscal years 2023-2026,
 * including individual ministry allocations and total budget calculations.
 *
 * <p>The class provides two output modes:
 * <ul>
 *   <li>String generation for programmatic use</li>
 *   <li>Direct console output for terminal display</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public final class FullBudgetPrinter {

  private final MinistryDataInput data;

  /**
   * Constructs a FullBudgetPrinter with the specified data source.
   *
   * @param dataToUse the MinistryDataInput containing budget data for all years
   */
  public FullBudgetPrinter(final MinistryDataInput dataToUse) {
    this.data = dataToUse;
  }

  /**
   * Generates a formatted budget report string for the specified year.
   *
   * <p>The report includes:
   * <ul>
   *   <li>A header with the fiscal year</li>
   *   <li>A line for each ministry with name and budget amount</li>
   *   <li>A separator line</li>
   *   <li>The total national budget for that year</li>
   * </ul>
   * </p>
   *
   * <p>All monetary values are formatted with thousand separators and
   * two decimal places followed by the euro symbol (€).</p>
   *
   * <p>If data for the requested year is not available, an error message
   * is returned instead.</p>
   *
   * @param year the fiscal year to display (valid values: "2023", "2024", "2025", "2026")
   * @return a formatted string containing the complete budget report,
   *         or an error message if the year is invalid
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
   * Prints the budget report directly to the console for the specified year.
   *
   * <p>This is a convenience method that wraps {@link #getBudgetOutput(String)}
   * and outputs the result to System.out. It is designed for terminal compatibility
   * and interactive use.</p>
   *
   * <p>The output format is identical to {@link #getBudgetOutput(String)}.</p>
   *
   * @param year the fiscal year to display (valid values: "2023", "2024", "2025", "2026")
   */
  public void showBudget(final String year) {
    System.out.println(getBudgetOutput(year));
  }
}
