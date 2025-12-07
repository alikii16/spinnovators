package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.EnvBudgetTranslator;

/** PRINTS THE RESULTS OF CHANGED DATA */

public class EditsPrinter {
  private final EnvBudgetTranslator translator;
  private final int yearForPrinting;
    
  public EditsPrinter(EnvBudgetTranslator translator, int yearForPrinting) {
    this.translator  = translator;
    this.yearForPrinting = yearForPrinting;
  }

  /** PRINTS THE RESULTS OF THE CHANGED YEARS */
  public void printEditYear(EnvYear year) {
    if (Integer.parseInt(year.getYear()) != this.yearForPrinting) {
      System.out.println("Το έτος " + this.yearForPrinting
          + "δεν ταιριάζει με το φορτωμένα δεδομένα");
      return;
    }
    
    System.out.println("ΕΝΗΜΕΡΩΜΕΝΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΓΙΑ ΤΟ ΕΤΟΣ " + year.getYear());
    
    for (EnvSector sector : year.getSectors()) {

      String translatedSector = translator.translateCategory(sector.getJsonKey());
      System.out.printf("ΤΟΜΕΑΣ: %s", translatedSector);
      double sectorTotal = 0.0;

        for (EnvUnit unit : sector.getUnits()) {
          String translatedUnit = translator.translateCategory(unit.getJsonKey());
          System.out.printf("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: %s", translatedUnit);

          double unitTotal = 0.0;
          
          for (EnvEntry entry : unit.getEntries()) {
            String translatedEntry = translator.translateCategory(entry.getJsonKey());
            double amount = entry.getAmount();

            unitTotal += amount;
          }
          
          System.out.printf("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: %s", unitTotal);
          sectorTotal += unitTotal;
        }
        
        System.out.printf("ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ %s",
              translatedSector, sectorTotal);

    }
  }
}
