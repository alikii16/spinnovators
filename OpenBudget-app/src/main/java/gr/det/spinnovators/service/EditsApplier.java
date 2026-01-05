package gr.det.spinnovators.service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.printer.EditsPrinter;
import gr.det.spinnovators.printer.EsgPrinter;

/**
 * Applies edits to budget entries and tracks ESG sustainability impact.
 *
 * This class handles the interactive budget editing process, validates changes,
 * and calculates the ESG sustainability score before and after modifications.
 * The editing session continues until the budget is balanced (total changes sum to zero).
 *
 * Key features:
 *   Interactive console-based budget modification
 *   Real-time ESG impact tracking and reporting
 *   Smart validation with ESG-aware rules
 *   Comprehensive before/after comparison analysis
 *
 * @author Spinnovators Team
 * @version 3.0 (Optimized & ESG-enabled)
 */
public class EditsApplier {

  // Budget constants for different years
  private static final double BUDGET_2025 = 2341227000.00;
  private static final double BUDGET_2026 = 3133452000.00;
  private static final String YEAR_2025 = "2025";
  private static final String YEAR_2026 = "2026";

  // Validation threshold for budget balance
  private static final double BALANCE_TOLERANCE = 0.01;

  // Number formatting for Greek locale
  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
  private static final NumberFormat GREEK_NUMBER_FORMAT =
      NumberFormat.getNumberInstance(HELLENIC_LOCALE);

  private final EnvBudgetTranslator translator;
  private final Scanner scanner;
  private final EsgScoreCalculator esgCalculator;
  private final EsgPrinter esgPrinter;
  private final InitialBudgetComparison comparisonAnalyzer;
  private final BudgetValidator validator;

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
    this(translator, new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8));
  }

  /**
   * Constructs an EditsApplier with a custom Scanner for flexible input handling.
   * This constructor is useful for testing and alternative input sources.
   *
   * @param translator The service used for converting internal keys to readable text.
   * @param scanner The Scanner to use for reading user input.
   */
  public EditsApplier(EnvBudgetTranslator translator, Scanner scanner) {
    this.translator = translator;
    this.scanner = scanner;
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
    this.comparisonAnalyzer = new InitialBudgetComparison(translator);
    this.validator = new BudgetValidator();
  }

  /**
   * Overloaded constructor to allow Scanner injection (Dependency Injection).
   * Crucial for Unit Testing to share the input stream.
   *
   * @param translator The translator service.
   * @param scanner The shared scanner instance.
   */
  public EditsApplier(EnvBudgetTranslator translator, Scanner scanner) {
    this.translator = translator;
    this.scanner = scanner; // <--- Εδώ παίρνει τον "κοινό" Scanner
    this.esgCalculator = new EsgScoreCalculator();
    this.esgPrinter = new EsgPrinter();
    this.comparisonAnalyzer = new InitialBudgetComparison(translator);
  }
  
  /**
   * Initiates the interactive editing session for a specific fiscal year.
   *
   * The session continues until the user balances the budget
   * (difference between original and new total must be zero). During the session:
   *   Displays current balance and ESG score
   *   Allows user to select sectors, units, and entries to modify
   *   Validates all changes with ESG-aware rules
   *   Recalculates ESG impact after each change
   *   Performs comprehensive comparison at the end
   *
   * @param year The EnvYear object representing the budget to be modified.
   */
  public void applyEditsToYear(EnvYear year) {
    System.out.println("%n--- ΕΝΑΡΞΗ ΕΠΕΞΕΡΓΑΣΙΑΣ ΓΙΑ ΤΟ ΕΤΟΣ " + year.getYear() + " ---");

    initializeBudgetTracking(year);
    this.originalYearSnapshot = createDeepCopy(year);
    calculateAndDisplayInitialEsg(year);

    boolean keepEditing = true;

    while (keepEditing) {
      displayCurrentStatus();

      EnvSector selectedSector = selectSector(year);

      if (selectedSector == null) {
        keepEditing = handleExitRequest();
        continue;
      }

      EnvUnit selectedUnit = selectUnit(selectedSector);
      if (selectedUnit == null) {
        continue;
      }

      processUnitEdit(selectedUnit, year);
    }

    finalizeEditingSession(year);
  }

  /**
   * Initializes budget tracking variables based on the selected year.
   *
   * @param year The budget year being edited.
   */
  private void initializeBudgetTracking(EnvYear year) {
    String yearString = year.getYear();

    if (YEAR_2025.equals(yearString)) {
      this.totalBudget = BUDGET_2025;
    } else if (YEAR_2026.equals(yearString)) {
      this.totalBudget = BUDGET_2026;
    }

    this.currentBalance = 0.0;
  }

  /**
   * Displays the current budget balance and ESG score status.
   */
  private void displayCurrentStatus() {
    if (Math.abs(currentBalance) > BALANCE_TOLERANCE) {
      System.out.printf("%n>>> ΥΠΟΛΟΙΠΟ ΓΙΑ ΙΣΟΣΚΕΛΙΣΜΟ: %,.2f € <<<%n", this.currentBalance);
    }

    if (currentEsgReport != null) {
      esgPrinter.printCompactSummary(currentEsgReport);
    }
  }

  /**
   * Handles the user's request to exit the editing session.
   * Checks if the budget is balanced before allowing exit.
   *
   * @return false if exit is allowed (budget balanced), true to continue editing.
   */
  private boolean handleExitRequest() {
    if (Math.abs(this.currentBalance) < BALANCE_TOLERANCE) {
      System.out.println(" Ο προϋπολογισμός είναι ισοσκελισμένος!");
      System.out.println(" Τερματισμός Λειτουργίας.");
      return false; // EXIT LOOP
    } else {
      System.out.println("!!! ΠΡΟΣΟΧΗ !!!");
      System.out.println(" Δεν επιτρέπεται τερματισμός.");
      System.out.println(" Ο προϋπολογισμός δεν ισοσκελίστηκε.");
      System.out.printf(" Πρέπει να καλύψετε διαφορά: %,.2f €%n", currentBalance);
      return true; // CONTINUE EDITING
    }
  }

  /**
   * Processes the editing of entries within a selected unit.
   *
   * @param unit The selected unit containing entries to edit.
   * @param year The current budget year.
   */
  private void processUnitEdit(EnvUnit unit, EnvYear year) {
    System.out.println("%n------------------------------------------------");
    System.out.println("Μονάδα: " + translator.translateCategory(unit.getJsonKey()));
    System.out.println("Πληκτρολογήστε το όνομα της κατηγορίας που θέλετε να επεξεργαστείτε:");
    System.out.print("--> ");

    String searchInput = scanner.nextLine().trim();

    if (!searchInput.isEmpty()) {
      findAndEditEntryInUnit(unit, searchInput, year);
    }
  }

  /**
   * Finalizes the editing session by printing results and performing comparison.
   *
   * @param year The modified budget year.
   */
  private void finalizeEditingSession(EnvYear year) {
    EditsPrinter printer = new EditsPrinter(translator);
    printer.printEditYear(year);

    System.out.println("%n Προετοιμασία αναλυτικής σύγκρισης...%n");

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
   * This method creates a complete independent copy of the year's data structure,
   * including all sectors, units, and entries, to preserve the original state
   * for later comparison analysis.
   *
   * @param year The year to copy.
   * @return A deep copy of the year with independent objects.
   */
  private EnvYear createDeepCopy(EnvYear year) {
    List<EnvSector> copiedSectors = new java.util.ArrayList<>();

    for (EnvSector sector : year.getSectors()) {
      List<EnvUnit> copiedUnits = new java.util.ArrayList<>();

      for (EnvUnit unit : sector.getUnits()) {
        List<EnvEntry> copiedEntries = new java.util.ArrayList<>();

        for (EnvEntry entry : unit.getEntries()) {
          copiedEntries.add(new EnvEntry(entry.getJsonKey(), entry.getAmount()));
        }

        copiedUnits.add(new EnvUnit(unit.getJsonKey(), copiedEntries));
      }

      copiedSectors.add(new EnvSector(sector.getJsonKey(), copiedUnits));
    }

    return new EnvYear(year.getYear(), copiedSectors);
  }

  /**
   * Calculates and displays the initial ESG sustainability report.
   *
   * This method computes the baseline ESG score before any modifications,
   * providing users with a reference point to understand how their changes
   * will impact environmental, social, and governance metrics.
   *
   * @param year The budget year to analyze.
   */
  private void calculateAndDisplayInitialEsg(EnvYear year) {
    System.out.println("%n Υπολογισμός αρχικού ESG Score...%n");

    try {
      this.initialEsgReport = esgCalculator.calculateReport(year, totalBudget);
      this.currentEsgReport = initialEsgReport;

      esgPrinter.printReport(initialEsgReport);

      System.out.println(" Μπορείτε να δείτε πώς οι αλλαγές σας επηρεάζουν το ESG score!");
      System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%n");
    } catch (Exception e) {
      System.err.println("  Σφάλμα κατά τον υπολογισμό ESG: " + e.getMessage());
      System.out.println(" Η επεξεργασία θα συνεχιστεί χωρίς ESG tracking.%n");
      this.initialEsgReport = null;
      this.currentEsgReport = null;
    }
  }

  /**
   * Presents a list of sectors to the user for selection.
   *
   * Displays all available sectors in a numbered menu format and
   * allows the user to choose one for editing or exit the session.
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
   * Displays all administrative units within the selected sector
   * in a numbered menu format.
   *
   * @param sector The parent sector containing units.
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
   * Searches for a budget entry within a unit by name and allows editing.
   *
   * This method performs case-insensitive search for the specified entry,
   * validates the new amount with ESG-aware rules, updates the entry,
   * tracks the balance change, and recalculates the ESG score.
   *
   * @param unit The unit to search in.
   * @param searchName The category name to search for.
   * @param year The current budget year (needed for ESG context).
   */
  private void findAndEditEntryInUnit(EnvUnit unit, String searchName, EnvYear year) {
    boolean found = false;

    for (EnvEntry entry : unit.getEntries()) {
      String entryName = translator.translateCategory(entry.getJsonKey());

      if (entryName.equalsIgnoreCase(searchName)) {
        found = true;
        editBudgetEntry(entry, unit, year);
        return;
      }
    }

    if (!found) {
      System.out.println(" Δεν βρέθηκε κατηγορία με το όνομα: '" + searchName + "'");
      System.out.println(" Συμβουλή: Προσέξτε τους τόνους και την ορθογραφία!");
    }
  }

  /**
   * Handles the interactive editing of a single budget entry.
   *
   * This method:
   *   Displays current amount
   *   Prompts for new amount
   *   Validates with ESG-aware rules
   *   Handles validation failures with informative messages
   *   Updates the entry only after successful validation
   *   Tracks balance changes
   *   Recalculates ESG impact
   *
   * @param entry The budget entry to edit.
   * @param unit The unit containing the entry (for ESG context).
   * @param year The current budget year (for ESG context).
   */
  private void editBudgetEntry(EnvEntry entry, EnvUnit unit, EnvYear year) {
    String entryName = translator.translateCategory(entry.getJsonKey());
    double oldAmount = entry.getAmount();

    System.out.printf("%nΒρέθηκε: %s | Τρέχον Ποσό: %,.2f €%n", entryName, oldAmount);

    Double validatedAmount = promptAndValidateAmount(oldAmount, entry, unit, year);

    if (validatedAmount != null) {
      applyBudgetChange(entry, oldAmount, validatedAmount, year);
    }
  }

  /**
   * Prompts user for new amount and validates it with ESG-aware rules.
   *
   * Continues prompting until a valid amount is entered or user cancels.
   * Handles all validation scenarios including ESG protection rules.
   *
   * @param oldAmount The current amount.
   * @param entry The entry being edited.
   * @param unit The unit containing the entry.
   * @param year The current budget year.
   * @return The validated amount, or null if user cancelled.
   */
  private Double promptAndValidateAmount(double oldAmount, EnvEntry entry,
                                        EnvUnit unit, EnvYear year) {
    while (true) {
      System.out.print("Εισάγετε το νέο ποσό: ");
      String amountInput = scanner.nextLine().trim();

      if (amountInput.isEmpty()) {
        System.out.println(" Δεν δόθηκε τιμή. Καμία αλλαγή.");
        return null;
      }

      try {
        double inputAmount = parseAmount(amountInput);
        String sectorKey = findSectorKeyForUnit(unit, year);

        BudgetValidator.ValidationResult result =
            validator.validate(this.totalBudget, oldAmount, inputAmount,
                sectorKey, entry.getJsonKey());

        ValidationOutcome outcome = handleValidationResult(result, validator,
            oldAmount, inputAmount);

        if (outcome == ValidationOutcome.ACCEPTED) {
          return inputAmount;
        } else if (outcome == ValidationOutcome.CANCELLED) {
          return null;
        }
        // Otherwise, continue loop for new input

      } catch (ParseException e) {
        System.out.println(" ΣΦΑΛΜΑ: Μη έγκυρη μορφή αριθμού.");
      }
    }
  }

  /**
   * Parses a monetary amount from user input using Greek locale formatting.
   *
   * Handles various input formats including decimals and thousands separators.
   *
   * @param input The user input string.
   * @return The parsed amount.
   * @throws ParseException If the input cannot be parsed as a number.
   */
  private double parseAmount(String input) throws ParseException {
    try {
      Number number = GREEK_NUMBER_FORMAT.parse(input);
      return number.doubleValue();
    } catch (ParseException e) {
      // Fallback: Try simple dot replacement for backwards compatibility
      String normalized = input.replace(",", ".");
      return Double.parseDouble(normalized);
    }
  }

  /**
   * Finds the sector key for a given unit within the budget year.
   *
   * @param unit The unit to locate.
   * @param year The budget year.
   * @return The sector key, or "unknown" if not found.
   */
  private String findSectorKeyForUnit(EnvUnit unit, EnvYear year) {
    return year.getSectors().stream()
        .filter(s -> s.getUnits().contains(unit))
        .findFirst()
        .map(EnvSector::getJsonKey)
        .orElse("unknown");
  }

  /**
   * Enumeration for validation outcome decisions.
   */
  private enum ValidationOutcome {
    ACCEPTED,    // Amount is valid and accepted
    REJECTED,    // Amount failed validation, prompt again
    CANCELLED    // User cancelled the change
  }

  /**
   * Handles the validation result and returns the appropriate outcome.
   *
   * Displays appropriate messages for each validation scenario and
   * handles user confirmation for warnings.
   *
   * @param result The validation result from BudgetValidator.
   * @param validator The validator instance (for calculating deviation).
   * @param oldAmount The original amount.
   * @param newAmount The proposed new amount.
   * @return The validation outcome (ACCEPTED, REJECTED, or CANCELLED).
   */
  private ValidationOutcome handleValidationResult(
      BudgetValidator.ValidationResult result,
      BudgetValidator validator,
      double oldAmount,
      double newAmount) {

    if (result == BudgetValidator.ValidationResult.OK) {
      return ValidationOutcome.ACCEPTED;

    } else if (result == BudgetValidator.ValidationResult.NEGATIVE_VALUE) {
      System.out.println(" ΣΦΑΛΜΑ: Μη έγκυρη τιμή (αρνητικό ποσό).");
      return ValidationOutcome.REJECTED;

    } else if (result == BudgetValidator.ValidationResult.EXCEEDS_TOTAL_BUDGET) {
      System.out.printf(" ΣΦΑΛΜΑ: Υπέρβαση Ορίου Προϋπολογισμού (Όριο: %,.2f €).%n",
          this.totalBudget);
      return ValidationOutcome.REJECTED;

    } else if (result == BudgetValidator.ValidationResult.ESG_ENV_PROTECTION) {
      System.out.println(" ΠΡΟΣΤΑΣΙΑ ΠΕΡΙΒΑΛΛΟΝΤΟΣ: Η μείωση δαπανών άνω του "
          + "5% απαγορεύεται.");
      System.out.println(" Στόχος: Διατήρηση υψηλού δείκτη βιωσιμότητας "
          + "(Environmental Score).");
      return ValidationOutcome.REJECTED;

    } else if (result == BudgetValidator.ValidationResult.ESG_GOV_RESTRICTION) {
      System.out.println(" ΔΗΜΟΣΙΟΝΟΜΙΚΗ ΠΕΙΘΑΡΧΙΑ: Η αύξηση διοικητικών δαπανών "
          + "άνω του 10% απαγορεύεται.");
      System.out.println(" Στόχος: Περιορισμός της γραφειοκρατίας και εξοικονόμηση "
          + "πόρων (Governance Score).");
      return ValidationOutcome.REJECTED;

    } else if (result == BudgetValidator.ValidationResult.ESG_SOCIAL_PROTECTION) {
      System.out.println(" ΚΟΙΝΩΝΙΚΗ ΠΟΛΙΤΙΚΗ: Η μείωση μισθών/παροχών άνω του "
          + "10% απαγορεύεται.");
      System.out.println(" Στόχος: Προστασία του βιοτικού επιπέδου των εργαζομένων "
          + "(Social Score).");
      return ValidationOutcome.REJECTED;

    } else if (result == BudgetValidator.ValidationResult.EXTREME_DEVIATION) {
      return handleExtremeDeviationWarning(validator, oldAmount, newAmount);
    }

    return ValidationOutcome.REJECTED;
  }

  /**
   * Handles the extreme deviation warning by asking user for confirmation.
   *
   * @param validator The validator instance.
   * @param oldAmount The original amount.
   * @param newAmount The proposed new amount.
   * @return ACCEPTED if user confirms, CANCELLED if user declines.
   */
  private ValidationOutcome handleExtremeDeviationWarning(
      BudgetValidator validator,
      double oldAmount,
      double newAmount) {

    double deviation = validator.calculateDeviationPercentage(oldAmount, newAmount);
    System.out.printf(" ΠΡΟΕΙΔΟΠΟΙΗΣΗ: Παρατηρείται μεγάλη απόκλιση (%.2f%%).%n",
        deviation);
    System.out.print(" Επιθυμείτε να προχωρήσετε παρόλα αυτά; (ΝΑΙ/ΟΧΙ): ");

    String confirm = scanner.nextLine().trim();
    if (confirm.equalsIgnoreCase("ΝΑΙ")) {
      return ValidationOutcome.ACCEPTED;
    } else {
      System.out.println(" Η αλλαγή ακυρώθηκε. Παρακαλώ εισάγετε νέα τιμή.");
      return ValidationOutcome.CANCELLED;
    }
  }

  /**
   * Applies the validated budget change to the entry and updates tracking.
   *
   * This method:
   *   Updates the entry with the new amount
   *   Calculates and tracks the balance offset
   *   Displays confirmation message
   *   Recalculates ESG score to show impact
   *
   * @param entry The entry to update.
   * @param oldAmount The previous amount.
   * @param newAmount The validated new amount.
   * @param year The current budget year (for ESG recalculation).
   */
  private void applyBudgetChange(EnvEntry entry, double oldAmount,
                                double newAmount, EnvYear year) {
    entry.setAmount(newAmount);
    double offsetAmount = oldAmount - newAmount;
    this.currentBalance += offsetAmount;

    System.out.printf(" [OK] Η τιμή άλλαξε επιτυχώς. Διαφορά: %,.2f €%n", offsetAmount);

    recalculateEsgScore(year);
  }

  /**
   * Reads an integer choice from user input with validation.
   *
   * Accepts values from 0 to maxOption inclusive. Returns -1 for invalid input.
   *
   * @param maxOption Maximum valid option number.
   * @return User's choice (0 to maxOption), or -1 if invalid.
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
      // Invalid input, will return -1
    }
    System.out.println(" Μη έγκυρη επιλογή.");
    return -1;
  }

  /**
   * Recalculates the ESG sustainability score after a budget change.
   *
   * Updates the current ESG report and displays a comparison showing
   * how the change impacted environmental, social, and governance scores.
   *
   * @param year The current budget year with modified data.
   */
  private void recalculateEsgScore(EnvYear year) {
    if (this.currentEsgReport == null) {
      return; // ESG tracking is disabled
    }

    System.out.println("%n Ανανέωση ESG Score...");

    try {
      EsgReport previousReport = this.currentEsgReport;
      this.currentEsgReport = esgCalculator.calculateReport(year, totalBudget);

      esgPrinter.printComparison(previousReport, currentEsgReport);
    } catch (Exception e) {
      System.err.println("  Σφάλμα κατά την ανανέωση ESG: " + e.getMessage());
    }
  }
}
