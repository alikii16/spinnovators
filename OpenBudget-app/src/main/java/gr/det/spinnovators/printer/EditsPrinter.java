package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;

/**
 * Prints the results of the changed budget data.
 * Responsible for displaying the updated hierarchy and amounts to the console.
 */
public class EditsPrinter {

  private final EnvBudgetTranslator translator;

  /**
   * Constructs an EditsPrinter.
   *
   * @param translator The translator service for localized category names.
   */
  public EditsPrinter(EnvBudgetTranslator translator) {
    this.translator = translator;
  }

  /**
   * Prints the results of the changed years to the console.
   *
   * @param year The ministry year data to print.
   */
  public void printEditYear(EnvYear year) {

    System.out.println("ΕΝΗΜΕΡΩΜΕΝΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΓΙΑ ΤΟ ΕΤΟΣ " + year.getYear());

    for (EnvSector sector : year.getSectors()) {

      String translatedSector = translator.translateCategory(sector.getJsonKey());
      System.out.printf("ΤΟΜΕΑΣ: %s%n", translatedSector);
      double sectorTotal = 0.0;

      for (EnvUnit unit : sector.getUnits()) {
        String translatedUnit = translator.translateCategory(unit.getJsonKey());
        System.out.printf("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: %s%n", translatedUnit);

        double unitTotal = 0.0;

        for (EnvEntry entry : unit.getEntries()) {
          // SpotBugs Fix: Removed unused variable assignment
          double amount = entry.getAmount();
          
          System.out.printf(" - %s : %,.2f €%n", 
              translator.translateCategory(entry.getJsonKey()), amount);
              
          unitTotal += amount;
        }

        System.out.printf("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: %.2f €%n", unitTotal);
        sectorTotal += unitTotal;
      }

      System.out.printf("ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ %s: %.2f €%n%n",
            translatedSector, sectorTotal);
    }
  }
}
