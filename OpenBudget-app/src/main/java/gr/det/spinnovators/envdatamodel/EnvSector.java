package gr.det.spinnovators.envdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a major policy sector within the environmental budget.
 * Examples include sectors like "energy_and_mineral_resources_management".
 * Each sector contains a collection of specific administrative or policy units.
 */

public class EnvSector {

  private final String jsonKey;
  private final List<EnvUnit> units;

  /**
   * Constructs an EnvSector instance.
   *
   * @param jsonKey The unique identifier for the sector, often derived from JSON data.
   * @param units A list of EnvUnit objects belonging to this sector.
   */
  public EnvSector(String jsonKey, List<EnvUnit> units) {
    this.jsonKey = jsonKey;
    this.units = units != null ? new ArrayList<>(units) : new ArrayList<>();
  }

  /**
   * Retrieves the JSON key associated with this sector.
   *
   * @return The unique string identifier for the sector.
   */
  public String getJsonKey() {
    return jsonKey;
  }

  /**
   * Retrieves the list of units under this policy sector.
   *
   * @return A list containing all EnvUnit objects within this sector.
   */
  public List<EnvUnit> getUnits() {
    return Collections.unmodifiableList(units);
  }

  // Helper method: locate a unit by its key
  /**
   * Locates a specific unit within the sector using its unique key.
   * This helper method iterates through the sector's units to find a match.
   *
   * @param key The unique JSON key of the unit to search for.
   * @return The matching EnvUnit object, or null if no unit is found with that key.
   */

  public EnvUnit getUnitByKey(String key) {
    if (key == null) {
      return null; // NullPointerException
    }
    for (EnvUnit unit : units) {
      if ((unit.getJsonKey().equals(key))) {
        return unit;
      }
    }
    return null; // Unit not found
  }
}
