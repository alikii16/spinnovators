package gr.det.spinnovators.service;

import java.util.List;
import java.util.Scanner;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;

public class EditsApplier {
  private final EnvBudgetTranslator translator;
  private final Scanner scanner;

  //they preserve the data during the changes
  private double currentBalance = 0;
  private double totalBudget = 0;

  public EditsApplier(EnvBudgetTranslator translator) {
    this.translator = translator;
    this.scanner = new Scanner(System.in);
  }

  public void applyEditsToYear(EnvYear year) {
    boolean keepEditing = true;

    System.out.println("\n--- ΕΝΑΡΞΗ ΕΠΕΞΕΡΓΑΣΙΑΣ ΓΙΑ ΤΟ ΕΤΟΣ " + year.getYear() + " ---");

    String temp = year.getYear();

    if ("2025".equals(temp)) {
      this.totalBudget = 2341227000.00;
    } else if ("2026".equals(temp)) {
      this.totalBudget = 3133452000.00;
    }

    this.currentBalance = 0.0;

    while (keepEditing) {
      if (Math.abs(currentBalance) > 0.01) {
        System.out.printf("\n>>> ΥΠΟΛΟΙΠΟ ΓΙΑ ΙΣΟΣΚΕΛΙΣΜΟ: %,.2f € <<<", this.currentBalance);
      }

      EnvSector selectedSector = selectSector(year);

      if (selectedSector == null) {
        if (Math.abs(this.currentBalance) < 0.01) {
          System.out.println("Ο προυπολογισμός είναι ισοσκελισμένος!");
          System.out.println("Τερματισμός Λειτουργίας.");
          keepEditing = false;
        } else {
          System.out.println("!!! ΠΡΟΣΟΧΗ !!!");
          System.out.println("Δεν επιτρέπεται τερματισμός.");
          System.out.println("Ο προϋπολογισμός δεν ισοσκελίστηκε.");
          System.out.printf("Πρέπει να καλύψετε διαφορά: %,.2f €\n", currentBalance);
        }
        continue;
      }

      EnvUnit selectedUnit = selectUnit(selectedSector);
      if (selectedUnit == null) {
        continue;
      }

      System.out.println("\n------------------------------------------------");
      System.out.println("Μονάδα: " + translator.translateCategory(selectedUnit.getJsonKey()));
      System.out.println("Πληκτρολογήστε το όνομα της κατηγορίας που θέλετε να επεξεργαστείτε:");
      System.out.print("--> ");

      String searchInput = scanner.nextLine().trim();

      if (!searchInput.isEmpty()) {
        findAndEditEntryInUnit(selectedUnit, searchInput);
      }

      System.out.println("--- ΤΕΛΟΣ ΕΠΕΞΕΡΓΑΣΙΑΣ ---");

    }
  }

  private EnvSector selectSector(EnvYear year) {
    List<EnvSector> sectors = year.getSectors();
    System.out.println("\n==========================================");
    System.out.println(" ΕΠΙΛΟΓΗ ΤΟΜΕΑ");
    System.out.println("==========================================");

    for (int i = 0; i < sectors.size(); i++) {
      String name = translator.translateCategory(sectors.get(i).getJsonKey());
      System.out.println((i + 1) + ". " + name);
    }

    System.out.println("0. ΤΕΛΟΣ / ΕΛΕΓΧΟΣ ΙΣΟΣΚΕΛΙΣΜΟΥ");
    System.out.print("--> Επιλογή: ");

    int choice = readIntegerChoice(sectors.size());
    if (choice <= 0) return null;
    return sectors.get(choice - 1);
  }

  private EnvUnit selectUnit(EnvSector sector) {
    List<EnvUnit> units = sector.getUnits();
    System.out.println("\n--- Επιλογή Μονάδας ---");

    for (int i = 0; i < units.size(); i++) {
      String name = translator.translateCategory(units.get(i).getJsonKey());
      System.out.println((i + 1) + ". " + name);
    }
    System.out.println("0. Επιστροφή");
    System.out.print("--> Επιλογή: ");

    int choice = readIntegerChoice(units.size());
    if (choice <= 0) return null;
    return units.get(choice - 1);
  }


  // This method searches for the category in the specific unit
  private void findAndEditEntryInUnit(EnvUnit unit, String searchName) {
    boolean found = false;

    // A loop in order to search the Unit and do the entry
    for (EnvEntry entry : unit.getEntries()) {

      // Translation of the key in order to compare with the user's entry
      String entryName = translator.translateCategory(entry.getJsonKey());

      if (entryName.equalsIgnoreCase(searchName)) {
        found = true;

        // Asking for the new amount
        System.out.printf("\nΒρέθηκε: %s | Τρέχον Ποσό: %,.2f €\n", entryName, entry.getAmount());
        double oldAmount = entry.getAmount();
        System.out.print("Δώσε το νέο ποσό: ");

        String amountInput = scanner.nextLine().trim();

        if (!amountInput.isEmpty()) {
          try {
            amountInput = amountInput.replace(",", ".");
            double newAmount = Double.parseDouble(amountInput);
            entry.setAmount(newAmount);

            //Check new amount's validation
            BudgetValidator obj = new BudgetValidator();
            newAmount = obj.getValidatedNewValue(totalBudget, oldAmount, newAmount);

            double offsetAmount = oldAmount - newAmount;
            //Current balance correction
            this.currentBalance += offsetAmount;
            System.out.printf(" [OK] Η τιμή άλλαξε επιτυχώς. Δημιουργήθηκε διαφορά: %,.2f €\\n", offsetAmount);
          } catch (NumberFormatException e) {
            System.out.println(" Λάθος: Παρακαλώ δώστε έγκυρο αριθμό.");
          }
        } else {
          System.out.println(" Δεν δόθηκε τιμή. Καμία αλλαγή.");
        }
        return;
      }
    }

    if (!found) {
      System.out.println(" Δεν βρέθηκε κατηγορία με το όνομα: '" + searchName + "'");
      System.out.println(" Συμβουλή: Προσέξτε τους τόνους και την ορθογραφία!");
    }
  }

  private int readIntegerChoice(int maxOption) {
    try {
      String input = scanner.nextLine().trim();
      if (input.isEmpty()) return -1;
      int val = Integer.parseInt(input);
      if (val >= 0 && val <= maxOption) return val;
    } catch (NumberFormatException e) {
      // We ignore the mistake and return -1
    }
    System.out.println("Μη έγκυρη επιλογή.");
    return -1;
  }
}
