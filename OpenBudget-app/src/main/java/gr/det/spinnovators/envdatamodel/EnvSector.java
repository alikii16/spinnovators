package gr.det.spinnovators.EnvDataMODEL;

import java.util.List;

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

    public EnvUnit getUnitByKey(String key) {
        if (key == null) return null;
        for (EnvUnit unit : units) {
            if (key.equals(unit.getJsonKey())) {
                return unit;
            }
        }
        return null;
    }
}
