package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvYear;

import java.util.Locale;

public class EnvBudgetPrinter {

    private final EnvBudgetData data;
    private final EnvBudgetTranslator translator;
    private static final Locale HELLENIC_LOCALE = new Locale("el", "GR");

    public EnvBudgetPrinter(EnvBudgetData data, EnvBudgetTranslator translator) {
        this.data = data;
        this.translator = translator;
    }

    public void printYearlyBudget(String year) {
        EnvYear yearlyBudget = data.getBudgetForYear(year);

        if (yearlyBudget == null) {
            System.out.println("Δεν βρέθηκαν αναλυτικά δεδομένα για το έτος " + year);
            return;
        } 
        
        System.out.println("\n ΑΝΑΛΥΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΥΠΟΥΡΓΕΙΟΥ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ ΤΟΥ ΕΤΟΥΣ " + year );
            
            for (EnvSector sector : yearlyBudget.getSectors()) {
                
                String translatedSector = translator.translateCategory(sector.getJsonKey());
                System.out.println("\n------------------------------------------------------------------------");
                System.out.printf(" ΤΟΜΕΑΣ: %s\n", translatedSector);
                System.out.println("------------------------------------------------------------------------");

                for (EnvUnit unit : sector.getUnits()) {
                    
                    String translatedUnit = translator.translateCategory(unit.getJsonKey());
                    System.out.printf(" ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ: %s\n", translatedUnit);

                    double unitTotal = 0;
                    for (EnvEntry entry : unit.getEntries()) {
                        
                        String translatedEntry = translator.translateCategory(entry.getJsonKey());
                        double amount = entry.getAmount();
                        
                        System.out.printf(HELLENIC_LOCALE, "      - %-40s: %,.2f €\n", translatedEntry, amount);
                        unitTotal += amount;
                    }
                    System.out.printf(HELLENIC_LOCALE, " ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ: %,.2f €\n\n", unitTotal);
                }
        }
        System.out.println("\n------------------------------------------------------------------------");
    }
}
