package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.EnvBudgetTranslator;


public class EditsPrinter {

    private final EnvBudgetTranslator translator;
    private final int yearforPrinting;

    public EditsPrinter(EnvBudgetTranslator translator, int yearforPrinting) {
        this.translator  = translator;
        this.yearforPrinting = yearforPrinting;
    }

    public void printEditYear(EnvYear year) {
        if (year.getYear() != this.yearForPrinting) {
            System.out.println("Το έτος " + this.yearforPrinting + " δεν ταιριάζει με το φορτωμένα δεδομένα");
            
        }

    }




}
