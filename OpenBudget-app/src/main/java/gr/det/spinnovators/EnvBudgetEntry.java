package gr.det.spinnovators;

public class EnvBudgetEntry {
  private String sector;
  private String unit;
  private String costType;
  private double amount;

  private static final EnvBudgetTranslator translator = new EnvBudgetTranslator();
  
  public EnvBudgetEntry(String sector, String unit, String costType, double amount) {
    this.sector = sector;
    this.unit = unit;
    this.costType = costType;
    this.amount = amount;
  }

  public String getSector() { return sector; }
  public String getUnit() { return unit; }
  public String getCostType() { return costType; }
  public double getAmount() { return amount; }

  public void setAmount(double amount) {
    this.amount = amount;
  }
  
  @Override
  public String toString() {
    String greekSector = translator.translateCategory(this.sector);
    String greekUnit = translator.translateCategory(this.unit);
    String greekCost = translator.translateCategory(this.costType);

    return String.format(
      "--------------------------------------------------\n" +                "\" +\n" + //
      "Τομέας:   %s\n" +
      "Υπηρεσία: %s\n" +
      "Έξοδο:    %s\n" +
      "Ποσό:     %,.2f €\n" + 
      "--------------------------------------------------",
      greekSector, greekUnit, greekCost, amount);
  }
}
