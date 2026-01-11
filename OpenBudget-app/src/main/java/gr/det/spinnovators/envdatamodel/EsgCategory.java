package gr.det.spinnovators.envdatamodel;

/**
 * Enumeration representing ESG (Environmental, Social, Governance) categories
 * for budget classification and sustainability scoring.
 *
 * <p>Each budget entry is classified into one of these categories to calculate
 * the overall ESG sustainability score of the ministry budget. This classification
 * helps assess how well the budget aligns with environmental, social, and
 * governance sustainability principles.</p>
 *
 * <p>The ESG scoring system uses these categories to evaluate:
 * <ul>
 *   <li>Environmental impact and climate action initiatives</li>
 *   <li>Social responsibility and personnel welfare programs</li>
 *   <li>Governance quality and administrative transparency</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public enum EsgCategory {
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
   *
   * <p>Examples include general operating expenses that don't have a clear
   * sustainability impact.</p>
   */
  NEUTRAL("Neutral", "Ουδέτερες");

  private final String nameEn;
  private final String nameEl;

  /**
   * Private constructor for EsgCategory enum constants.
   *
   * <p>This constructor is called internally when defining each enum constant
   * to initialize its English and Greek names.</p>
   *
   * @param nameEn the English name of the category
   * @param nameEl the Greek name of the category
   */
  EsgCategory(String nameEn, String nameEl) {
    this.nameEn = nameEn;
    this.nameEl = nameEl;
  }

  /**
   * Gets the English name of this category.
   *
   * @return English name.
   */
  public String getNameEn() {
    return nameEn;
  }

  /**
   * Gets the Greek name of this category.
   *
   * @return Greek name.
   */
  public String getNameEl() {
    return nameEl;
  }
}
