package gr.det.spinnovators.envdatamodel;

/**
 * Enumeration representing ESG (Environmental, Social, Governance) categories
 * for budget classification and sustainability scoring.
 *
 * Each budget entry is classified into one of these categories to calculate
 * the overall ESG sustainability score of the ministry budget.
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public enum ESG_Category {
    /**
     * Environmental category - expenses related to environmental protection,
     * renewable energy, climate action, and natural resource management.
     */
    ENVIRONMENTAL("Environmental", "Περιβαλλοντικές"),

    /**
     * Social category - expenses related to personnel welfare, community benefits,
     * and social responsibility initiatives.
     */
    SOCIAL("Social", "Κοινωνικές"),

    /**
     * Governance category - expenses related to administration, transparency,
     * coordination, and institutional management.
     */
    GOVERNANCE("Governance", "Διοίκηση"),

    /**
     * Neutral category - expenses that don't clearly fit into E, S, or G categories.
     * These are excluded from ESG score calculations.
     */
    NEUTRAL("Neutral", "Ουδέτερες");

    private final String nameEn;
    private final String nameEl;

    /**
     * Constructor for EsgCategory enum.
     *
     * @param icon Unicode emoji representing the category
     * @param nameEn English name of the category
     * @param nameEl Greek name of the category
     */
    ESG_Category(String nameEn, String nameEl) {
        this.nameEn = nameEn;
        this.nameEl = nameEl;
    }

    /**
     * Gets the English name of this category.
     * @return English name
     */
    public String getNameEn() {
        return nameEn;
    }

    /**
     * Gets the Greek name of this category.
     * @return Greek name
     */
    public String getNameEl() {
        return nameEl;
    }
}
