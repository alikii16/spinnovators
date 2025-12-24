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

public class OpenBudgetApplication {
  public static void main(String[] args) {

    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    EnvBudgetLoader envLoader = new EnvBudgetLoader();
    EnvBudgetData envBudgetData = envLoader.loadBudget();
    EnvBudgetPrinter envPrinter = new EnvBudgetPrinter(envBudgetData, translator);

    // Start web server for HTML interface
    try {
      String frontendPath = null;

      // Try to find frontend directory from classpath resources
      java.net.URL resourceUrl = OpenBudgetApplication.class.getClassLoader()
          .getResource("frontend/login.html");
      if (resourceUrl != null && resourceUrl.getProtocol().equals("file")) {
        try {
          frontendPath = java.nio.file.Paths.get(resourceUrl.toURI()).getParent().toString();
        } catch (Exception e) {
          // Continue to fallback paths
        }
      }

      // Fallback: try common paths if resource not found
      // Prefer src/main/resources/frontend first (for development) before target/classes/frontend
      if (frontendPath == null || !new File(frontendPath).exists()) {
        String currentDir = System.getProperty("user.dir");
        String[] possiblePaths = {
            currentDir + File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "frontend",
            currentDir + File.separator + "OpenBudget-app" + File.separator + "src"
                + File.separator + "main" + File.separator + "resources"
                + File.separator + "frontend",
            currentDir + File.separator + "target" + File.separator + "classes"
                + File.separator + "frontend"
        };

        for (String path : possiblePaths) {
          File dir = new File(path);
          if (dir.exists() && dir.isDirectory()) {
            frontendPath = path;
            break;
          }
        }
      }

      if (frontendPath != null && new File(frontendPath).exists()) {
        LoginWebServer.startServer(frontendPath);
        System.out.println("Web interface available at http://localhost:8080/login.html");

        // Try to open browser automatically
        try {
          String url = "http://localhost:8080/login.html";
          java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
          if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            desktop.browse(new java.net.URI(url));
          } else {
            // Fallback for Windows
            new ProcessBuilder("cmd", "/c", "start", url).start();
          }
        } catch (Exception browserEx) {
          System.out.println("Please open http://localhost:8080/login.html "
              + "manually in your browser");
        }

        System.out.println("You can also use the terminal interface below:");
        System.out.println("==========================================");
      } else {
        System.out.println("Warning: Frontend directory not found. Using terminal mode only.");
      }
    } catch (Exception e) {
      System.out.println("Warning: Could not start web server: " + e.getMessage());
      System.out.println("Continuing with terminal mode only...");
    }

    // Terminal interface (original functionality)
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
    scanner.close();
  }
}
