package gr.det.spinnovators.envdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an administrative unit (e.g., General Secretariat) within a policy sector.
 *
 * <p>Each unit contains multiple budget entries ({@link EnvEntry}) that specify
 * detailed allocations for different expense categories. Units represent the
 * organizational divisions responsible for executing specific policy areas within
 * their parent sector.</p>
 *
 * <p>Examples of units include "General Secretariat for Energy",
 * "General Secretariat for Natural Environment", and similar administrative divisions.
 * Each unit manages its own budget breakdown across various expense categories.</p>
 *
 * <p>This class is part of the hierarchical budget structure:
 * EnvYear → EnvSector → <strong>EnvUnit</strong> → EnvEntry</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 * @see EnvSector
 * @see EnvEntry
 */
public class EnvUnit {

  private final String jsonKey; // e.g. general_secretariat_for_energy
  private final List<EnvEntry> entries;

  /**
   * Constructs an EnvUnit instance with the specified key and entries.
   *
   * <p>A defensive copy of the entries list is created to prevent external modification.
   * If a null list is provided, an empty list is initialized.</p>
   *
   * @param jsonKey the unique identifier for this unit (e.g., "general_secretariat_for_energy")
   * @param entries a list of EnvEntry objects belonging to this unit
   */
  public EnvUnit(String jsonKey, List<EnvEntry> entries) {
    this.jsonKey = jsonKey;
    this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
  }

  /**
   * Retrieves the JSON key associated with this unit.
   *
   * @return the unique string identifier for the unit
   */
  public String getJsonKey() {
    return jsonKey;
  }

  /**
   * Retrieves the list of budget entries within this unit.
   *
   * <p>The returned list is unmodifiable to preserve data integrity.</p>
   *
   * @return an unmodifiable list containing all EnvEntry objects within this unit
   */
  public List<EnvEntry> getEntries() {
    return Collections.unmodifiableList(entries);
  }

  /**
   * Locates a specific budget entry within the unit using its unique JSON key.
   *
   * <p>This helper method performs a linear search through the unit's entries
   * to find a match. If the provided key is null, the method returns null
   * immediately to prevent iteration issues and potential NullPointerExceptions.</p>
   *
   * @param key the unique JSON key of the entry to search for
   * @return the matching EnvEntry object, or null if no entry is found with that key
   *         or if the key parameter is null
   */
  public EnvEntry getEntryByKey(String key) {
    if (key == null) {
      return null;
    }
    for (EnvEntry entry : entries) {
      if (entry.getJsonKey().equals(key)) {
        return entry;
      }
    }
    return null;
  }
}
