package gr.det.spinnovators;


import gr.det.spinnovators.authentication.FirstLogin;
import gr.det.spinnovators.data.MinistryDataInput;
import gr.det.spinnovators.editor.EnvBudgetEditor;
import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EnvBudgetPrinter;
import gr.det.spinnovators.printer.FullBudgetPrinter;
import gr.det.spinnovators.service.BudgetPercentageService;
import gr.det.spinnovators.service.EnvBudgetLoader;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import gr.det.spinnovators.service.EsgScoreCalculator;
import gr.det.spinnovators.service.YearToYearBudgetComparison;
import gr.det.spinnovators.web.LoginWebServer;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The main entry point for the OpenBudget application.
 *
 * <p>This class serves as the orchestrator for the entire budget management system,
 * initializing core services, launching the optional web-based interface, and managing
 * the primary terminal-based user interaction flow. It provides a dual-interface
 * approach: a modern web UI and a traditional console interface.</p>
 *
 * <p>The application supports two user roles:
 * <ul>
 *   <li><strong>Minister (Role "a"):</strong> Full access including budget editing,
 *       comparisons, ESG reports, and all viewing capabilities</li>
 *   <li><strong>Employee (Role "b"):</strong> Read-only access to view budgets,
 *       compare years, and generate ESG reports</li>
 * </ul>
 * </p>
 *
 * <p>Main features:
 * <ul>
 *   <li>View state budget data for years 2023-2026</li>
 *   <li>View environmental ministry budget with detailed breakdowns</li>
 *   <li>Edit and modify future budgets (2025-2026) - Minister only</li>
 *   <li>Compare budgets across different years</li>
 *   <li>Generate ESG (Environmental, Social, Governance) sustainability reports</li>
 *   <li>Calculate ministry budget percentage of total state budget</li>
 *   <li>Web-based interface with automatic browser launch</li>
 * </ul>
 * </p>
 *
 * <p>The application attempts to start a web server on port 8080 and automatically
 * opens the default browser. If the web server fails to start, the application
 * gracefully falls back to terminal-only mode.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see FirstLogin
 * @see LoginWebServer
 * @see EnvBudgetEditor
 */

public class OpenBudgetApplication {
  /**
   * Starts the application and manages the main execution loop.
   *
   * @param args Command-line arguments passed to the application at startup.
   */
  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    EnvBudgetTranslator translator = new EnvBudgetTranslator();
    EnvBudgetLoader envLoader = new EnvBudgetLoader();
    EnvBudgetData envBudgetData = envLoader.loadBudget();
    EnvBudgetPrinter envPrinter = new EnvBudgetPrinter(envBudgetData, translator);

    // Start web server for HTML interface
    try {
      // Define explicit path (Fixes Windows issues)
      String[] possiblePaths = {
          "src/main/resources/frontend",
          "OpenBudget-app/src/main/resources/frontend"
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
    String role = FirstLogin.login(scanner);

    MinistryDataInput allData = new MinistryDataInput();
    FullBudgetPrinter fullPrinter = new FullBudgetPrinter(allData);
    EsgScoreCalculator esgCalculator = new EsgScoreCalculator();
    BudgetPercentageService percentageService = new BudgetPercentageService();


    if (role.equals("a")) {
      boolean exitApp = false;

      while (!exitApp) {
        System.out.println("=== ΜΕΝΟΥ ΕΠΙΛΟΓΗΣ ===");
        System.out.println("1. Κρατικός Προϋπολογισμός");
        System.out.println("2. Προϋπολογισμός Υπουργείου Περιβάλλοντος & Ενέργειας");
        System.out.println("3. Έξοδος");
        System.out.print("Επιλογή: ");

        if (!scanner.hasNextLine()) {
          break;
        }
        String mainChoice = scanner.nextLine();

        if (mainChoice.equals("1")) {
          // General Budget Menu for minister
          boolean backToMain = false;
          while (!backToMain) {
            System.out.println("--- Κρατικός Προϋπολογισμός ---");
            System.out.println("1. Προβολή Προϋπολογισμού (ανά έτος)");
            System.out.println("2. Επιστροφή στο Αρχικό Μενού");
            System.out.print("Επιλογή: ");

            if (!scanner.hasNextLine()) {
              break;
            }
            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
              System.out.print("Δώστε έτος (2023, 2024, 2025, 2026): ");
              if (scanner.hasNextLine()) {
                String yr = scanner.nextLine();
                fullPrinter.showBudget(yr);
              }
            } else if (subChoice.equalsIgnoreCase("2")) {
              backToMain = true;
            }
          }
        } else if (mainChoice.equals("2")) {
          // Ministry Budget Menu for minister
          boolean backToMain = false;
          while (!backToMain) {
            System.out.println("--- Υπουργείο Περιβάλλοντος & Ενέργειας ---");
            System.out.println("1. Προβολή (ανά έτος)");
            System.out.println("2. Επεξεργασία Προϋπολογισμού και στη συνέχεια"
                      + "Σύγκριση του με τα αρχικά δεδομένα");
            System.out.println("3. Σύγκριση Προϋπολογισμών μεταξύ ετών");
            System.out.println("4. Προβολή Αναφοράς Βιωσιμότητας (ESG Report)");
            System.out.println("5. Επιστροφή στο Αρχικό Μενού");
            System.out.print("Επιλογή: ");

            if (!scanner.hasNextLine()) {
              break;
            }
            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
              System.out.print("Δώστε έτος για το Υπ. Περιβάλλοντος: ");
              if (scanner.hasNextLine()) {
                String yr = scanner.nextLine();
                try {
                  int selectedYearInt = Integer.parseInt(yr);
                  percentageService.displayEnvironmentPercentage(selectedYearInt);
                  envPrinter.printYearlyBudget(yr);
                } catch (NumberFormatException e) {
                  System.out.println("Λάθος έτος.");
                }
              }

            } else if (subChoice.equals("2")) {
              EnvBudgetEditor editor = new EnvBudgetEditor(envBudgetData, translator, scanner);
              editor.startEditingSession();

            } else if (subChoice.equals("3")) {
              System.out.print("Με βάση ποιο έτος θέλετε να γίνει η σύγκριση: ");
              String y1 = scanner.hasNextLine() ? scanner.nextLine() : "";
              System.out.print("Επιλέξτε το δεύτερο έτος: ");
              String y2 = scanner.hasNextLine() ? scanner.nextLine() : "";

              EnvYear baseYear = envBudgetData.getBudgetForYear(y1);
              EnvYear compareYear = envBudgetData.getBudgetForYear(y2);

              if (baseYear != null && compareYear != null) {
                YearToYearBudgetComparison comparisonService = new YearToYearBudgetComparison(
                    translator, envBudgetData.getEnvMinistryTotalBudget());
                comparisonService.compareYears(baseYear, compareYear);
              } else {
                System.out.println(
                    "Σφάλμα: Τα έτη " + y1 + " και " + y2 + " δεν βρέθηκαν στα δεδομένα."
                );
              }

            } else if (subChoice.equalsIgnoreCase("4")) {
              System.out.print("Επιλέξτε έτος για αναφορά ESG (π.χ. 2025): ");
              String yrEsg = scanner.hasNextLine() ? scanner.nextLine() : "";

              EnvYear selectedYear = envBudgetData.getBudgetForYear(yrEsg);
              Double totalBudget = envBudgetData.getEnvMinistryTotalBudget().get(yrEsg);

              if (selectedYear != null && totalBudget != null) {
                System.out.println("\n--- ΥΠΟΛΟΓΙΣΜΟΣ ESG REPORT ---");

                EsgReport report = esgCalculator.calculateReport(selectedYear, totalBudget);

                System.out.println("==========================================");
                System.out.println("ΑΝΑΦΟΡΑ ΒΙΩΣΙΜΟΤΗΤΑΣ ΕΤΟΥΣ: " + report.getYear());
                System.out.println("Αξιολόγηση: " + report.getRatingGreek());
                System.out.println("Συνολικό Score: " + String.format("%.2f",
                          report.getOverallScore()) + "/100");
                System.out.println("------------------------------------------");
                System.out.printf("Περιβαλλοντικό Score: %.2f%% (%,.2f €)\n",
                          report.getEnvironmentalScore(), report.getEnvironmentalAmount());
                System.out.printf("Κοινωνικό Score:      %.2f%% (%,.2f €)\n",
                          report.getSocialScore(), report.getSocialAmount());
                System.out.printf("Διακυβέρνηση Score:   %.2f%% (%,.2f €)\n",
                          report.getGovernanceScore(), report.getGovernanceAmount());
                System.out.println("==========================================");
              } else {
                System.out.println(" Δεν βρέθηκαν επαρκή δεδομένα για το έτος " + yrEsg);
              }
            } else if (subChoice.equalsIgnoreCase("5")) {
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

    } else if (role.equals("b")) {
      boolean exitApp = false;

      while (!exitApp) {
        System.out.println("=== ΜΕΝΟΥ ΕΠΙΛΟΓΗΣ ===");
        System.out.println("1. Κρατικός Προϋπολογισμός");
        System.out.println("2. Προϋπολογισμός Υπουργείου Περιβάλλοντος & Ενέργειας");
        System.out.println("3. Έξοδος");
        System.out.print("Επιλογή: ");

        if (!scanner.hasNextLine()) {
          break;
        }
        String mainChoice = scanner.nextLine();

        if (mainChoice.equals("1")) {
          // General Budget Menu for employee
          boolean backToMain = false;
          while (!backToMain) {
            System.out.println("--- Κρατικός Προϋπολογισμός ---");
            System.out.println("1. Προβολή Προϋπολογισμού (ανά έτος)");
            System.out.println("2. Επιστροφή στο Αρχικό Μενού");
            System.out.print("Επιλογή: ");

            if (!scanner.hasNextLine()) {
              break;
            }
            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
              System.out.print("Δώστε έτος (2023, 2024, 2025, 2026): ");
              if (scanner.hasNextLine()) {
                fullPrinter.showBudget(scanner.nextLine());
              }
            } else if (subChoice.equalsIgnoreCase("2")) {
              backToMain = true;
            }
          }
        } else if (mainChoice.equals("2")) {
          // Ministry Budget Menu for employee
          boolean backToMain = false;
          while (!backToMain) {
            System.out.println("--- Υπουργείο Περιβάλλοντος & Ενέργειας ---");
            System.out.println("1. Προβολή (ανά έτος)");
            System.out.println("2. Σύγκριση Προϋπολογισμών μεταξύ ετών");
            System.out.println("3. Προβολή Αναφοράς Βιωσιμότητας (ESG Report)");
            System.out.println("4. Επιστροφή στο Αρχικό Μενού");
            System.out.print("Επιλογή: ");

            if (!scanner.hasNextLine()) {
              break;
            }
            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
              System.out.print("Δώστε έτος για το Υπουργείο Περιβάλλοντος: ");
              if (scanner.hasNextLine()) {
                String yr = scanner.nextLine();
                try {
                  int selectedYearInt = Integer.parseInt(yr);
                  percentageService.displayEnvironmentPercentage(selectedYearInt);
                  envPrinter.printYearlyBudget(yr);
                } catch (NumberFormatException e) {
                  System.out.println("Λάθος έτος.");
                }
              }

            } else if (subChoice.equals("2")) {
              System.out.print("Με βάση ποιο έτος θέλετε να γίνει η σύγκριση: ");
              String y1 = scanner.hasNextLine() ? scanner.nextLine() : "";
              System.out.print("Επιλέξτε το δεύτερο έτος: ");
              String y2 = scanner.hasNextLine() ? scanner.nextLine() : "";

              EnvYear baseYear = envBudgetData.getBudgetForYear(y1);
              EnvYear compareYear = envBudgetData.getBudgetForYear(y2);

              if (baseYear != null && compareYear != null) {
                YearToYearBudgetComparison comparisonService = new YearToYearBudgetComparison(
                    translator, envBudgetData.getEnvMinistryTotalBudget());
                comparisonService.compareYears(baseYear, compareYear);
              } else {
                System.out.println(
                    "Σφάλμα: Τα έτη " + y1 + " και " + y2 + " δεν βρέθηκαν στα δεδομένα."
                );
              }

            } else if (subChoice.equalsIgnoreCase("3")) {
              System.out.print("Επιλέξτε έτος για αναφορά ESG (π.χ. 2025): ");
              String yrEsg = scanner.hasNextLine() ? scanner.nextLine() : "";

              EnvYear selectedYear = envBudgetData.getBudgetForYear(yrEsg);

              Double totalBudget = envBudgetData.getEnvMinistryTotalBudget().get(yrEsg);

              if (selectedYear != null && totalBudget != null) {
                System.out.println("\n--- ΥΠΟΛΟΓΙΣΜΟΣ ESG REPORT ---");

                EsgReport report = esgCalculator.calculateReport(selectedYear, totalBudget);

                System.out.println("==========================================");
                System.out.println("ΑΝΑΦΟΡΑ ΒΙΩΣΙΜΟΤΗΤΑΣ ΕΤΟΥΣ: " + report.getYear());
                System.out.println("Αξιολόγηση: " + report.getRatingGreek());
                System.out.println("Συνολικό Score: " + String.format("%.2f",
                                report.getOverallScore()) + "/100");
                System.out.println("------------------------------------------");
                System.out.printf("Περιβαλλοντικό Score: %.2f%% (%,.2f €)\n",
                                report.getEnvironmentalScore(), report.getEnvironmentalAmount());
                System.out.printf("Κοινωνικό Score:      %.2f%% (%,.2f €)\n",
                                report.getSocialScore(), report.getSocialAmount());
                System.out.printf("Διακυβέρνηση Score:   %.2f%% (%,.2f €)\n",
                                report.getGovernanceScore(), report.getGovernanceAmount());
                System.out.println("==========================================");
              } else {
                System.out.println(" Δεν βρέθηκαν επαρκή δεδομένα για το έτος " + yrEsg);
              }
            } else if (subChoice.equals("4")) {
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
    }
    scanner.close();
  }
}
