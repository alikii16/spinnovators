package gr.det.spinnovators.service;

import java.util.HashMap;
import java.util.Map;

import gr.det.spinnovators.envdatamodel.EsgCategory;


/**
 * Classifies budget sectors, units, and entries into ESG categories.
 *
 * <p>Uses refined mappings for balanced ESG scoring that reflects real-world
 * budget allocation across Environmental, Social, and Governance categories.
 *
 * @author Spinnovators Team
 * @version 3.0 (Balanced Classification with Unit support)
 */
public class EsgClassifier {

  // Sector-level classifications
  private static final Map<String, EsgCategory> SECTOR_CLASSIFICATIONS = new HashMap<>();

  // Entry-level classifications
  private static final Map<String, EsgCategory> ENTRY_CLASSIFICATIONS = new HashMap<>();

  // Unit-level classifications (for recovery funds and special cases)
  private static final Map<String, EsgCategory> UNIT_CLASSIFICATIONS = new HashMap<>();

  static {
    // ==================== SECTOR CLASSIFICATIONS ====================

    // Environmental sectors
    SECTOR_CLASSIFICATIONS.put("natural_environment_and_water_protection",
        EsgCategory.ENVIRONMENTAL);
    SECTOR_CLASSIFICATIONS.put("spatial_planning_and_urban_environment",
        EsgCategory.ENVIRONMENTAL);

    // Governance sector
    SECTOR_CLASSIFICATIONS.put("executive_coordination_and_investments",
        EsgCategory.GOVERNANCE);

    // ==================== ENTRY CLASSIFICATIONS ====================

    // Social entries (highest priority - apply across all sectors)
    ENTRY_CLASSIFICATIONS.put("personnel_costs", EsgCategory.SOCIAL);
    ENTRY_CLASSIFICATIONS.put("community_benefits", EsgCategory.SOCIAL);

    // Governance entries
    ENTRY_CLASSIFICATIONS.put("purchase_of_goods_and_services",
        EsgCategory.GOVERNANCE);

    // ==================== UNIT CLASSIFICATIONS ====================

    // Recovery and Resilience Fund units - Mixed approach for balance
    // These are EU funds split across: Green (60%), Social (25%), Governance (15%)

    // Environmental units
    UNIT_CLASSIFICATIONS.put("general_secretariat_for_natural_environment_and_water",
        EsgCategory.ENVIRONMENTAL);
    UNIT_CLASSIFICATIONS.put("general_secretariat_for_waste_management",
        EsgCategory.ENVIRONMENTAL);
    UNIT_CLASSIFICATIONS.put("general_secretariat_for_forestry",
        EsgCategory.ENVIRONMENTAL);
    UNIT_CLASSIFICATIONS.put("general_secretariat_for_spatial_and_urban_planning",
        EsgCategory.ENVIRONMENTAL);
    UNIT_CLASSIFICATIONS.put("general_secretariat_for_energy_and_mineral_resources",
        EsgCategory.ENVIRONMENTAL);

    // Governance units
    UNIT_CLASSIFICATIONS.put("ministerial_secretariats_and_offices",
        EsgCategory.GOVERNANCE);
    UNIT_CLASSIFICATIONS.put("other_ministerial_units",
        EsgCategory.GOVERNANCE);
    UNIT_CLASSIFICATIONS.put("other_units_minister_secretary",
        EsgCategory.GOVERNANCE);
    UNIT_CLASSIFICATIONS.put("other_ministerial_units_sp_ue",
        EsgCategory.GOVERNANCE);
    UNIT_CLASSIFICATIONS.put("other_ministerial_units_energy_mgt",
        EsgCategory.GOVERNANCE);
  }

  /**
   * Classifies a budget entry based on sector, unit, and entry type.
   *
   * <p>Classification priority:
   * <ol>
   *   <li>Entry-specific (e.g., personnel_costs → SOCIAL)</li>
   *   <li>Recovery funds special handling</li>
   *   <li>Unit-level classification</li>
   *   <li>Sector-level classification</li>
   *   <li>Energy sector special cases</li>
   * </ol>
   *
   * @param sectorKey The JSON key of the sector
   * @param unitKey The JSON key of the unit
   * @param entryKey The JSON key of the entry
   * @return The ESG category for this entry
   */
  public EsgCategory classifyEntry(String sectorKey, String unitKey, String entryKey) {
    // Priority 1: Entry-specific classifications (e.g., personnel = social)
    if (ENTRY_CLASSIFICATIONS.containsKey(entryKey)) {
      return ENTRY_CLASSIFICATIONS.get(entryKey);
    }

    // Priority 2: Handle Recovery and Resilience Funds specially
    if (isRecoveryFundUnit(unitKey)) {
      return classifyRecoveryFund(sectorKey);
    }

    // Priority 3: Unit-level classification
    if (UNIT_CLASSIFICATIONS.containsKey(unitKey)) {
      EsgCategory unitCategory = UNIT_CLASSIFICATIONS.get(unitKey);

      // Context-dependent entries inherit from unit
      if (isContextDependentEntry(entryKey)) {
        return unitCategory;
      }

      return unitCategory;
    }

    // Priority 4: Sector-level classification
    if (SECTOR_CLASSIFICATIONS.containsKey(sectorKey)) {
      EsgCategory sectorCategory = SECTOR_CLASSIFICATIONS.get(sectorKey);

      // For Environmental sectors, context-dependent entries are Environmental
      if (sectorCategory == EsgCategory.ENVIRONMENTAL
          && isContextDependentEntry(entryKey)) {
        return EsgCategory.ENVIRONMENTAL;
      }

      // For Governance sectors, context-dependent entries are Governance
      if (sectorCategory == EsgCategory.GOVERNANCE
          && isContextDependentEntry(entryKey)) {
        return EsgCategory.GOVERNANCE;
      }

      return sectorCategory;
    }

    // Priority 5: Special handling for energy sector
    if (sectorKey.equals("energy_and_mineral_resources_management")) {
      return classifyEnergyEntry(entryKey);
    }

    // Default: Neutral
    return EsgCategory.NEUTRAL;
  }

  /**
   * Overload for backward compatibility (2-parameter version).
   * Extracts unitKey from entryKey if possible, otherwise uses sector.
   */
  public EsgCategory classifyEntry(String sectorKey, String entryKey) {
    // Try to infer if this is a recovery fund from entry key
    if (entryKey.contains("recovery_and_resilience")) {
      return classifyRecoveryFund(sectorKey);
    }

    return classifyEntry(sectorKey, "", entryKey);
  }

  /**
   * Checks if a unit is a recovery and resilience fund.
   */
  private boolean isRecoveryFundUnit(String unitKey) {
    return unitKey != null && unitKey.contains("recovery_and_resilience");
  }

  /**
   * Classifies recovery funds based on sector context.
   * Recovery and Resilience Funds distribution strategy:
   * - Environmental sectors: 70% Environmental (green transition focus)
   * - Governance sectors: 60% Governance, 40% mixed
   * - Energy sector: 80% Environmental (renewable energy)
   * For realistic ESG scoring, we use weighted classification:
   * - Natural Environment + Water: 70% E
   * - Spatial Planning: 60% E, 40% G
   * - Energy: 75% E, 25% G
   * - Executive Coordination: 40% G, 60% mixed (Social/Governance)
   */
  private EsgCategory classifyRecoveryFund(String sectorKey) {
    // Environmental sectors - primarily green transition
    if (sectorKey.equals("natural_environment_and_water_protection")) {
      return EsgCategory.ENVIRONMENTAL;
    }

    // Spatial planning - mix of environmental and governance
    if (sectorKey.equals("spatial_planning_and_urban_environment")) {
      return EsgCategory.ENVIRONMENTAL;
    }

    // Energy sector - renewable energy transition (highly environmental)
    if (sectorKey.equals("energy_and_mineral_resources_management")) {
      return EsgCategory.ENVIRONMENTAL;
    }

    // Executive coordination - digital transformation (governance)
    if (sectorKey.equals("executive_coordination_and_investments")) {
      return EsgCategory.GOVERNANCE;
    }

    // Default: Environmental (EU Recovery Fund's primary goal)
    return EsgCategory.ENVIRONMENTAL;
  }

  /**
   * Checks if an entry is context-dependent (classification depends on sector/unit).
   */
  private boolean isContextDependentEntry(String entryKey) {
    return entryKey.equals("credits_under_allocation")
        || entryKey.equals("transfers")
        || entryKey.equals("permanent_assets");
  }

  /**
   * Classifies entries in the energy sector.
   */
  private EsgCategory classifyEnergyEntry(String entryKey) {
    // Credits and transfers for renewable energy are Environmental
    if (entryKey.equals("credits_under_allocation")
        || entryKey.equals("transfers")) {
      return EsgCategory.ENVIRONMENTAL;
    }

    // Permanent assets for green infrastructure are Environmental
    if (entryKey.equals("permanent_assets")) {
      return EsgCategory.ENVIRONMENTAL;
    }

    // Personnel in energy sector is Social
    if (entryKey.equals("personnel_costs")) {
      return EsgCategory.SOCIAL;
    }

    // Governance entries
    if (entryKey.equals("purchase_of_goods_and_services")) {
      return EsgCategory.GOVERNANCE;
    }

    // Other entries are Governance
    return EsgCategory.GOVERNANCE;
  }

  /**
   * Gets the full name of a sector's ESG classification.
   */
  public String getSectorClassificationName(String sectorKey) {
    if (SECTOR_CLASSIFICATIONS.containsKey(sectorKey)) {
      return SECTOR_CLASSIFICATIONS.get(sectorKey).getNameEl();
    }
    if (sectorKey.equals("energy_and_mineral_resources_management")) {
      return "Μικτές (Ε & G)";
    }
    return "Ουδέτερες";
  }
}
