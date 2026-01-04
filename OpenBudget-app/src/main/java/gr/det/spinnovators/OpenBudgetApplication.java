package gr.det.spinnovators;

import gr.det.spinnovators.authentication.FirstLogin;
import gr.det.spinnovators.data.MinistryDataInput;
import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.printer.EnvBudgetPrinter;
import gr.det.spinnovators.printer.FullBudgetPrinter;
import gr.det.spinnovators.service.EnvBudgetLoader;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import gr.det.spinnovators.web.LoginWebServer;
import gr.det.spinnovators.editor.EnvBudgetEditor;
import java.io.File;
import java.util.Scanner;

/**
 * The main entry point for the OpenBudget application.
 * This class initializes the core services, launches the optional web-based 
 * login interface, and manages the primary terminal-based user interaction flow.
 */
public class OpenBudgetApplication {
  /**
   * Starts the application and manages the main execution loop.
   *
   * @param args Command-line arguments passed to the application at startup.
   */
  public static void main(String[] args) {

    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    EnvBudgetLoader envLoader = new EnvBudgetLoader();
    EnvBudgetData envBudgetData = envLoader.loadBudget();
    EnvBudgetPrinter envPrinter = new EnvBudgetPrinter(envBudgetData, translator);

    // Start web server for HTML interface
    try {
      // Define explicit path (Fixes Windows issues)
      String[] possiblePaths = {
          "src/main/resources/frontend",              // Αν τρέχεις μέσα από τον φάκελο του project
          "OpenBudget-app/src/main/resources/frontend" // Αν τρέχεις από τον εξωτερικό φάκελο
      };

      String foundPath = null;
      for (String path : possiblePaths) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
          foundPath = path;
          break;
        }
      }

      if (foundPath != null) {
        LoginWebServer.startServer(foundPath);
        
        String url = "http://localhost:8080/login.html";
        System.out.println("==========================================");
        System.out.println("Web interface available at: " + url);
        System.out.println("==========================================");
      
        // (Optional) Open Browser automatically
        try {
          if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
          }
        } catch (Exception ignored) {
          System.out.println("Please open " + url + " manually in your browser");
        }
        System.out.println("You can also use the terminal interface below:");
        System.out.println("==========================================");

      } else {
        System.out.println("Warning: Frontend directory not found at " + foundPath);
        System.out.println("Using terminal mode only.");
      }
    } catch (Exception e) {
      System.out.println("Warning: Could not start web server: " + e.getMessage());
      System.out.println("Continuing with terminal mode only...");
    }

    // Terminal interface execution (original functionality)
    FirstLogin.login();

    MinistryDataInput allData = new MinistryDataInput();
    FullBudgetPrinter fullPrinter = new FullBudgetPrinter(allData);

    Scanner scanner = new Scanner(System.in);
    boolean exitApp = false;

    while (!exitApp) {
      System.out.println("\n=== ΜΕΝΟΥ ΕΠΙΛΟΓΗΣ ===");
      System.out.println("1. Κρατικός Προϋπολογισμός");
      System.out.println("2. Προϋπολογισμός Υπουργείου Περιβάλλοντος & Ενέργειας");
      System.out.println("3. Έξοδος");
      System.out.print("Επιλογή: ");
      String mainChoice = scanner.nextLine();

      if (mainChoice.equals("1")) {
        // General Budget Menu
        boolean backToMain = false;
        while (!backToMain) {
          System.out.println("\n--- Κρατικός Προϋπολογισμός ---");
          System.out.println("1. Προβολή Προϋπολογισμού (ανά έτος)");
          System.out.println("2. Σύγκριση Προϋπολογισμών");
          System.out.println("3. Επιστροφή στο Αρχικό Μενού");
          System.out.print("Επιλογή: ");
          String subChoice = scanner.nextLine();

          if (subChoice.equals("1")) {
            System.out.print("Δώστε έτος (2023, 2024, 2025, 2026): ");
            String yr = scanner.nextLine();
            fullPrinter.showBudget(yr);
          } else if (subChoice.equals("2")) {
            System.out.println("Εκκίνηση λειτουργίας σύγκρισης...");
            // Εδώ θα μπει η λογική σύγκρισης
          } else if (subChoice.equalsIgnoreCase("3")) {
            backToMain = true;
          }
        } 
      } else if (mainChoice.equals("2")) {
        // Ministry Budget Menu
        boolean backToMain = false;
        while (!backToMain) {
          System.out.println("\n--- Υπουργείο Περιβάλλοντος & Ενέργειας ---");
          System.out.println("1. Προβολή (ανά έτος)");
          System.out.println("2. Επεξεργασία (Editor)");
          System.out.println("3. Επιστροφή στο Αρχικό Μενού");
          System.out.print("Επιλογή: ");
          String subChoice = scanner.nextLine();

          if (subChoice.equals("1")) {
            System.out.print("Δώστε έτος για το Υπ. Περιβάλλοντος: ");
            String yr = scanner.nextLine();
            envPrinter.printYearlyBudget(yr);
          } else if (subChoice.equals("2")) {
            EnvBudgetEditor editor = new EnvBudgetEditor(envBudgetData, translator);
            editor.startEditingSession();
          } else if (subChoice.equalsIgnoreCase("3")) {
            backToMain = true;
          }
        }
      } else if (mainChoice.equals("3")) {
        System.out.println("Έξοδος...");
        exitApp = true;
      } else {
        System.out.println("Μη έγκυρη επιλογή. Παρακαλώ επιλέξτε μεταξύ 1, 2 ή 3.");
      }
    }
    scanner.close();
  }
}


