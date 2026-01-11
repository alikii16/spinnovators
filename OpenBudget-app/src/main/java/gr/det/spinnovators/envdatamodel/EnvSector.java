package gr.det.spinnovators.envdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a major policy sector within the environmental budget.
 *
 * <p>Examples include sectors like "energy_and_mineral_resources_management",
 * "natural_environment_and_climate_change", and other high-level policy areas.
 * Sectors represent the highest level of budget organization below the year level.</p>
 *
 * <p>Each sector contains a collection of specific administrative or policy units
 * ({@link EnvUnit}) that manage different aspects of the sector's responsibilities.
 * For example, the energy sector might contain units for renewable energy,
 * fossil fuels, and energy efficiency.</p>
 *
 * <p>This class is part of the hierarchical budget structure:
 * EnvYear → <strong>EnvSector</strong> → EnvUnit → EnvEntry</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see EnvYear
 * @see EnvUnit
 */
public class EnvSector {

  private final String jsonKey;
  private final List<EnvUnit> units;

  /**
   * Constructs an EnvSector instance with the specified key and units.
   *
   * <p>A defensive copy of the units list is created to prevent external modification.
   * If a null list is provided, an empty list is initialized.</p>
   *
   * @param jsonKey the unique identifier for the sector, often derived from JSON data
   * @param units a list of EnvUnit objects belonging to this sector
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

  /**
   * Locates a specific unit within the sector using its unique JSON key.
   *
   * <p>This helper method performs a linear search through the sector's units
   * to find a match. If the provided key is null, the method returns null
   * immediately to prevent iteration issues.</p>
   *
   * @param key the unique JSON key of the unit to search for
   * @return the matching EnvUnit object, or null if no unit is found with that key
   *         or if the key parameter is null
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
    return null;
  }
}
