package gr.det.spinnovators.envdatamodel;

/**
 * Represents the smallest budget entry (e.g., "personnel_costs")
 * Holds only the JSON key and the amount.
 */

public class EnvEntry {

  private final String jsonKey;   // Example: "personnel_costs"
  private double amount;    // Example: 2256000.00

  public EnvEntry(String jsonKey, double amount) {
    this.jsonKey = jsonKey;
    this.amount = amount;
  }

  public String getJsonKey() {
    return jsonKey;
  }

  public double getAmount() {
    return amount;
  }

  // Will be critically used for BudgetService updates
  public void setAmount(double amount) {
    this.amount = amount;
  }
  
  /* This method defines how the object is printed by returning 
   * a string that looks like {jsonKey: amount} whenever the 
   * object is converted to text. 
   */
  @Override
  public String toString() {
    return "{" + jsonKey + ": " + amount + "}";
  }
}
