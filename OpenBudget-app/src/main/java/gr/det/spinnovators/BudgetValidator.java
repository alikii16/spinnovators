package gr.det.spinnovators;

import java.util.Scanner;

/**
 * Class for validating changes to the budget according to specific constraints.
 * Applies checks for:
 * - Empty input
 * - Negative values
 * - Values exceeding the total ministry budget
 * - Extreme deviations (>20%)
 */
public class BudgetValidator {

    private final Scanner scanner;

    /**
     * Constructor - creates a new BudgetValidator with its own Scanner
     */
    public BudgetValidator() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Validates a new budget value.
     * If validation fails, requests a new value from the user until a valid value is entered.
     *
     * @param totalMinistryBudget Total budget of the ministry for the current year
     * @param oldValue Existing (old) value of the category
     * @param initialNewValue Initial new value entered by the user
     * @return The validated new value after all checks
     */
    public double getValidatedNewValue(double totalMinistryBudget, double oldValue, double initialNewValue) {
        double currentValue = initialNewValue;

        System.out.printf("Old value: %,.2f € | Total ministry budget: %,.2f €\n\n",
                         oldValue, totalMinistryBudget);

        while (true) {
            // CHECK 1: Negative value
            if (currentValue < 0) {
                System.out.println("ERROR: Value cannot be negative.");
                currentValue = getNewInputFromUser();
                continue;
            }

            // CHECK 2: Value exceeds total budget
            if (currentValue > totalMinistryBudget) {
                System.out.printf("ERROR: Value (%,.2f €) exceeds total ministry budget (%,.2f €).\n",
                                 currentValue, totalMinistryBudget);
                currentValue = getNewInputFromUser();
                continue;
            }

            // CHECK 3: Extreme deviation (> 20%)
            if (isExtremeDeviation(oldValue, currentValue)) {
                double deviation = calculateDeviationPercentage(oldValue, currentValue);

                System.out.println("\n==============================================");
                System.out.println("WARNING: EXTREME BUDGET CHANGE");
                System.out.println("==============================================");
                System.out.printf("Percentage change: %6.2f%%\n", deviation);
                System.out.println("Change exceeds the 20% limit!");
                System.out.println("==============================================\n");

                System.out.print("Are you sure you want to apply this value? (yes/no): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("yes")) {
                    System.out.println("Confirmed: Extreme value will be applied.\n");
                    return currentValue;
                } else if (confirmation.equals("no")) {
                    System.out.println("Cancelled: Please enter a new value.\n");
                    currentValue = getNewInputFromUser();
                    continue;
                } else {
                    System.out.println("Invalid response. Please answer 'yes' or 'no'.\n");
                    currentValue = getNewInputFromUser();
                    continue;
                }
            }

            // Value passed all checks successfully
            System.out.println("Value validated successfully!\n");
            return currentValue;
        }
    }

    /**
     * Prompts the user to enter a new value and handles input errors.
     * Checks for empty or non-numeric input.
     *
     * @return A valid numeric value (double)
     */
    private double getNewInputFromUser() {
        while (true) {
            System.out.print("Please enter a new budget value: ");
            String input = scanner.nextLine().trim();

            // Check for empty input
            if (input.isEmpty()) {
                System.out.println("ERROR: Value cannot be empty.\n");
                continue;
            }

            // Attempt to convert to a number
            try {
                double value = Double.parseDouble(input);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid numeric format. Please enter a number.\n");
            }
        }
    }

    /**
     * Checks if the deviation between old and new values is extreme (>20%).
     * @param oldValue Old value
     * @param newValue New value
     * @return true if deviation > 20%, false otherwise
     */
    private boolean isExtremeDeviation(double oldValue, double newValue) {
        // Calculate percentage deviation
        double percentageDeviation = Math.abs((newValue - oldValue) / oldValue) * 100.0;
        return percentageDeviation > 20.0;
    }

    /**
     * Calculates the percentage deviation between old and new values!
     * @param oldValue Old value
     * @param newValue New value
     * @return Deviation percentage (e.g., 25.5 for 25.5%)
     */
    private double calculateDeviationPercentage(double oldValue, double newValue) {

        return Math.abs((newValue - oldValue) / oldValue) * 100.0;
    }

    /**
     * Closes the Scanner when no longer needed.
     */
    public void closeScanner() {
        if (this.scanner != null) {
            this.scanner.close();
        }
    }
}
