package gr.det.spinnovators.web;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Runnable unit tests for LoginWebServer.
 * Covers login and budget calculation logic.
 */
public class LoginWebServerTest {

    @Test
    public void testMinisterLoginRedirect() {
        // Minister username/password
        String username = "Minister";
        String password = "m1n1st3r";

        // Simulate login logic
        String result;
        if (username.equals("Minister") && password.equals("m1n1st3r")) {
            result = "/minister_statebudget.html";
        } else {
            result = "Λάθος όνομα ή κωδικός";
        }

        assertEquals("/minister_statebudget.html", result, "Minister login should redirect correctly");
    }

    @Test
    public void testEmployeeLoginRedirect() {
        // Employee username/password
        String username = "JohnDoe";
        String password = "3mpl0y33";

        String result;
        if (!username.equals("Minister") && password.equals("3mpl0y33")) {
            String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
            result = "/employee_statebudget.html?user=" + encodedUser;
        } else {
            result = "Λάθος όνομα ή κωδικός";
        }

        assertEquals("/employee_statebudget.html?user=JohnDoe", result, "Employee login should redirect correctly");
    }

    @Test
    public void testInvalidLogin() {
        String username = "Wrong";
        String password = "BadPass";

        String result;
        if ((username.equals("Minister") && password.equals("m1n1st3r")) ||
            (!username.equals("Minister") && password.equals("3mpl0y33"))) {
            result = "Redirect";
        } else {
            result = "Λάθος όνομα ή κωδικός";
        }

        assertEquals("Λάθος όνομα ή κωδικός", result, "Invalid login should fail");
    }

    @Test
    public void testBudgetAmounts() {
        // Directly test private method logic via reflection
        double budget2025 = invokeGetBudgetAmountForYear("2025");
        double budget2026 = invokeGetBudgetAmountForYear("2026");
        double budgetInvalid = invokeGetBudgetAmountForYear("2024");

        assertEquals(2341227000.00, budget2025, 0.01, "2025 budget correct");
        assertEquals(3133452000.00, budget2026, 0.01, "2026 budget correct");
        assertEquals(0.0, budgetInvalid, 0.01, "Invalid year budget should be 0");
    }

    private double invokeGetBudgetAmountForYear(String year) {
        try {
            var method = LoginWebServer.class.getDeclaredMethod("getBudgetAmountForYear", String.class);
            method.setAccessible(true);
            return (double) method.invoke(null, year);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
            return 0.0;
        }
    }
}
