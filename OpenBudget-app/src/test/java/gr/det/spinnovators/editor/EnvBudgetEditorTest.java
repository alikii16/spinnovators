package gr.det.spinnovators.editor;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Unit tests for the {@link EnvBudgetEditor} class.
 *
 * <p>This test class verifies the interactive editing session flow,
 * ensuring that user inputs are handled correctly through console
 * input/output simulation. The tests validate user confirmation,
 * year selection, and error handling mechanisms.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>User declining to start the editing session</li>
 *   <li>Invalid year input handling and error messages</li>
 *   <li>Successful editing session initiation for year 2025</li>
 *   <li>Successful editing session initiation for year 2026</li>
 *   <li>Console output verification for all scenarios</li>
 *   <li>Complete branch coverage of the conditional logic</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvBudgetEditorTest {

  private EnvBudgetData dummyData;
  private EnvBudgetTranslator translator;

  // Captures the console output for verification assertions
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() {
    // 1. Prepare Dummy Data for ALL scenarios (2025, 2026, 2099)
    EnvYear year2025 = new EnvYear("2025", new ArrayList<EnvSector>());
    EnvYear year2026 = new EnvYear("2026", new ArrayList<EnvSector>());
    EnvYear year2099 = new EnvYear("2099", new ArrayList<EnvSector>());

    Map<String, EnvYear> years = new HashMap<>();
    years.put("2025", year2025);
    years.put("2026", year2026);
    years.put("2099", year2099);

    Map<String, Double> totals = new HashMap<>();
    totals.put("2025", 100000.0);
    totals.put("2026", 120000.0);

    dummyData = new EnvBudgetData(years, totals);

    // 2. Initialize Translator
    translator = new EnvBudgetTranslator();

    // 3. Redirect System.out to capture output
    System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8));
  }

  /**
   * Tests the scenario where the user declines to start the editing session.
   * <p>
   * Input: "ΟΧΙ"
   * Expected: Confirmation prompt appears, Year prompt does NOT appear.
   * </p>
   */
  @Test
  public void testStartEditingSession_UserDeclines() {
    String input = "ΟΧΙ\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));


    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    EnvBudgetEditor editor = new EnvBudgetEditor(dummyData, translator, scanner);

    editor.startEditingSession();

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Θέλετε να προχωρήσετε"),
        "failure - Editor should ask for confirmation prompt.");
    Assertions.assertFalse(output.contains("Επιλέξτε το έτος"),
        "failure - Editor should NOT ask for year selection if user says NO.");
  }

  /**
   * Tests the scenario where the user provides an invalid year.
   * <p>
   * Input: "ΝΑΙ" -> "2099"
   * Expected: Error message "Δεν βρέθηκαν δεδομένα".
   * This covers the 'TRUE && TRUE' branch of the condition.
   * </p>
   */
  @Test
  public void testStartEditingSession_InvalidYear() {
    String input = "ΝΑΙ\n2099\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    EnvBudgetEditor editor = new EnvBudgetEditor(dummyData, translator, scanner);

    editor.startEditingSession();

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Επιλέξτε το έτος"),
        "failure - Editor should ask for year selection.");
    Assertions.assertTrue(output.contains("Σφάλμα: Δεν βρέθηκαν δεδομένα"),
        "failure - Editor should print error message for invalid year.");
  }

  /**
   * Tests the successful flow ("Happy Path") for 2025.
   * <p>
   * Input: "ΝΑΙ" -> "2025" -> "0"
   * This covers the 'FALSE && ...' branch (Short-circuit).
   * </p>
   */
  @Test
  public void testStartEditingSession_SuccessFlow2025() {
    String input = "ΝΑΙ\n2025\n0\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    EnvBudgetEditor editor = new EnvBudgetEditor(dummyData, translator, scanner);

    editor.startEditingSession();

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);

    Assertions.assertTrue(output.contains("ΕΝΑΡΞΗ ΕΠΕΞΕΡΓΑΣΙΑΣ"),
        "failure - Should print start message for valid year 2025.");
  }

  /**
   * Tests the successful flow ("Happy Path") for 2026.
   * <p>
   * Input: "ΝΑΙ" -> "2026" -> "0"
   * This covers the 'TRUE && FALSE' branch, completing the coverage.
   * </p>
   */
  @Test
  public void testStartEditingSession_SuccessFlow2026() {
    String input = "ΝΑΙ\n2026\n0\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    EnvBudgetEditor editor = new EnvBudgetEditor(dummyData, translator, scanner);

    editor.startEditingSession();

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);

    Assertions.assertTrue(output.contains("ΕΝΑΡΞΗ ΕΠΕΞΕΡΓΑΣΙΑΣ"),
        "failure - Should print start message for valid year 2026.");
  }
}
