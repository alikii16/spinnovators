package gr.det.spinnovators.EnvDataMODEL;

import java.util.List;

/**
 * Represents all data for a specific year (e.g., "2025")
 */

public class EnvYear {

    private String year;
    private List<EnvSector> sectors;

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

    // Traverses sectors --> units --> entries and returns the EnvEntry object.
    public EnvEntry findEntry(String sectorKey, String unitKey, String entryKey) {
        if (sectorKey == null || unitKey == null || entryKey == null) {
            return null;
        } // Might cause NullPointerException
	
	// Finds the sector or returns null
        EnvSector sector = getSectorByKey(sectorKey);
        if (sector == null) {
            return null;
        }

        // Finds the unit inside the sector or returns null
        EnvUnit unit = sector.getUnitByKey(unitKey);
        if (unit == null) {
            return null;
        }

        // Sector and unit were found successfully, returns final EnvEntry object (the entry) 
        return unit.getEntryByKey(entryKey);
    }

    // Helper method: Searches Sector 
      private EnvSector getSectorByKey(String key) {
        for (EnvSector sector : sectors) {
            if (key.equals(sector.getJsonKey())) {
                return sector;
            }
        }
        return null; //No match found 
    }
}
