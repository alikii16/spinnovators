package gr.det.spinnovators.service;

import java.util.HashMap;
import java.util.Map;

import gr.det.spinnovators.envdatamodel.EsgCategory;

/**
 * Classifies budget sectors, units, and entries into ESG categories.
 *
 * <p>Uses predefined mappings to determine whether a budget item should be
 * counted as Environmental, Social, Governance, or Neutral for ESG scoring.
 *
 * @author Spinnovators Team
 * @version 2.0 (Fixed classification logic)
 */
public class EsgClassifier {

  // Sector-level classifications
  private static final Map<String, EsgCategory> SECTOR_CLASSIFICATIONS = new HashMap<>();

  // Entry-level classifications
  private static final Map<String, EsgCategory> ENTRY_CLASSIFICATIONS = new HashMap<>();

  static {
    // Environmental sectors
    SECTOR_CLASSIFICATIONS.put("natural_environment_and_water_protection",
        EsgCategory.ENVIRONMENTAL);
    SECTOR_CLASSIFICATIONS.put("spatial_planning_and_urban_environment",
        EsgCategory.ENVIRONMENTAL);

    // Governance sector
    SECTOR_CLASSIFICATIONS.put("executive_coordination_and_investments",
        EsgCategory.GOVERNANCE);

    // Social entries (apply across all sectors)
    ENTRY_CLASSIFICATIONS.put("personnel_costs", EsgCategory.SOCIAL);
    ENTRY_CLASSIFICATIONS.put("community_benefits", EsgCategory.SOCIAL);

    // Governance entries
    ENTRY_CLASSIFICATIONS.put("purchase_of_goods_and_services",
        EsgCategory.GOVERNANCE);

    // 🆕 CRITICAL FIX: Recovery and resilience funds are ENVIRONMENTAL
    // These are EU funds specifically for green transition
    ENTRY_CLASSIFICATIONS.put("recovery_and_resilience_fund_expenses",
        EsgCategory.ENVIRONMENTAL);
  }

  /**
   * Classifies a budget entry based on its sector and entry type.
   *
   * <p>Classification logic:
   * <ol>
   *   <li>Check if entry type has specific classification (e.g., personnel_costs → SOCIAL)</li>
   *   <li>If not, inherit classification from sector</li>
   *   <li>Handle special cases for energy sector (context-dependent)</li>
   *   <li>Default to NEUTRAL if no clear classification</li>
   * </ol>
   *
   * @param sectorKey The JSON key of the sector (e.g., "natural_environment_and_water_protection")
   * @param entryKey The JSON key of the entry (e.g., "personnel_costs")
   * @return The ESG category for this entry
   */
  public EsgCategory classifyEntry(String sectorKey, String entryKey) {
    // Priority 1: Entry-specific classifications (e.g., personnel = social)
    if (ENTRY_CLASSIFICATIONS.containsKey(entryKey)) {
      return ENTRY_CLASSIFICATIONS.get(entryKey);
    }

    // Priority 2: Sector-level classification
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

    // Priority 3: Special handling for energy sector
    if (sectorKey.equals("energy_and_mineral_resources_management")) {
      return classifyEnergyEntry(entryKey);
    }

    // Default: Neutral
    return EsgCategory.NEUTRAL;
  }

  /**
   * Checks if an entry is context-dependent (classification depends on sector).
   *
   * @param entryKey The entry JSON key
   * @return true if context-dependent, false otherwise
   */
  private boolean isContextDependentEntry(String entryKey) {
    return entryKey.equals("credits_under_allocation")
        || entryKey.equals("transfers")
        || entryKey.equals("permanent_assets");
  }

  /**
   * Classifies entries in the energy sector.
   *
   * <p>Energy sector is mixed - some expenses are environmental (renewable energy),
   * others are governance (coordination).
   *
   * @param entryKey The entry JSON key
   * @return ESG category for this energy entry
   */
  private EsgCategory classifyEnergyEntry(String entryKey) {
    // Recovery funds in energy sector are ENVIRONMENTAL
    if (entryKey.contains("recovery_and_resilience")) {
      return EsgCategory.ENVIRONMENTAL;
    }

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

    // Other entries are Governance
    return EsgCategory.GOVERNANCE;
  }

  /**
   * Gets the full name of a sector's ESG classification.
   *
   * @param sectorKey The sector JSON key
   * @return Greek name of the ESG category
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
