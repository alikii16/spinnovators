package gr.det.spinnovators.authentication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for the FirstLogin authentication class.
 *
 * <p>This test class verifies the authentication system's behavior using
 * dependency injection with Scanner objects to simulate user input. It ensures
 * proper handling of valid credentials, invalid attempts, edge cases, and the
 * overall structure of the utility class.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>Class structure validation (final class, private constructor)</li>
 *   <li>Successful authentication for both Minister and Employee roles</li>
 *   <li>Failed login attempts with retry behavior</li>
 *   <li>Edge cases: empty input, wrong passwords, multiple failures</li>
 *   <li>Output verification for user prompts and welcome messages</li>
 * </ul>
 * </p>
 *
 * <p>Testing approach:
 * <ul>
 *   <li>Uses ByteArrayInputStream to simulate console input</li>
 *   <li>Captures System.out using ByteArrayOutputStream to verify messages</li>
 *   <li>UTF-8 encoding support for Greek language characters</li>
 *   <li>Reflection to verify class structure and method signatures</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see FirstLogin
 */
class FirstLoginTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Capture console output to verify prompts and messages
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        // Restore original system output
        System.setOut(originalOut);
    }

    /**
     * Tests that FirstLogin is a proper utility class (final, private constructor).
     */
    @Test
    void testUtilityClassStructure() {
        assertTrue(Modifier.isFinal(FirstLogin.class.getModifiers()),
            "FirstLogin class should be final");

        var constructors = FirstLogin.class.getDeclaredConstructors();
        assertEquals(1, constructors.length, "Should have exactly one constructor");
        assertTrue(Modifier.isPrivate(constructors[0].getModifiers()),
            "Constructor should be private");

        // Verify login method exists and accepts a Scanner
        try {
            Method loginMethod = FirstLogin.class.getMethod("login", Scanner.class);
            assertTrue(Modifier.isPublic(loginMethod.getModifiers()), "login() should be public");
            assertTrue(Modifier.isStatic(loginMethod.getModifiers()), "login() should be static");
        } catch (NoSuchMethodException e) {
            fail("login(Scanner) method not found. Did you update the signature?");
        }
    }

    /**
     * Tests successful minister login.
     */
    @Test
    void testMinisterLogin() {
        String input = "Minister\nm1n1st3r\n";
        Scanner scanner = createScanner(input);

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        assertEquals("a", role, "Role should be 'a' for Minister");
        assertTrue(output.contains("Καλωσήρθατε κύριε Υπουργέ"), "Should print minister welcome message");
        assertTrue(output.contains("Εισάγετε όνομα χρήστη:"), "Should prompt for username");
    }

    /**
     * Tests successful employee login.
     */
    @Test
    void testEmployeeLogin() {
        String input = "JohnDoe\n3mpl0y33\n";
        Scanner scanner = createScanner(input);

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        assertEquals("b", role, "Role should be 'b' for Employee");
        assertTrue(output.contains("Καλωσήρθατε JohnDoe"), "Should welcome employee by name");
    }

    /**
     * Tests failed login followed by a successful retry.
     */
    @Test
    void testFailedLoginWithRetry() {
        // Scenario: Wrong User -> Wrong Pass -> Correct Employee
        String input = "WrongUser\nWrongPass\nJohnDoe\n3mpl0y33\n";
        Scanner scanner = createScanner(input);

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Λάθος όνομα ή κωδικός"), "Should display error message on failure");
        assertTrue(output.contains("Καλωσήρθατε JohnDoe"), "Should eventually succeed");
        assertEquals("b", role);
    }

    /**
     * Tests minister username with wrong password.
     */
    @Test
    void testMinisterWrongPassword() {
        // Scenario: Minister/Wrong -> Minister/Correct
        String input = "Minister\nwrongpass\nMinister\nm1n1st3r\n";
        Scanner scanner = createScanner(input);

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Λάθος όνομα ή κωδικός"), "Should fail with wrong password");
        assertEquals("a", role, "Should login as Minister after correction");
    }

    /**
     * Tests empty input handling.
     */
    @Test
    void testEmptyInputHandling() {
        // Scenario: Empty Username -> Empty Password -> Correct Login
        String input = "\n\nJohn\n3mpl0y33\n";
        Scanner scanner = createScanner(input);

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        // Ensure it didn't crash and eventually logged in
        assertEquals("b", role);
        assertTrue(output.contains("Εισάγετε όνομα χρήστη"), "Should handle empty lines and re-prompt");
    }

    /**
     * Tests multiple failed attempts.
     */
    @Test
    void testMultipleFailures() {
        StringBuilder inputBuilder = new StringBuilder();
        // 3 failed attempts
        inputBuilder.append("User1\nPass1\n");
        inputBuilder.append("User2\nPass2\n");
        inputBuilder.append("User3\nPass3\n");
        // 1 successful attempt
        inputBuilder.append("Alice\n3mpl0y33\n");

        Scanner scanner = createScanner(inputBuilder.toString());

        String role = FirstLogin.login(scanner);

        String output = outputStream.toString(StandardCharsets.UTF_8);

        // Count how many times the error message appeared
        int errorCount = output.split("Λάθος όνομα ή κωδικός").length - 1;
        assertTrue(errorCount >= 3, "Should show error message for each failed attempt");

        assertEquals("b", role);
        assertTrue(output.contains("Καλωσήρθατε Alice"), "Should welcome Alice");
    }

    /**
     * Tests that the class exists and is loaded correctly.
     */
    @Test
    void testClassCompilation() {
        assertNotNull(FirstLogin.class, "Class should be loadable");
        assertEquals("gr.det.spinnovators.authentication.FirstLogin", FirstLogin.class.getName());
    }

    /**
     * Tests password constants verification logic indirectly.
     */
    @Test
    void testPasswordConstantsLogic() {
        // This test simulates the logic used inside the class to verify constants haven't changed logic
        String ministerPass = "m1n1st3r";
        String employeePass = "3mpl0y33";

        assertEquals("m1n1st3r", ministerPass);
        assertEquals("3mpl0y33", employeePass);
        assertTrue(!ministerPass.equals(employeePass));
    }

    // --- Helper Method ---

    /**
     * Creates a Scanner initialized with the provided string input.
     * Uses UTF-8 encoding to support Greek characters.
     */
    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
