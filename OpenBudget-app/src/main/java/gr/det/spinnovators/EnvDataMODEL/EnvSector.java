package gr.det.spinnovators.EnvDataMODEL;

import java.util.List;

/**
 * Represents a major policy sector (e.g., "energy_and_mineral_resources_management")
 */

public class EnvSector {

    private final String jsonKey;
    private final List<EnvUnit> units;

    public EnvSector(String jsonKey, List<EnvUnit> units) {
        this.jsonKey = jsonKey;
        this.units = units;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public List<EnvUnit> getUnits() {
        return units;
    }

    // Helper method: locate a unit by its key
    public EnvUnit getUnitByKey(String key) {
        if (key == null) return null; // NullPointerException
        for (EnvUnit unit : units) {
            if (key.equals(unit.getJsonKey())) {
                return unit;
            }
        }
        return null; // Unit not found
    }
}
