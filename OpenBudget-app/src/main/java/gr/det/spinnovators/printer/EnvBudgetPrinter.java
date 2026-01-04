package gr.det.spinnovators.printer;


import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.util.Locale;

/**
 * Responsible for formatting and printing the detailed environmental budget to the console.
 * This class processes the hierarchical structure of the budget (Sector > Unit > Entry)
 * and displays localized financial data for a specific year.
 */
public class EnvBudgetPrinter {

  private final EnvBudgetData data;
  private final EnvBudgetTranslator translator;
  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");

  /**
   * Constructs an EnvBudgetPrinter with the required data source and translator.
   *
   * @param data The budget data repository containing the environmental records.
   * @param translator The service used to translate internal keys into readable labels.
   */
  public EnvBudgetPrinter(EnvBudgetData data, EnvBudgetTranslator translator) {
    this.data = data;
    this.translator = translator;
  }

  /**
   * Generates and prints a structured report of the budget for the specified year.
   * The report includes breakdown by policy sectors, administrative units, 
   * and individual expense entries, including totals for each level.
   *
   * @param year The fiscal year to be printed.
   */
  public void printYearlyBudget(String year) {
    EnvYear yearlyBudget = data.getBudgetForYear(year);

    if (yearlyBudget == null) {
      System.out.println("Δεν βρέθηκαν αναλυτικά δεδομένα για το έτος " + year);
      return;
    }

    System.out.println("%n ΑΝΑΛΥΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΥΠΟΥΡΓΕΙΟΥ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ"
                       + " ΤΟΥ ΕΤΟΥΣ " + year);

    for (EnvSector sector : yearlyBudget.getSectors()) {

      String translatedSector = translator.translateCategory(sector.getJsonKey());
      System.out.println("%n--------------------------------------------------------------------");
      System.out.printf(" ΤΟΜΕΑΣ: %s%n", translatedSector);
      System.out.println("--------------------------------------------------------------------");

      double sectorTotal = 0;

      for (EnvUnit unit : sector.getUnits()) {

        String translatedUnit = translator.translateCategory(unit.getJsonKey());
        System.out.printf(" ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: %s%n", translatedUnit);

        double unitTotal = 0;
        for (EnvEntry entry : unit.getEntries()) {

          String translatedEntry = translator.translateCategory(entry.getJsonKey());
          double amount = entry.getAmount();

          System.out.printf(HELLENIC_LOCALE, "      - %-40s: %,.2f €%n", translatedEntry, amount);
          unitTotal += amount;
        }
        System.out.printf(HELLENIC_LOCALE, " ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: %,.2f €%n%n", unitTotal);
        sectorTotal += unitTotal;
      }
      System.out.printf(HELLENIC_LOCALE, " ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ (%s): %,.2f €%n",
                                     translatedSector, sectorTotal);
    }
    System.out.println("%n--------------------------------------------------------------------");
  }
}
