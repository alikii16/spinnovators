package gr.det.spinnovators.service;

import gr.det.spinnovators.data.MinistryDataInput;

public class BudgetPercentageService {

  private MinistryDataInput dataInput;

  public BudgetPercentageService() {
    this.dataInput = new MinistryDataInput();
  }

  public BudgetPercentageService(MinistryDataInput dataInput) {
    this.dataInput = dataInput;
  }

  public void displayEnvironmentPercentage(int year) {
    String target = "Υπουργείο Περιβάλλοντος και Ενέργειας";
    double[] amounts;
    String[] names;

    switch (year) {
      case 2026:
        amounts = dataInput.getBudgetAmount26();
        names = dataInput.getNames26();
        break;
      case 2025:
        amounts = dataInput.getBudgetAmount25();
        names = dataInput.getNames25();
        break;
      case 2024:
        amounts = dataInput.getBudgetAmount24();
        names = dataInput.getNames24();
        break;
      case 2023:
        amounts = dataInput.getBudgetAmount23();
        names = dataInput.getNames23();
        break;
      default:
        System.out.println("Το έτος " + year + " δεν υποστηρίζεται.");
        return;
    }

    double totalSum = 0;
    double ministryAmount = 0;

    for (int i = 0; i < amounts.length; i++) {
      totalSum += amounts[i];
      if (names[i].equals(target)) {
        ministryAmount = amounts[i];
      }
    }

    if (totalSum > 0) {
      double percentage = (ministryAmount / totalSum) * 100;
      System.out.printf("--- Στατιστικά Έτους %d ---%n", year);
      System.out.printf("Το %s αντιπροσωπεύει το %.4f%% του κρατικού προϋπολογισμού.%n", target, percentage);
    }
  }
}
