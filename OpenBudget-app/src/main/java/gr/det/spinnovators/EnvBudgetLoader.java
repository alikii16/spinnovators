package gr.det.spinnovators.service;

import gr.det.spinnovators.EnvDataMODEL.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

/**
  * EnvBudgetLoader is responsible for reading the JSON file and creating the hierarchical 
  * structure of EnvBudgetData objects.
 */

public class EnvBudgetLoader {

    private static final String JSON_FILE_NAME = "/env_budget_data.json";
    private final Gson gson = new Gson();

    /**
     * The main method. Reads the JSON and initiates the transformation.
     * @return The fully constructed EnvBudgetData object.
     * @throws EnvDataLoadException If file reading, file lookup, or JSON parsing fails.
     */

    public EnvBudgetData loadBudget() throws EnvDataLoadException {
        // Use the ClassLoader to find the file within the classpath (e.g., src/main/resources)
        try (InputStream is = getClass().getResourceAsStream(JSON_FILE_NAME)) {

            if (is == null) {
                throw new EnvDataLoadException("Error: JSON file not found: " + JSON_FILE_NAME + ". Ensure it is in src/main/resources.");
            }

            JsonReader reader = new JsonReader(new InputStreamReader(is));
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

            Map<String, Object> rootMap = gson.fromJson(reader, mapType);

            // Validation that the JSON root contains the necessary keys
            if (rootMap == null || !rootMap.containsKey("data_by_year")) {
                 throw new EnvDataLoadException("Error: The structure of the JSON file is invalid (missing 'data_by_year').");
            }

            // Extraction and conversion to Model Objects
            Map<String, Double> totalBudget = extractTotalBudget((Map<String, Object>) rootMap.get("env_ministry_total_budget"));
            Map<String, EnvYear> dataByYear = extractYears((Map<String, Object>) rootMap.get("data_by_year"));

            return new EnvBudgetData(dataByYear, totalBudget);

        } catch (IOException e) {
            // Error during closing or reading the stream
            throw new EnvDataLoadException("I/O error during file loading.", e);
        } catch (JsonSyntaxException e) {
            // Error if the JSON content is not correctly formatted
            throw new EnvDataLoadException("JSON syntax error. Check commas and braces.", e);
        }
    }

    /** Helper method for extracting the total budget. */
    private Map<String, Double> extractTotalBudget(Map<String, Object> budgetMap) {
        Map<String, Double> totalBudget = new HashMap<>();
        if (budgetMap != null) {
            // Gson reads all decimal numbers as Double
            for (Map.Entry<String, Object> entry : budgetMap.entrySet()) {
            String year = entry.getKey();
            Object amount = entry.getValue();
            
            // Cast the amount to Double as read by Gson
            if (amount instanceof Double) {
                totalBudget.put(year, (Double) amount);
            }
          }
        }
        return totalBudget;
    }

    /** Helper method for creating EnvYear objects. */
    private Map<String, EnvYear> extractYears(Map<String, Object> yearsMap) {
        Map<String, EnvYear> envYears = new HashMap<>();
        if (yearsMap != null) {
            yearsMap.forEach((year, sectorsMap) -> {
                // sectorsMap: Map<String, Object> -> Key: SectorKey, Value: UnitsMap
                List<EnvSector> sectors = extractSectors((Map<String, Object>) sectorsMap);
                envYears.put(year, new EnvYear(year, sectors));
            });
        }
        return envYears;
    }

    /** Helper method for creating EnvSector objects. */
    private List<EnvSector> extractSectors(Map<String, Object> sectorsMap) {
        List<EnvSector> sectors = new ArrayList<>();
        if (sectorsMap != null) {
            sectorsMap.forEach((sectorKey, unitsMap) -> {
                // unitsMap: Map<String, Object> -> Key: UnitKey, Value: EntriesMap
                List<EnvUnit> units = extractUnits((Map<String, Object>) unitsMap);
                sectors.add(new EnvSector(sectorKey, units));
            });
        }
        return sectors;
    }

    /** Helper method for creating EnvUnit objects. */
    private List<EnvUnit> extractUnits(Map<String, Object> unitsMap) {
        List<EnvUnit> units = new ArrayList<>();
        if (unitsMap != null) {
            unitsMap.forEach((unitKey, entriesMap) -> {
                // entriesMap: Map<String, Object> -> Key: EntryKey, Value: Amount (Double)
                List<EnvEntry> entries = extractEntries((Map<String, Object>) entriesMap);
                units.add(new EnvUnit(unitKey, entries));
            });
        }
        return units;
    }

    /** Helper method for creating EnvEntry objects. */
    private List<EnvEntry> extractEntries(Map<String, Object> entriesMap) {
        List<EnvEntry> entries = new ArrayList<>();
        if (entriesMap != null) {
            entriesMap.forEach((entryKey, amount) -> {
                // Cast the amount to Double as read by Gson
                entries.add(new EnvEntry(entryKey, (Double) amount));
            });
        }
        return entries;
    }
}


