package gr.det.spinnovators.envdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the complete environmental budget data for a specific fiscal year.
 *
 * <p>This class serves as the top-level container in the budget data hierarchy,
 * holding a list of policy sectors ({@link EnvSector}) that collectively represent
 * the entire environmental ministry budget for the year. It provides the entry point
 * for all budget operations and data access.</p>
 *
 * <p>The complete data hierarchy is: <strong>EnvYear</strong> → EnvSector → EnvUnit → EnvEntry</p>
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Store all sector-level budget data for the year</li>
 *   <li>Provide navigation methods to locate specific entries</li>
 *   <li>Serve as the root for budget calculations and modifications</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see EnvSector
 * @see EnvBudgetData
 */
public class EnvYear {

  private final String year;
  private final List<EnvSector> sectors;

  /**
   * Constructs an EnvYear instance with the specified year and sectors.
   *
   * <p>A defensive copy of the sectors list is created to prevent external modification.
   * If a null list is provided, an empty list is initialized.</p>
   *
   * @param year the fiscal year string (e.g., "2025", "2026")
   * @param sectors a list of EnvSector objects associated with this year
   */
  public EnvYear(String year, List<EnvSector> sectors) {
    this.year = year;
    this.sectors = sectors != null ? new ArrayList<>(sectors) : new ArrayList<>();
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
    return Collections.unmodifiableList(sectors);
  }

  /**
   * Searches for a specific budget entry by traversing the data hierarchy.
   *
   * <p>This method navigates through the three-level hierarchy:
   * <ol>
   *   <li>Locates the sector using the sectorKey</li>
   *   <li>Locates the unit within that sector using the unitKey</li>
   *   <li>Locates the entry within that unit using the entryKey</li>
   * </ol>
   * </p>
   *
   * <p>The method returns null if any part of the path is invalid, including
   * when any of the provided keys are null, or when the sector, unit, or entry
   * cannot be found at their respective levels.</p>
   *
   * @param sectorKey the unique key of the sector to search in
   * @param unitKey the unique key of the administrative unit within the sector
   * @param entryKey the unique key of the specific budget entry within the unit
   * @return the matching EnvEntry object, or null if any part of the path is invalid
   *         or if any of the parameters are null
   */
  public EnvEntry findEntry(String sectorKey, String unitKey, String entryKey) {
    if (sectorKey == null || unitKey == null || entryKey == null) {
      return null;
    }

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

    // Sector and unit were found successfully, returns final EnvEntry object
    return unit.getEntryByKey(entryKey);
  }

  /**
   * Internal helper method to locate a sector by its unique JSON key.
   *
   * <p>This private method performs a linear search through all sectors
   * in this year's budget to find a matching key.</p>
   *
   * @param key the unique identifier for the sector to find
   * @return the matching EnvSector object, or null if not found
   */
  private EnvSector getSectorByKey(String key) {
    for (EnvSector sector : sectors) {
      if (sector.getJsonKey().equals(key)) {
        return sector;
      }
    }
    return null;
  }
}
