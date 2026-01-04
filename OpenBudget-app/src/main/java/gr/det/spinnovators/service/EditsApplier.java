package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EsgPrinter;
import java.util.List;
import java.util.Scanner;

/**
 * Applies edits to budget entries and tracks ESG sustainability impact.
 *
 * <p>This class handles the interactive budget editing process, validates changes,
 * and calculates the ESG sustainability score before and after modifications.</p>
 *
 * @author Spinnovators Team
 * @version 2.0 (ESG-enabled)
 */
public class EditsApplier {

  private final EnvBudgetTranslator translator;
  private final Scanner scanner;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;
  private final InitialBudgetComparison comparisonAnalyzer;

  // Budget tracking - preserve data during changes
  private double currentBalance = 0;
  private double totalBudget = 0;

  // ESG tracking
  private EsgReport initialEsgReport;
  private EsgReport currentEsgReport;

  // Deep copy for final comparison
  private EnvYear originalYearSnapshot;

  /**
   * Constructs an EditsApplier with necessary services for translation and ESG analysis.
   *
   * @param translator The service used for converting internal keys to readable text.
   */
  public EditsApplier(EnvBudgetTranslator translator) {
    this.translator = translator;
    // ΠΡΕΠΕΙ ΝΑ ΕΙΝΑΙ:
    this.scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8);
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
    this.comparisonAnalyzer = new InitialBudgetComparison(translator);
  }

  /**
   * Initiates the interactive editing session for a specific fiscal year.
   * <p>The session continues until the user balances the budget 
   * (difference between original and new total must be zero).</p>
   *
   * @param year The EnvYear object representing the budget to be modified.
   */
  public void applyEditsToYear(EnvYear year) {
    System.out.println("%n--- ΕΝΑΡΞΗ ΕΠΕΞΕΡΓΑΣΙΑΣ ΓΙΑ ΤΟ ΕΤΟΣ " + year.getYear() + " ---");

    String temp = year.getYear();

    if ("2025".equals(temp)) {
      this.totalBudget = 2341227000.00;
    } else if ("2026".equals(temp)) {
      this.totalBudget = 3133452000.00;
    }

    this.currentBalance = 0.0;

    // Create deep copy of original state for final comparison
    this.originalYearSnapshot = createDeepCopy(year);

    // Calculate and display initial ESG report
    calculateAndDisplayInitialEsg(year);

    boolean keepEditing = true;

    while (keepEditing) {
      // Display balance status
      if (Math.abs(currentBalance) > 0.01) {
        System.out.printf("%n>>> ΥΠΟΛΟΙΠΟ ΓΙΑ ΙΣΟΣΚΕΛΙΣΜΟ: %,.2f € <<<%n", this.currentBalance);
      }

      // Also show current ESG score
      if (currentEsgReport != null) {
        esgPrinter.printCompactSummary(currentEsgReport);
      }

      EnvSector selectedSector = selectSector(year);

      if (selectedSector == null) {
        // User wants to exit - check if budget is balanced
        if (Math.abs(this.currentBalance) < 0.01) {
          System.out.println(" Ο προϋπολογισμός είναι ισοσκελισμένος!");
          System.out.println("Τερματισμός Λειτουργίας.");
          keepEditing = false; // EXIT LOOP
        } else {
          System.out.println("!!! ΠΡΟΣΟΧΗ !!!");
          System.out.println("Δεν επιτρέπεται τερματισμός.");
          System.out.println("Ο προϋπολογισμός δεν ισοσκελίστηκε.");
          System.out.printf("Πρέπει να καλύψετε διαφορά: %,.2f €%n", currentBalance);
        }
        continue;
      }

      EnvUnit selectedUnit = selectUnit(selectedSector);
      if (selectedUnit == null) {
        continue;
      }

      System.out.println("%n------------------------------------------------");
      System.out.println("Μονάδα: " + translator.translateCategory(selectedUnit.getJsonKey()));
      System.out.println("Πληκτρολογήστε το όνομα της κατηγορίας που θέλετε να επεξεργαστείτε:");
      System.out.print("--> ");

      String searchInput = scanner.nextLine().trim();

      if (!searchInput.isEmpty()) {
        findAndEditEntryInUnit(selectedUnit, searchInput, year);
      }
    }

    System.out.println("%n Προετοιμασία αναλυτικής σύγκρισης...%n");

    // Perform full comparison between original and modified budget
    comparisonAnalyzer.performFullComparison(
        originalYearSnapshot,  // Original state (snapshot)
        year,                  // Modified state (current)
        totalBudget
    );

    System.out.println("%n--- ΤΕΛΟΣ ΕΠΕΞΕΡΓΑΣΙΑΣ ---");
  }

  /**
   * Creates a deep copy of EnvYear for comparison purposes.
   *
   * @param year The year to copy.
   * 
   * @return A deep copy of the year.
   */
  private EnvYear createDeepCopy(EnvYear year) {
    List<EnvSector> copiedSectors = new java.util.ArrayList<>();

    for (EnvSector sector : year.getSectors()) {
      List<EnvUnit> copiedUnits = new java.util.ArrayList<>();

      for (EnvUnit unit : sector.getUnits()) {
        List<EnvEntry> copiedEntries = new java.util.ArrayList<>();

        for (EnvEntry entry : unit.getEntries()) {
          // Create new entry with same values
          copiedEntries.add(new EnvEntry(entry.getJsonKey(), entry.getAmount()));
        }

        copiedUnits.add(new EnvUnit(unit.getJsonKey(), copiedEntries));
      }

      copiedSectors.add(new EnvSector(sector.getJsonKey(), copiedUnits));
    }

    return new EnvYear(year.getYear(), copiedSectors);
  }

  /**
   * Calculates and displays the initial ESG report.
   *
   * @param year The budget year.
   */
  private void calculateAndDisplayInitialEsg(EnvYear year) {
    System.out.println("%n Υπολογισμός αρχικού ESG Score...%n");

    try {
      // Calculate initial ESG report
      this.initialEsgReport = esgCalculator.calculateReport(year, totalBudget);
      this.currentEsgReport = initialEsgReport;

      // Display the report
      esgPrinter.printReport(initialEsgReport);

      System.out.println(" Μπορείτε να δείτε πώς οι αλλαγές σας επηρεάζουν το ESG score!");
      System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%n");
    } catch (Exception e) {
      System.err.println("  Σφάλμα κατά τον υπολογισμό ESG: " + e.getMessage());
      System.out.println("Η επεξεργασία θα συνεχιστεί χωρίς ESG tracking.%n");
      this.initialEsgReport = null;
      this.currentEsgReport = null;
    }
  }

  /**
   * Presents a list of sectors to the user for selection.
   *
   * @param year The budget year containing the sectors.
   * @return The chosen EnvSector or null if the user chooses to exit.
   */
  private EnvSector selectSector(EnvYear year) {
    final List<EnvSector> sectors = year.getSectors();
    System.out.println("%n==========================================");
    System.out.println(" ΕΠΙΛΟΓΗ ΤΟΜΕΑ");
    System.out.println("==========================================");

    for (int i = 0; i < sectors.size(); i++) {
      String name = translator.translateCategory(sectors.get(i).getJsonKey());
      System.out.println((i + 1) + ". " + name);
    }

    System.out.println("0. ΤΕΛΟΣ / ΕΛΕΓΧΟΣ ΙΣΟΣΚΕΛΙΣΜΟΥ");
    System.out.print("--> Επιλογή: ");

    int choice = readIntegerChoice(sectors.size());
    if (choice <= 0) {
      return null;
    }
    return sectors.get(choice - 1);
  }

  /**
   * Presents a list of units within a sector for user selection.
   *
   * @param sector The parent sector.
   * @return The chosen EnvUnit or null to return to sector selection.
   */
  private EnvUnit selectUnit(EnvSector sector) {
    List<EnvUnit> units = sector.getUnits();
    System.out.println("%n--- Επιλογή Μονάδας ---");

    for (int i = 0; i < units.size(); i++) {
      String name = translator.translateCategory(units.get(i).getJsonKey());
      System.out.println((i + 1) + ". " + name);
    }
    System.out.println("0. Επιστροφή");
    System.out.print("--> Επιλογή: ");

    int choice = readIntegerChoice(units.size());
    if (choice <= 0) {
      return null;
    }
    return units.get(choice - 1);
  }

  /**
   * Searches for an entry in a unit and allows editing.
   *
   * @param unit       The unit to search in
   * @param searchName The category name to search for
   * @param year       The current budget year
   */
  private void findAndEditEntryInUnit(EnvUnit unit, String searchName, EnvYear year) {
    boolean found = false;

    // A loop in order to search the Unit and find the desired entry
    for (EnvEntry entry : unit.getEntries()) {
      String entryName = translator.translateCategory(entry.getJsonKey());

      if (entryName.equalsIgnoreCase(searchName)) {
        found = true;
        System.out.printf("%nΒρέθηκε: %s | Τρέχον Ποσό: %,.2f €%n", entryName, entry.getAmount());
        
        double oldAmount = entry.getAmount();
        BudgetValidator validator = new BudgetValidator();
        double finalValidatedAmount = oldAmount;

        // Loop for validation until we get a valid or confirmed value
        while (true) {
          System.out.print("Εισάγετε το νέο ποσό: ");
          String amountInput = scanner.nextLine().trim();

          if (amountInput.isEmpty()) {
            System.out.println(" Δεν δόθηκε τιμή. Καμία αλλαγή.");
            return;
          }

          try {
            
            amountInput = amountInput.replace(",", ".");
            double inputAmount = Double.parseDouble(amountInput);

            // 1. Retrieve Sector Key (required for ESG classification)
            String sectorKey = year.getSectors().stream()
                .filter(s -> s.getUnits().contains(unit))
                .findFirst()
                .map(EnvSector::getJsonKey)
                .orElse("unknown");
            
            // 2. Call Validator with ESG context
            BudgetValidator.ValidationResult result = 
                validator.validate(this.totalBudget, oldAmount, inputAmount, 
                  sectorKey, entry.getJsonKey());

            // 3. Handle Validation Results
            if (result == BudgetValidator.ValidationResult.OK) {
              finalValidatedAmount = inputAmount;
              break;

            } else if (result == BudgetValidator.ValidationResult.NEGATIVE_VALUE) {
              System.out.println(" ΣΦΑΛΜΑ: Μη έγκυρη τιμή (αρνητικό ποσό).");

            } else if (result == BudgetValidator.ValidationResult.EXCEEDS_TOTAL_BUDGET) {
              System.out.printf(" ΣΦΑΛΜΑ: Υπέρβαση Ορίου Προϋπολογισμού (Όριο: %,.2f €).%n",
                  this.totalBudget);

            // --- SMART ESG BLOCKS ---
            } else if (result == BudgetValidator.ValidationResult.ESG_ENV_PROTECTION) {
              System.out.println(" ΠΡΟΣΤΑΣΙΑ ΠΕΡΙΒΑΛΛΟΝΤΟΣ: Η μείωση δαπανών άνω του "
                  + "5% απαγορεύεται.");
              System.out.println(" Στόχος: Διατήρηση υψηλού δείκτη βιωσιμότητας "
                  + "(Environmental Score).");

            } else if (result == BudgetValidator.ValidationResult.ESG_GOV_RESTRICTION) {
              System.out.println(" ΔΗΜΟΣΙΟΝΟΜΙΚΗ ΠΕΙΘΑΡΧΙΑ: Η αύξηση διοικητικών δαπανών "
                  + "άνω του 10% απαγορεύεται.");
              System.out.println(" Στόχος: Περιορισμός της γραφειοκρατίας και εξοικονόμηση "
                  + "πόρων (Governance Score).");

            } else if (result == BudgetValidator.ValidationResult.ESG_SOCIAL_PROTECTION) {
              System.out.println(" ΚΟΙΝΩΝΙΚΗ ΠΟΛΙΤΙΚΗ: Η μείωση μισθών/παροχών άνω του "
                  + "10% απαγορεύεται.");
              System.out.println(" Στόχος: Προστασία του βιοτικού επιπέδου των εργαζομένων "
                  + "(Social Score).");

            // --- WARNING (Yellow Card) ---
            } else if (result == BudgetValidator.ValidationResult.EXTREME_DEVIATION) {
              double dev = validator.calculateDeviationPercentage(oldAmount, inputAmount);
              System.out.printf(" ΠΡΟΕΙΔΟΠΟΙΗΣΗ: Παρατηρείται μεγάλη απόκλιση (%.2f%%).%n", dev);
              System.out.print(" Επιθυμείτε να προχωρήσετε παρόλα αυτά; (ΝΑΙ/ΟΧΙ): ");
              
              String confirm = scanner.nextLine().trim();
              if (confirm.equalsIgnoreCase("ΝΑΙ")) {
                finalValidatedAmount = inputAmount;
                break;
              } else {
                System.out.println(" Η αλλαγή ακυρώθηκε. Παρακαλώ εισάγετε νέα τιμή.");
              }
            }

          } catch (NumberFormatException e) {
            System.out.println(" ΣΦΑΛΜΑ: Μη έγκυρη μορφή αριθμού.");
          }
        }

        // Apply changes & Recalculate
        entry.setAmount(finalValidatedAmount);
        double offsetAmount = oldAmount - finalValidatedAmount;
        this.currentBalance += offsetAmount;

        System.out.printf(" [OK] Η τιμή άλλαξε επιτυχώς. Διαφορά: %,.2f €\n", offsetAmount);
        
        // Important: Recalculate to show the impact
        recalculateEsgScore(year);
        return;
      }
    }

    if (!found) {
      System.out.println(" Δεν βρέθηκε κατηγορία με το όνομα: '" + searchName + "'");
      System.out.println(" Συμβουλή: Προσέξτε τους τόνους και την ορθογραφία!");
    }
  }

  /**
   * Reads an integer choice from user input.
   *
   * @param maxOption Maximum valid option number
   * @return User's choice or -1 if invalid
   */
  private int readIntegerChoice(int maxOption) {
    try {
      String input = scanner.nextLine().trim();
      if (input.isEmpty()) {
        return -1;
      }
      int val = Integer.parseInt(input);
      if (val >= 0 && val <= maxOption) {
        return val;
      }
    } catch (NumberFormatException e) {
      // We ignore the mistake and return -1
    }
    System.out.println("Μη έγκυρη επιλογή.");
    return -1;
  }

  /**
   * Recalculates the ESG score after a budget change.
   *
   * @param year The current budget year
   */
  private void recalculateEsgScore(EnvYear year) {
    if (this.currentEsgReport == null) {
      return; // ESG tracking is disabled
    }

    System.out.println("%n Ανανέωση ESG Score...");

    try {
      EsgReport previousReport = this.currentEsgReport;
      this.currentEsgReport = esgCalculator.calculateReport(year, totalBudget);

      // Show comparison
      esgPrinter.printComparison(previousReport, currentEsgReport);
    } catch (Exception e) {
      System.err.println("  Σφάλμα κατά την ανανέωση ESG: " + e.getMessage());
    }
  }
}
