package gr.det.spinnovators.EnvDataMODEL;

import java.util.List;

public class EnvUnit {
    private final String jsonKey;
    private final List<EnvEntry> entries;

    public EnvUnit(String jsonKey, List<EnvEntry> entries) {
        this.jsonKey = jsonKey;
        this.entries = entries;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public List<EnvEntry> getEntries() {
        return entries;
    }

    public EnvEntry getEntryByKey(String key) {
        if (key == null) return null;
        for (EnvEntry entry : entries) {
            if (entry.getJsonKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }
}
