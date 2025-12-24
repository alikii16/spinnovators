package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;

import gr.det.spinnovators.service.*;


/** PRINTS THE RESULTS OF CHANGED DATA. */

public class EditsPrinter {
  private final EnvBudgetTranslator translator;
  private final String yearForPrinting;

  public EditsPrinter(EnvBudgetTranslator translator, String yearForPrinting) {
    this.translator  = translator;
    this.yearForPrinting = yearForPrinting;
  }

  /** PRINTS THE RESULTS OF THE CHANGED YEARS.
   *
   * @param year the ministry year data to print.
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
          String translatedEntry = translator.translateCategory(entry.getJsonKey());
          double amount = entry.getAmount();
          unitTotal += amount;
        }

        System.out.printf("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: %.2f%n", unitTotal);
        sectorTotal += unitTotal;
      }

      System.out.printf("ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ %s: %.2f%n%n",
            translatedSector, sectorTotal);
    }
  }
}
