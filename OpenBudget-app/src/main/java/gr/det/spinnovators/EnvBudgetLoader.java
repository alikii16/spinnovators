package gr.det.spinnovators;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import gr.det.spinnovators.EnvDataMODEL.EnvBudgetData;
import gr.det.spinnovators.EnvDataMODEL.EnvEntry;
import gr.det.spinnovators.EnvDataMODEL.EnvSector;
import gr.det.spinnovators.EnvDataMODEL.EnvUnit;
import gr.det.spinnovators.EnvDataMODEL.EnvYear;

/**
 * EnvBudgetLoader is responsible for reading the JSON file and creating the
 * hierarchical structure of EnvBudgetData objects.
 */
public class EnvBudgetLoader {

    /** The JSON file to load from classpath. */
    private static final String JSON_FILE_NAME = "env_budget_data.json";

    /** Gson instance used for JSON parsing. */
    private final Gson gson = new Gson();

    /**
     * Loads the budget file and constructs EnvBudgetData.
     *
     * @return The fully constructed EnvBudgetData object.
     * @throws EnvDataLoadException If file reading, file lookup, or JSON parsing fails.
     */
    public EnvBudgetData loadBudget() throws EnvDataLoadException {
        // Using the ClassLoader to find the file within the classpath (e.g., src/main/resources)
        InputStream is = getClass().getResourceAsStream(JSON_FILE_NAME);
        if (is == null) {
            throw new EnvDataLoadException(
                JSON_FILE_NAME + "Error: JSON file not found: " +
                ". Ensure it is in src/main/resources."
            );
        }

        try (
            InputStream nonNullIs = is;
            JsonReader reader = new JsonReader(new InputStreamReader(nonNullIs))
        ) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> rootMap = gson.fromJson(reader, mapType);

            if (rootMap == null || !rootMap.containsKey("data_by_year")) {
                throw new EnvDataLoadException(
                    "Error: The structure of the JSON file is invalid " +
                    "(missing 'data_by_year')."
                );
            }

            Map<String, Double> totalBudget = transformTotalBudget(
                (Map<String, Object>) rootMap.get("env_ministry_total_budget")
            );

            Map<String, EnvYear> dataByYear = transformYears(
                (Map<String, Object>) rootMap.get("data_by_year")
            );

            return new EnvBudgetData(dataByYear, totalBudget);

        } catch (IOException e) {
            throw new EnvDataLoadException(
                "I/O error during file loading or closing resources.", e
            );
        } catch (JsonSyntaxException e) {
            throw new EnvDataLoadException(
                "JSON syntax error. Check commas and braces.", e
            );
        }
    }

    /**
     * Transforms raw total budget map into a strongly typed Map.
     *
     * @param budgetMap raw JSON map
     * @return map of budget per year
     */
    private Map<String, Double> transformTotalBudget(final Map<String, Object> budgetMap) {
        Map<String, Double> totalBudget = new HashMap<>();
        if (budgetMap != null) {
            for (Map.Entry<String, Object> entry : budgetMap.entrySet()) {
                String year = entry.getKey();
                Object amount = entry.getValue();
                if (amount instanceof Double) {
                    totalBudget.put(year, (Double) amount);
                }
            }
        }
        return totalBudget;
    }

    /**
     * Transforms raw JSON year map into structured EnvYear objects.
     *
     * @param yearsMap raw map from JSON
     * @return structured years
     */
    private Map<String, EnvYear> transformYears(final Map<String, Object> yearsMap) {
        Map<String, EnvYear> envYears = new HashMap<>();
        if (yearsMap != null) {
            for (Map.Entry<String, Object> entry : yearsMap.entrySet()) {
                String year = entry.getKey();
                Map<String, Object> sectorsMap = (Map<String, Object>) entry.getValue();
                List<EnvSector> sectors = transformSectors(sectorsMap);
                envYears.put(year, new EnvYear(year, sectors));
            }
        }
        return envYears;
    }

    /**
     * Transforms raw sector map into structured EnvSector objects.
     *
     * @param sectorsMap raw map from JSON
     * @return list of EnvSector objects
     */
    private List<EnvSector> transformSectors(final Map<String, Object> sectorsMap) {
        List<EnvSector> sectors = new ArrayList<>();
        if (sectorsMap != null) {
            for (Map.Entry<String, Object> entry : sectorsMap.entrySet()) {
                String sectorKey = entry.getKey();
                Map<String, Object> unitsMap = (Map<String, Object>) entry.getValue();
                List<EnvUnit> units = transformUnits(unitsMap);
                sectors.add(new EnvSector(sectorKey, units));
            }
        }
        return sectors;
    }

    /**
     * Helper method for constructing EnvUnit objects.
     *
     * @param unitsMap raw map from JSON
     * @return structured list of units
     */
    private List<EnvUnit> transformUnits(final Map<String, Object> unitsMap) {
        List<EnvUnit> units = new ArrayList<>();
        if (unitsMap != null) {
            for (Map.Entry<String, Object> entry : unitsMap.entrySet()) {
                String unitKey = entry.getKey();
                Map<String, Object> entriesMap = (Map<String, Object>) entry.getValue();
                List<EnvEntry> entries = transformEntries(entriesMap);
                units.add(new EnvUnit(unitKey, entries));
            }
        }
        return units;
    }

    /**
     * Helper method for constructing EnvEntry objects.
     *
     * @param entriesMap raw map from JSON
     * @return list of EnvEntry objects
     */
    private List<EnvEntry> transformEntries(final Map<String, Object> entriesMap) {
        List<EnvEntry> entries = new ArrayList<>();
        if (entriesMap != null) {
            for (Map.Entry<String, Object> entry : entriesMap.entrySet()) {
                String entryKey = entry.getKey();
                Object amount = entry.getValue();
                if (amount instanceof Double) {
                    entries.add(new EnvEntry(entryKey, (Double) amount));
                }
            }
        }
        return entries;
    }
}
