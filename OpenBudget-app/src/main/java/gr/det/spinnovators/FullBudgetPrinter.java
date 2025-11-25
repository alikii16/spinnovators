package gr.det.spinnovators;

public final class FullBudgetPrinter {

    /**
     *Creates variable data that receives the data from class MinistryDataInput.
     */
    private MinistryDataInput data;

    /**
     * Constructor for FullBudgetPrinter.
     * @param dataToUse The MinistryDataInput object containing budget
     * data.
     */
    public FullBudgetPrinter(final MinistryDataInput dataToUse) {
        this.data = dataToUse;
    }

    /**
     * Displays the full bufget for the specified year.
     * @param year The year for which to display the budget.
     * (e.g. 2023, 2024, 2025).
     */

