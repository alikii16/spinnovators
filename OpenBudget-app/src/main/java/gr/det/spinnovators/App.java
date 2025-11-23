package gr.det.spinnovators;

import java.util.Scanner;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;


public class App {
    public static void main( String[] args ) {

        EnvBudgetTranslator translator = new EnvBudgetTranslator();
        EnvBudgetLoader envLoader = new EnvBudgetLoader();
        EnvBudgetData envBudgetData = envLoader.loadBudget();
        EnvBudgetPrinter envPrinter = new EnvBudgetPrinter(envBudgetData, translator);

        FirstLogin.login();
    
        MinistryDataInput allData = new MinistryDataInput(); 
        FullBudgetPrinter printer = new FullBudgetPrinter(allData); 

        Scanner scanner = new Scanner(System.in);
        String chosenYear;
        String tempChosenYear = "0000";

        do {
            System.out.println("================================="); 
            System.out.print("Ποιού έτους τον προϋπολογισμό θα θέλατε να δείτε; (2023, 2024 ή 2025): "); 
            chosenYear = scanner.nextLine();


            if (!chosenYear.equals("0000")) {
                printer.showBudget(chosenYear);
                tempChosenYear = chosenYear;
            } else if (!chosenYear.equals("2023") && !chosenYear.equals("2024") && !chosenYear.equals("2025") && !chosenYear.equals("0000")) {
                System.out.println("Μη έγκυρη επιλογή. Δοκιμάστε ξανά.");
            } else {
                System.out.println("Θα εμφανιστεί ο προϋπολογισμός του Υπουργείου Περιβάλλοντος και Ενέργειας.");
                
                envPrinter.printYearlyBudget(tempChosenYear);

            }

        } while (!chosenYear.equals("0000"));
  
        scanner.close(); 
    } 
}
