package gr.det.spinnovators.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * EnvBudgetLoader is responsible for reading the JSON file and creating
 * the hierarchical structure of EnvBudgetData objects.
 *
 * <p>This class loads budget data from a JSON resource file, parses it using Gson,
 * and transforms it into a structured object model for use in the application.
 * It handles errors gracefully by returning an empty model on failure.</p>
 *
 * @author Spinnovators Team
 * @version 2.0
 */
public class EnvBudgetLoader {

  // Constants for JSON field names to avoid magic strings
  private static final String JSON_FILE_NAME = "env_budget_data.json";
  private static final String FIELD_DATA_BY_YEAR = "data_by_year";
  private static final String FIELD_TOTAL_BUDGET = "env_ministry_total_budget";
  private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

  // Logger for structured error reporting
  private static final Logger LOGGER = Logger.getLogger(EnvBudgetLoader.class.getName());

  private final Gson gson = new Gson();

  /**
   * Loads and parses the budget data from the JSON resource file.
   *
   * <p>This method reads the JSON file, validates its structure, and transforms
   * the raw data into a structured {@link EnvBudgetData} object containing all
   * budget information organized by year, sector, unit, and entry.</p>
   *
   * <p>If any error occurs during loading or parsing (file not found, invalid JSON,
   * transformation errors), this method logs the error and returns an empty
   * {@link EnvBudgetData} object to ensure the application can continue running.</p>
   *
   * @return The fully constructed EnvBudgetData object, or an empty object on error.
   */
  public EnvBudgetData loadBudget() {
    EnvBudgetData emptyModel = createEmptyModel();

    InputStream inputStream = locateResourceFile();
    if (inputStream == null) {
      LOGGER.log(Level.SEVERE, "JSON file not found: {0}", JSON_FILE_NAME);
      return emptyModel;
    }

    try {
      Map<String, Object> rootMap = parseJsonFile(inputStream);

      if (!validateRootStructure(rootMap)) {
        LOGGER.log(Level.SEVERE,
            "JSON structure is invalid (missing ''{0}'')", FIELD_DATA_BY_YEAR);
        return emptyModel;
      }

      return buildBudgetDataModel(rootMap);

    } catch (JsonSyntaxException e) {
      LOGGER.log(Level.SEVERE, "JSON syntax error", e);
      return emptyModel;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during data transformation", e);
      return emptyModel;
    }
  }

  /**
   * Creates an empty EnvBudgetData model to be used as fallback.
   *
   * @return An empty EnvBudgetData instance with no data.
   */
  private EnvBudgetData createEmptyModel() {
    return new EnvBudgetData(new HashMap<>(), new HashMap<>());
  }

  /**
   * Locates the JSON resource file in the classpath.
   *
   * @return InputStream for the resource file, or null if not found.
   */
  protected InputStream locateResourceFile() {
    return getClass().getClassLoader().getResourceAsStream(JSON_FILE_NAME);
  }

  /**
   * Parses the JSON file from the input stream into a raw Map structure.
   *
   * @param inputStream The input stream containing JSON data.
   * @return A Map representing the parsed JSON structure.
   * @throws IOException If an I/O error occurs while reading.
   * @throws JsonSyntaxException If the JSON is malformed.
   */
  private Map<String, Object> parseJsonFile(InputStream inputStream)
      throws IOException, JsonSyntaxException {


    try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      // Using TypeToken for better type safety with Gson
      return gson.fromJson(reader, MAP_TYPE);  
    }
  }

  /**
   * Validates that the root JSON structure contains required fields.
   *
   * @param rootMap The parsed root JSON map.
   * @return true if the structure is valid, false otherwise.
   */
  private boolean validateRootStructure(Map<String, Object> rootMap) {
    return rootMap != null && rootMap.containsKey(FIELD_DATA_BY_YEAR);
  }

  /**
   * Builds the complete EnvBudgetData model from the parsed JSON structure.
   *
   * @param rootMap The validated root JSON map.
   * @return A fully constructed EnvBudgetData object.
   */
  private EnvBudgetData buildBudgetDataModel(Map<String, Object> rootMap) {
    // Extract and transform the two main components
    Map<String, Double> totalBudget =
        transformTotalBudget(getMapFromRoot(rootMap, FIELD_TOTAL_BUDGET));
    Map<String, EnvYear> dataByYear =
        transformYears(getMapFromRoot(rootMap, FIELD_DATA_BY_YEAR));

    return new EnvBudgetData(dataByYear, totalBudget);
  }

  /**
   * Safely extracts a map from the root structure.
   *
   * @param rootMap The root JSON map.
   * @param key The key to extract.
   * @return The extracted map, or an empty map if not found.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> getMapFromRoot(Map<String, Object> rootMap, String key) {
    Object value = rootMap.get(key);
    if (value instanceof Map) {
      return (Map<String, Object>) value;
    }
    return new HashMap<>();
  }

  /**
   * Transforms the raw budget total map into a typed Map of String to Double.
   *
   * <p>Extracts year-to-budget mappings from the JSON structure, ensuring
   * that all values are properly converted to Double type.</p>
   *
   * @param budgetMap The raw map containing total budget data.
   * @return A typed map of year (String) to budget amount (Double).
   */
  private Map<String, Double> transformTotalBudget(Map<String, Object> budgetMap) {
    Map<String, Double> totalBudget = new HashMap<>();

    if (budgetMap == null || budgetMap.isEmpty()) {
      return totalBudget;
    }

    for (Map.Entry<String, Object> entry : budgetMap.entrySet()) {
      String year = entry.getKey();
      Object amount = entry.getValue();

      // Gson reads decimal numbers as Double
      if (amount instanceof Double) {
        totalBudget.put(year, (Double) amount);
      } else {
        LOGGER.log(Level.WARNING, "Skipping non-Double budget value for year: {0}", year);
      }
    }
    return totalBudget;
  }

  /**
   * Transforms the raw Map of years into structured EnvYear objects.
   *
   * <p>Iterates through each year in the data and constructs a complete
   * hierarchy of sectors, units, and entries for that year.</p>
   *
   * @param yearsMap The raw map containing data organized by year.
   * @return A map of year (String) to EnvYear objects.
   */
  @SuppressWarnings("unchecked")
  private Map<String, EnvYear> transformYears(Map<String, Object> yearsMap) {
    Map<String, EnvYear> envYears = new HashMap<>();

    if (yearsMap == null || yearsMap.isEmpty()) {
      return envYears;
    }

    for (Map.Entry<String, Object> entry : yearsMap.entrySet()) {
      String year = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Map) {
        Map<String, Object> sectorsMap = (Map<String, Object>) value;
        List<EnvSector> sectors = transformSectors(sectorsMap);
        envYears.put(year, new EnvYear(year, sectors));
      } else {
        LOGGER.log(Level.WARNING, "Invalid data structure for year: {0}", year);
      }
    }
    return envYears;
  }

  /**
   * Transforms the raw Map of sectors into structured EnvSector objects.
   *
   * <p>Each sector contains multiple units, which are processed by
   * the {@link #transformUnits(Map)} method.</p>
   *
   * @param sectorsMap The raw map containing sector data.
   * @return A list of EnvSector objects.
   */
  @SuppressWarnings("unchecked")
  private List<EnvSector> transformSectors(Map<String, Object> sectorsMap) {
    List<EnvSector> sectors = new ArrayList<>();

    if (sectorsMap == null || sectorsMap.isEmpty()) {
      return sectors;
    }

    for (Map.Entry<String, Object> entry : sectorsMap.entrySet()) {
      String sectorKey = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Map) {
        Map<String, Object> unitsMap = (Map<String, Object>) value;
        List<EnvUnit> units = transformUnits(unitsMap);
        sectors.add(new EnvSector(sectorKey, units));
      } else {
        LOGGER.log(Level.WARNING, "Invalid data structure for sector: {0}", sectorKey);
      }
    }
    return sectors;
  }

  /**
   * Transforms the raw Map of units into structured EnvUnit objects.
   *
   * <p>Each unit contains multiple budget entries, which are processed by
   * the {@link #transformEntries(Map)} method.</p>
   *
   * @param unitsMap The raw map containing unit data.
   * @return A list of EnvUnit objects.
   */
  @SuppressWarnings("unchecked")
  private List<EnvUnit> transformUnits(Map<String, Object> unitsMap) {
    List<EnvUnit> units = new ArrayList<>();

    if (unitsMap == null || unitsMap.isEmpty()) {
      return units;
    }

    for (Map.Entry<String, Object> entry : unitsMap.entrySet()) {
      String unitKey = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Map) {
        Map<String, Object> entriesMap = (Map<String, Object>) value;
        List<EnvEntry> entries = transformEntries(entriesMap);
        units.add(new EnvUnit(unitKey, entries));
      } else {
        LOGGER.log(Level.WARNING, "Invalid data structure for unit: {0}", unitKey);
      }
    }
    return units;
  }

  /**
   * Transforms the raw Map of entries into structured EnvEntry objects.
   *
   * <p>Each entry represents a specific budget line item with a category key
   * and a monetary amount.</p>
   *
   * @param entriesMap The raw map containing entry data.
   * @return A list of EnvEntry objects.
   */
  private List<EnvEntry> transformEntries(Map<String, Object> entriesMap) {
    List<EnvEntry> entries = new ArrayList<>();

    if (entriesMap == null || entriesMap.isEmpty()) {
      return entries;
    }

    for (Map.Entry<String, Object> entry : entriesMap.entrySet()) {
      String entryKey = entry.getKey();
      Object amount = entry.getValue();

      if (amount instanceof Double) {
        entries.add(new EnvEntry(entryKey, (Double) amount));
      } else {
        LOGGER.log(Level.WARNING, "Skipping non-Double amount for entry: {0}", entryKey);
      }
    }
    return entries;
  }
}
