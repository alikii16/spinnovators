package gr.det.spinnovators.printer;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;

/**
 * Prints the results of the changed budget data to the console.
 * This class is responsible for displaying the updated hierarchical budget structure
 * (Year → Sector → Unit → Entry) with localized Greek labels and formatted amounts.
 *
 * <p>The output includes:
 * <ul>
 *   <li>Translated sector names</li>
 *   <li>Translated unit names</li>
 *   <li>Individual entry amounts with Greek category labels</li>
 *   <li>Subtotals for each unit</li>
 *   <li>Total amounts for each sector</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
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
   * Prints the complete updated budget for the specified year to the console.
   *
   * <p>This method traverses the entire budget hierarchy and displays:
   * <ol>
   *   <li>A header with the year</li>
   *   <li>Each sector with its translated name</li>
   *   <li>Each unit within the sector with its translated name</li>
   *   <li>All entries within each unit with amounts</li>
   *   <li>Subtotals for each unit</li>
   *   <li>Total for each sector</li>
   * </ol>
   * </p>
   *
   * <p>All monetary values are formatted with thousand separators and
   * two decimal places followed by the euro symbol (€).</p>
   *
   * @param year the EnvYear object containing the updated budget data to print
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
