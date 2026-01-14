package gr.det.spinnovators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link OpenBudgetApplication} class.
 *
 * <p>This test suite simulates complete user sessions by redirecting standard 
 * input and output streams. It covers different user roles (Minister, Employee), 
 * full menu navigation, and various edge cases to ensure the CLI behaves correctly.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>Authentication for multiple roles</li>
 * <li>General and Ministry budget viewing</li>
 * <li>ESG reporting and comparison logic</li>
 * <li>Error handling for invalid inputs and missing data</li>
 * <li>Immediate exit and menu fallback scenarios</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class OpenBudgetApplicationTest {

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  /**
   * Sets up the output capture before each test to intercept console messages.
   * This allows us to verify the application's responses in a headless environment.
   */
  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8));
  }

  /**
   * Restores the original system streams after each test to prevent 
   * side effects on other tests in the suite.
   */
  @AfterEach
  void tearDown() {
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  /**
   * Tests a Minister login followed by an immediate exit choice.
   * Verifies the successful termination of the application.
   */
  @Test
  void ministerExitImmediately() {
    String input = "Minister\nm1n1st3r\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Έξοδος"), "Should display exit message for Minister");
  }

  /**
   * Tests an Employee login followed by an immediate exit choice.
   * Verifies that different roles can access the exit functionality.
   */
  @Test
  void employeeExitImmediately() {
    String input = "John\n3mpl0y33\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Έξοδος"), "Should display exit message for Employee");
  }

  /**
   * Tests the Minister's ability to view the General State Budget.
   * Simulates navigating to the General Budget menu and viewing a specific year.
   */
  @Test
  void ministerViewGeneralBudget() {
    String input = "Minister\nm1n1st3r\n1\n1\n2024\n2\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Κρατικός"), "Should display General Budget header");
  }

  /**
   * Tests the Employee's ability to view the General State Budget.
   * Simulates a standard employee session for budget data retrieval.
   */
  @Test
  void employeeViewGeneralBudget() {
    String input = "John\n3mpl0y33\n1\n1\n2023\n2\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Κρατικός"), "Should allow employee to view general budget");
  }

  /**
   * Tests viewing the Environment Ministry budget for a specific year.
   * Verifies that the ministry-specific data is correctly loaded and displayed.
   */
  @Test
  void ministerViewEnvBudgetYear() {
    String input = "Minister\nm1n1st3r\n2\n1\n2025\n5\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Υπουργείο"), "Should display Ministry-specific budget header");
  }

  /**
   * Tests the generation of an ESG sustainability report via the menu.
   * Ensures that the ESG calculator and reporter are properly integrated into the main loop.
   */
  @Test
  void ministerEsgReport() {
    String input = "Minister\nm1n1st3r\n2\n4\n2025\n5\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("ΑΝΑΦΟΡΑ ΒΙΩΣΙΜΟΤΗΤΑΣ"), "Should display ESG report title");
  }

  /**
   * Tests the error handling logic for invalid year inputs during comparison.
   * Verifies that the application does not crash when invalid data is provided.
   */
  @Test
  void ministerCompareInvalidYears() {
    String input = "Minister\nm1n1st3r\n2\n3\n1900\n1800\n5\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Σφάλμα"), "Should handle non-existent years gracefully");
  }

  /**
   * Tests the year-to-year comparison functionality for an employee.
   * Verifies the full path from login to comparison analysis.
   */
  @Test
  void employeeCompareYears() {
    String input = "John\n3mpl0y33\n2\n2\n2024\n2025\n4\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertFalse(output.isEmpty(), "Output should contain comparison data");
  }

  /**
   * Tests the robustness of the menu system when an invalid option is selected.
   * Verifies that the application prompts the user again instead of exiting.
   */
  @Test
  void invalidMenuChoice() {
    String input = "Minister\nm1n1st3r\n9\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Μη έγκυρη"), "Should notify user of invalid choice");
  }

  /**
   * Tests the authentication failure scenario.
   * Verifies that invalid credentials result in an error message.
   */
  @Test
  void authenticationFailure() {
    String input = "WrongUser\nWrongPass\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    OpenBudgetApplication.main(new String[]{});

    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(output.contains("Αποτυχία"), "Should notify user of login failure");
  }
}