package gr.det.spinnovators.service;

import java.util.HashMap;
import java.util.Map;

import gr.det.spinnovators.envdatamodel.ESG_Category;

/**
 * Classifies budget sectors, units, and entries into ESG categories.
 *
 * Uses predefined mappings to determine whether a budget item should be
 * counted as Environmental, Social, Governance, or Neutral for ESG scoring.
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class ESG_Classifier {

    // Sector-level classifications
    private static final Map<String, ESG_Category> SECTOR_CLASSIFICATIONS = new HashMap<>();

    // Entry-level classifications
    private static final Map<String, ESG_Category> ENTRY_CLASSIFICATIONS = new HashMap<>();

    static {
        // Environmental sectors
        SECTOR_CLASSIFICATIONS.put("natural_environment_and_water_protection",
            ESG_Category.ENVIRONMENTAL);
        SECTOR_CLASSIFICATIONS.put("spatial_planning_and_urban_environment",
            ESG_Category.ENVIRONMENTAL);

        // Governance sector
        SECTOR_CLASSIFICATIONS.put("executive_coordination_and_investments",
            ESG_Category.GOVERNANCE);

        // Social entries (apply across all sectors)
        ENTRY_CLASSIFICATIONS.put("personnel_costs", ESG_Category.SOCIAL);
        ENTRY_CLASSIFICATIONS.put("community_benefits", ESG_Category.SOCIAL);

        // Governance entries
        ENTRY_CLASSIFICATIONS.put("purchase_of_goods_and_services",
            ESG_Category.GOVERNANCE);
    }

    /**
     * Classifies a budget entry based on its sector and entry type.
     *
     * Classification logic:
     *
     *  Check if entry type has specific classification (e.g., personnel_costs → SOCIAL)
     *  If not, inherit classification from sector
     *  Handle special cases for energy sector (context-dependent)
     *  Default to NEUTRAL if no clear classification
     *
     *
     * @param sectorKey The JSON key of the sector (e.g., "natural_environment_and_water_protection")
     * @param entryKey The JSON key of the entry (e.g., "personnel_costs")
     * @return The ESG category for this entry
     */
    public ESG_Category classifyEntry(String sectorKey, String entryKey) {
        // Priority 1: Entry-specific classifications (e.g., personnel = social)
        if (ENTRY_CLASSIFICATIONS.containsKey(entryKey)) {
            return ENTRY_CLASSIFICATIONS.get(entryKey);
        }

        // Priority 2: Sector-level classification
        if (SECTOR_CLASSIFICATIONS.containsKey(sectorKey)) {
            ESG_Category sectorCategory = SECTOR_CLASSIFICATIONS.get(sectorKey);

            // For Environmental sectors, context-dependent entries are Environmental
            if (sectorCategory == ESG_Category.ENVIRONMENTAL &&
                isContextDependentEntry(entryKey)) {
                return ESG_Category.ENVIRONMENTAL;
            }

            // For Governance sectors, context-dependent entries are Governance
            if (sectorCategory == ESG_Category.GOVERNANCE &&
                isContextDependentEntry(entryKey)) {
                return ESG_Category.GOVERNANCE;
            }

            return sectorCategory;
        }

        // Priority 3: Special handling for energy sector
        if (sectorKey.equals("energy_and_mineral_resources_management")) {
            return classifyEnergyEntry(entryKey);
        }

        // Default: Neutral
        return ESG_Category.NEUTRAL;
    }

    /**
     * Checks if an entry is context-dependent (classification depends on sector).
     *
     * @param entryKey The entry JSON key
     * @return true if context-dependent, false otherwise
     */
    private boolean isContextDependentEntry(String entryKey) {
        return entryKey.equals("credits_under_allocation") ||
               entryKey.equals("transfers") ||
               entryKey.equals("permanent_assets");
    }

    /**
     * Classifies entries in the energy sector.
     *
     * Energy sector is mixed - some expenses are environmental (renewable energy),
     * others are governance (coordination).\
     *
     * @param entryKey The entry JSON key
     * @return ESG category for this energy entry
     */
    private ESG_Category classifyEnergyEntry(String entryKey) {
        // Credits and transfers for renewable energy are Environmental
        if (entryKey.equals("credits_under_allocation") ||
            entryKey.equals("transfers")) {
            return ESG_Category.ENVIRONMENTAL;
        }

        // Permanent assets for green infrastructure are Environmental
        if (entryKey.equals("permanent_assets")) {
            return ESG_Category.ENVIRONMENTAL;
        }

        // Other entries are Governance
        return ESG_Category.GOVERNANCE;
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
