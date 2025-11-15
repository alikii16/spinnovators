package gr.det.spinnovators;

import java.util.Scanner;  

public class App {
    public static void main( String[] args ) {
        FirstLogin.login();
    
        MinistryDataInput allData = new MinistryDataInput(); 
        FullBudgetPrinter printer = new FullBudgetPrinter(allData); 

        Scanner scanner = new Scanner(System.in);
        String chosenYear;

        do {
            System.out.println("================================="); 
            System.out.print("Ποιού έτους τον προϋπολογισμό θα θέλατε να δείτε; (2023, 2024 ή 2025): "); 
            chosenYear = scanner.nextLine();

            if (!chosenYear.equals("0000")) {
                printer.ShowBudget(chosenYear);
            } else if (!chosenYear.equals("2023") && !chosenYear.equals("2024") && !chosenYear.equals("2025") && !chosenYear.equals("0000")) {
                System.out.println("Μη έγκυρη επιλογή. Δοκιμάστε ξανά.");
            } else {
                System.out.println("Θα εμφανιστεί ο προϋπολογισμός του Υπουργείου Περιβάλλοντος και Ενέργειας.");
            }

        } while (!chosenYear.equals("0000"));
  
        scanner.close(); 
    } 
}
