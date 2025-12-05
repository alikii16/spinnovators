package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import java.util.Scanner;
import java.util.List;

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
      double totalBudget = 2341227000.00;
    } else if ("2026".equals(temp)) {
      double totalBudget = 3133452000.00;
    }

    while (keepEditing) {
      System.out.println("\\nΠληκτρολογήστε την κατηγορία που θέλετε να επεξεργαστείτε ή 'ΤΕΛΟΣ' για έξοδο.");
      System.out.print("-->");
      
      String searchInput = scanner.nextLine().trim();

      if (searchInput.equalsIgnoreCase("ΤΕΛΟΣ") || searchInput.equalsIgnoreCase("TELOS")) {
        keepEditing = false;
      } else if (!searchInput.isEmpty()) {
        findAndEditCategory(year, searchInput);
      }
    }
    System.out.println("--- ΤΕΛΟΣ ΕΠΕΞΕΡΓΑΣΙΑΣ ---");
  }

  // This method searches for the category throughout the year
  private void findAndEditCategory(EnvYear year, String searchName) {
    boolean found = false;

    // A triple loop in order to search Sectors -> Units -> Entries
    for (EnvSector sector : year.getSectors()) {
      for (EnvUnit unit : sector.getUnits()) {
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
           
            try {
              double newAmount = Double.parseDouble(amountInput);
              entry.setAmount(newAmount);
              double offsetAmount = oldAmount - newAmount;
              System.out.printf(" [OK] Η τιμή άλλαξε επιτυχώς. Δημιουργήθηκε διαφορά: ", offsetAmount, " ευρώ");
            } catch (NumberFormatException e) {
              System.out.println(" Λάθος: Παρακαλώ δώστε έγκυρο αριθμό.");
            }         
          return;
          // EDO THA PERNAEI TON ELEGXO EGKYROTITAS KALONTAS THN METHODO CHRISANTHI/NIKOU
        }
        }
      }
    }

    if (!found) {
      System.out.println(" Δεν βρέθηκε κατηγορία με το όνομα: '" + searchName + "'");
      System.out.println(" Συμβουλή: Προσέξτε τους τόνους και την ορθογραφία!");
    }
  }
}
