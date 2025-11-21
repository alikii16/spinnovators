package gr.det.spinnovators;

<<<<<<< HEAD
import java.awt.Desktop;
import java.util.Scanner;
=======
import java.util.Scanner;  
>>>>>>> parent of e6b01cd (Δημιουργία frontend: για τη σελίδα login, την σελίδα του κρατικού προϋπολογισμού για Υπουργό και Υπάλληλο, και την σελίδα προϋπολογισμού Υπουργείου για Υπάλληλο και Υπουργό. Ένωση του back-end με το front-end.)

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

<<<<<<< HEAD
            if (chosenYear.equals("2023") || chosenYear.equals("2024") || chosenYear.equals("2025")) {
                printer.showBudget(chosenYear);
            } else if (chosenYear.equals("0000")) {
                System.out.println("Για να δείτε τον προϋπολογισμό του ΥΠΕΝ, χρησιμοποιήστε το web interface.");
                System.out.println("Πατήστε Ctrl+C για έξοδο.\n");
=======
            if (!chosenYear.equals("0000")) {
                printer.ShowBudget(chosenYear);
            } else if (!chosenYear.equals("2023") && !chosenYear.equals("2024") && !chosenYear.equals("2025") && !chosenYear.equals("0000")) {
                System.out.println("Μη έγκυρη επιλογή. Δοκιμάστε ξανά.");
>>>>>>> parent of e6b01cd (Δημιουργία frontend: για τη σελίδα login, την σελίδα του κρατικού προϋπολογισμού για Υπουργό και Υπάλληλο, και την σελίδα προϋπολογισμού Υπουργείου για Υπάλληλο και Υπουργό. Ένωση του back-end με το front-end.)
            } else {
                System.out.println("Θα εμφανιστεί ο προϋπολογισμός του Υπουργείου Περιβάλλοντος και Ενέργειας.");
            }

        } while (!chosenYear.equals("0000"));
  
        scanner.close(); 
    } 
}
