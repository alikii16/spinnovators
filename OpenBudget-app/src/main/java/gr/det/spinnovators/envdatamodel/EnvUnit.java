package gr.det.spinnovators.envdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an administrative unit (e.g., General Secretariat)
 * containing multiple EnvEntry items.
 */
public class EnvUnit {

  private final String jsonKey; // e.g. general_secretariat_for_energy
  private final List<EnvEntry> entries;

  public EnvUnit(String jsonKey, List<EnvEntry> entries) {
    this.jsonKey = jsonKey;
    this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
  }

  public String getJsonKey() {
    return jsonKey;
  }

  public List<EnvEntry> getEntries() {
    return Collections.unmodifiableList(entries);
  }

  /**
   * Helper method: returns the EnvEntry with the given key,
   * or null if it doesn't exist.
   */
  public EnvEntry getEntryByKey(String key) {
    if (key == null) {
      return null; //User's given key is null, might cause NullPointerException
    }
    for (EnvEntry entry : entries) {
      if (entry.getJsonKey().equals(key)) {
        return entry;
      }
    }
    return null; //Entry not found
  }
}
