package gr.det.spinnovators;

import gr.det.spinnovators.authentication.FirstLogin;
import gr.det.spinnovators.data.MinistryDataInput;
import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.printer.EnvBudgetPrinter;
import gr.det.spinnovators.printer.FullBudgetPrinter;
import gr.det.spinnovators.service.EnvBudgetLoader;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import gr.det.spinnovators.web.LoginWebServer;
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
      } else if (!chosenYear.equals("2023") && !chosenYear.equals("2024")
          && !chosenYear.equals("2025") && !chosenYear.equals("0000")) {
        System.out.println("Μη έγκυρη επιλογή. Δοκιμάστε ξανά.");
      } else {
        System.out.println("Θα εμφανιστεί ο προϋπολογισμός του Υπουργείου "
            + "Περιβάλλοντος και Ενέργειας.");
        envPrinter.printYearlyBudget(tempChosenYear);
      }

    } while (!chosenYear.equals("0000"));

    // Start Editor
    gr.det.spinnovators.editor.EnvBudgetEditor editor = 
        new gr.det.spinnovators.editor.EnvBudgetEditor(envBudgetData, translator);
    editor.startEditingSession();

    scanner.close();
  }
}
