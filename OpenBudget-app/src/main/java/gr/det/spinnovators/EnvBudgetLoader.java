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
  * EnvBudgetLoader is responsible for reading the JSON file and creating the hierarchical
  * structure of EnvBudgetData objects.
 */

public class EnvBudgetLoader {

    private static final String JSON_FILE_NAME = "env_budget_data.json";
    private final Gson gson = new Gson();

    /**
     * The main method. Reads the JSON and initiates the transformation.
     * @return The fully constructed EnvBudgetData object.
     * @throws EnvDataLoadException If file reading, file lookup, or JSON parsing fails.
     */

    public EnvBudgetData loadBudget() throws EnvDataLoadException {
        // Using the ClassLoader to find the file within the classpath (e.g., src/main/resources)

        InputStream is = getClass().getResourceAsStream(JSON_FILE_NAME);

        if (is == null) {
        throw new EnvDataLoadException("Error: JSON file not found: " + JSON_FILE_NAME + ". Ensure it is in src/main/resources.");
        }

        // Starting try-with-resources block for automatic resource closing.
        try (

            InputStream nonNullIs = is;

            JsonReader reader = new JsonReader(new InputStreamReader(nonNullIs));
        ) {

            Type mapType = new TypeToken<Map<String, Object>>() {}.getType(); // Defining the root structure type for Gson (a Map from String to generic Object)
            Map<String, Object> rootMap = gson.fromJson(reader, mapType); // Parsing the entire JSON file into a raw Map structure

            // Validation that the JSON root contains the necessary key
            if (rootMap == null || !rootMap.containsKey("data_by_year")) {
                 throw new EnvDataLoadException("Error: The structure of the JSON file is invalid (missing 'data_by_year').");
            }

            // Extraction and conversion to Model Objects using the helper methods
            Map<String, Double> totalBudget = transformTotalBudget((Map<String, Object>) rootMap.get("env_ministry_total_budget"));
            Map<String, EnvYear> dataByYear = transformYears((Map<String, Object>) rootMap.get("data_by_year"));

            // Returning the final structured data
            return new EnvBudgetData(dataByYear, totalBudget);

        } catch (IOException e) {
            // Error during closing or reading the stream
            throw new EnvDataLoadException("I/O error during file loading or closing resources.", e);
        } catch (JsonSyntaxException e) {
            // Error if the JSON content is not correctly formatted
            throw new EnvDataLoadException("JSON syntax error. Check commas and braces.", e);
        }
    }

    /** Helper method to transform the raw budget total map into a typed Map<String, Double> */
    private Map<String, Double> transformTotalBudget(Map<String, Object> budgetMap) {

        Map<String, Double> totalBudget = new HashMap<>();

        if (budgetMap != null) {
            // Gson reads all decimal numbers as Double

            for (Map.Entry<String, Object> entry : budgetMap.entrySet()) {
            String year = entry.getKey();
            Object amount = entry.getValue();

            // Gson reads amounts as Double, so we check if it is actually Double
            // If so, we add it straight to the Map list
              if (amount instanceof Double) {
                totalBudget.put(year, (Double) amount);
            }
        }
    }
    return totalBudget;
}

    /** Transforms the raw Map of years into structured EnvYear objects */
    private Map<String, EnvYear> transformYears(Map<String, Object> yearsMap) {

        Map<String, EnvYear> envYears = new HashMap<>();

        if (yearsMap != null) {
            for (Map.Entry<String, Object> entry : yearsMap.entrySet()) {
                String year = entry.getKey();
                // The value is the raw map of sectors for that year
                Map<String, Object> sectorsMap = (Map<String, Object>) entry.getValue();

                List<EnvSector> sectors = transformSectors(sectorsMap);
                envYears.put(year, new EnvYear(year, sectors));
            }
        }
        return envYears;
    }

    /** Transforms the raw Map of sectors into structured EnvSector objects. */
    private List<EnvSector> transformSectors(Map<String, Object> sectorsMap) {

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

    /** Helper method for creating EnvUnit objects. */
    private List<EnvUnit> transformUnits(Map<String, Object> unitsMap) {

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

    /** Helper method for creating EnvEntry objects. */
    private List<EnvEntry> transformEntries(Map<String, Object> entriesMap) {

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
