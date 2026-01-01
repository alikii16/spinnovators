package gr.det.spinnovators.envdatamodel;

import java.util.List;

/**
 * Represents the complete environmental budget data for a specific fiscal year.
 * It serves as the top-level container in the data hierarchy, holding a list 
 * of policy sectors.
 */

public class EnvYear {

  private final String year;
  private final List<EnvSector> sectors;

  /**
   * Constructs an EnvYear instance.
   *
   * @param year The fiscal year string (e.g., "2025").
   * @param sectors A list of EnvSector objects associated with this year.
   */
  public EnvYear(String year, List<EnvSector> sectors) {
    this.year = year;
    this.sectors = sectors;
  }

  /**
   * Retrieves the fiscal year identifier.
   *
   * @return The year as a string.
   */
  public String getYear() {
    return year;
  }

  /**
   * Retrieves all policy sectors associated with this year.
   *
   * @return A list of EnvSector objects.
   */
  public List<EnvSector> getSectors() {
    return sectors;
  }

  /**
   * Traverses sectors --> units --> entries and returns the EnvEntry object.
   *
   * Searches for a specific budget entry by traversing the data hierarchy,
   * starting from the sector level down to the unit and finally to the entry.
   * This method returns the matching EnvEntry object if found, or null if any part
   * of the path is invalid.
   *
   * @param sectorKey The unique key of the sector.
   * @param unitKey The unique key of the administrative unit.
   * @param entryKey The unique key of the specific budget entry.
   * @return The matching EnvEntry object, or null if any part of the path is invalid.
   */
  
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
  /**
   * Internal helper method to locate a sector by its unique JSON key.
   *
   * @param key The unique identifier for the sector.
   * @return The matching EnvSector object, or null if not found.
   */
  private EnvSector getSectorByKey(String key) {
    for (EnvSector sector : sectors) {
      if (key.equals(sector.getJsonKey())) {
        return sector;
      }
    }
    return null; // No match found
  }
}
