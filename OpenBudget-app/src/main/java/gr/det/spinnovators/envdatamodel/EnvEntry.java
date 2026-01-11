package gr.det.spinnovators.envdatamodel;

/**
 * Represents the smallest budget entry within the environmental budget structure.
 *
 * <p>Each entry corresponds to a specific budget category (e.g., "personnel_costs",
 * "operational_expenses") and holds its allocated amount. This is the atomic unit
 * of the budget hierarchy and the only mutable element, allowing for budget
 * modifications through the {@link #setAmount(double)} method.</p>
 *
 * <p>This class is mutable, allowing the amount to be updated through
 * the {@link #setAmount(double)} method, which is critical for budget
 * modification operations. All other properties are immutable to maintain
 * data integrity.</p>
 *
 * <p>Position in hierarchy: EnvYear → EnvSector → EnvUnit → <strong>EnvEntry</strong></p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see EnvUnit
 */
public class EnvEntry {

  private final String jsonKey;
  private double amount;

  /**
   * Constructs an EnvEntry with the specified JSON key and amount.
   *
   * @param jsonKey the unique identifier for this budget entry (e.g., "personnel_costs")
   * @param amount the monetary amount allocated to this entry (e.g. 2256000.00)
   */
  public EnvEntry(String jsonKey, double amount) {
    this.jsonKey = jsonKey;
    this.amount = amount;
  }

  /**
   * Retrieves the JSON key identifier for this budget entry.
   *
   * @return the JSON key string (e.g., "personnel_costs")
   */
  public String getJsonKey() {
    return jsonKey;
  }

  /**
   * Retrieves the current monetary amount allocated to this entry.
   *
   * @return the budget amount as a double
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Updates the monetary amount allocated to this entry.
   * This method is critically used by budget service update operations.
   *
   * @param amount the new budget amount to set
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }

  /**
   * Returns a string representation of this budget entry.
   * The format is: {jsonKey: amount}
   *
   * @return a formatted string containing the JSON key and amount
   */
  @Override
  public String toString() {
    return "{" + jsonKey + ": " + amount + "}";
  }
}
