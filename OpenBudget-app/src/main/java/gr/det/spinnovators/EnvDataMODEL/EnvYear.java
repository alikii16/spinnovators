package gr.det.spinnovators.EnvDataMODEL;

import java.util.List;

public class EnvYear {
    private final String year;
    private final List<EnvSector> sectors;

    public EnvYear(String year, List<EnvSector> sectors) {
        this.year = year;
        this.sectors = sectors;
    }

    public String getYear() {
        return year;
    }

    public List<EnvSector> getSectors() {
        return sectors;
    }

    public EnvEntry findEntry(String sectorKey, String unitKey, String entryKey) {
        if (sectorKey == null || unitKey == null || entryKey == null) {
            return null;
        }
        EnvSector sector = getSectorByKey(sectorKey);
        if (sector == null) {
            return null;
        }
        EnvUnit unit = sector.getUnitByKey(unitKey);
        if (unit == null) {
            return null;
        }
        return unit.getEntryByKey(entryKey);
    }

    private EnvSector getSectorByKey(String key) {
        for (EnvSector sector : sectors) {
            if (key.equals(sector.getJsonKey())) {
                return sector;
            }
        }
        return null;
    }
}
