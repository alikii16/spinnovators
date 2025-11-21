package gr.det.spinnovators;

import java.awt.Desktop;
import java.io.File;
import java.util.Scanner;

public class App {
        public static void main(String[] args) throws Exception {
        MinistryDataInput allData = new MinistryDataInput();
        BudgetHttpServer budgetServer = new BudgetHttpServer(allData);
        
        try {
            budgetServer.start();
            System.out.println("========================================");
            System.out.println("HTTP Server started successfully on port 8081");
            System.out.println("Server is ready to accept requests");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("ΣΦΑΛΜΑ: Δεν ήταν δυνατή η εκκίνηση του HTTP Server!");
            System.err.println("========================================");
            System.err.println("Λεπτομέρειες: " + e.getMessage());
            e.printStackTrace();
            System.err.println("========================================");
            System.err.println("Η εφαρμογή θα τερματιστεί.");
            return; // Exit if server cannot start
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nΚλείσιμο του server...");
            budgetServer.stop();
        }));

        // Open the login page in browser via HTTP server
        try {
            Desktop.getDesktop().browse(new java.net.URI("http://localhost:8081/portal"));
        } catch (Exception e) {
            System.out.println("Δεν ήταν δυνατό να ανοίξει το browser αυτόματα.");
            System.out.println("Παρακαλώ ανοίξτε το browser και πηγαίνετε στο: http://localhost:8081/portal");
        }

        FullBudgetPrinter printer = new FullBudgetPrinter(allData);

        Scanner scanner = new Scanner(System.in);
        String chosenYear;

        do {
            System.out.println("=================================");
            System.out.print("Ποιο έτος θέλετε να δείτε; (2023, 2024, 2025): ");
            chosenYear = scanner.nextLine();

            if (chosenYear.equals("2023") || chosenYear.equals("2024") || chosenYear.equals("2025")) {
                printer.ShowBudget(chosenYear);
            } else if (chosenYear.equals("0000")) {
                System.out.println("Για να δείτε τον προϋπολογισμό του ΥΠΕΝ, χρησιμοποιήστε το web interface.");
                System.out.println("Πατήστε Ctrl+C για έξοδο.\n");
            } else {
                System.out.println("Δεν υπάρχουν στοιχεία για το έτος \"" + chosenYear + "\".");
                System.out.println("Δοκιμάστε ξανά.\n");
            }

        } while (!chosenYear.equals("0000"));

        scanner.close();
        // Server will be stopped by shutdown hook
    }
}