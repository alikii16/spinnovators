package gr.det.spinnovators;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Similar to MinistryDataInput, but for Environment Ministry budget data.
 * Loads data from JSON once and stores in arrays for fast access.
 */
public class EnvMinistryDataInput {
    
    private static final String JSON_FILE_NAME = "env_budget_data.json";
    private final Gson gson = new Gson();
    private final EnvBudgetTranslator translator = new EnvBudgetTranslator();
    
    // Data for each year: List of entries (sector, unit, entry, amount)
    private List<EnvBudgetEntry> data2025;
    private List<EnvBudgetEntry> data2024;
    private List<EnvBudgetEntry> data2023;
    
    public EnvMinistryDataInput() {
        loadData();
    }
    
    private void loadData() {
        System.out.println("EnvMinistryDataInput: Loading data from JSON...");
        
        InputStream is = getClass().getResourceAsStream("/" + JSON_FILE_NAME);
        if (is == null) {
            is = getClass().getResourceAsStream(JSON_FILE_NAME);
        }
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(JSON_FILE_NAME);
        }
        
        if (is == null) {
            System.err.println("EnvMinistryDataInput: JSON file not found: " + JSON_FILE_NAME);
            data2025 = new ArrayList<>();
            data2024 = new ArrayList<>();
            data2023 = new ArrayList<>();
            return;
        }
        
        try (JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> rootMap = gson.fromJson(reader, mapType);
            
            if (rootMap == null || !rootMap.containsKey("data_by_year")) {
                System.err.println("EnvMinistryDataInput: Invalid JSON structure");
                data2025 = new ArrayList<>();
                data2024 = new ArrayList<>();
                data2023 = new ArrayList<>();
                return;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> dataByYear = (Map<String, Object>) rootMap.get("data_by_year");
            
            data2025 = parseYearData("2025", dataByYear);
            data2024 = parseYearData("2024", dataByYear);
            data2023 = parseYearData("2023", dataByYear);
            
            System.out.println("EnvMinistryDataInput: Data loaded successfully");
            System.out.println("  - 2025: " + data2025.size() + " entries");
            System.out.println("  - 2024: " + data2024.size() + " entries");
            System.out.println("  - 2023: " + data2023.size() + " entries");
            
        } catch (Exception e) {
            System.err.println("EnvMinistryDataInput: Error loading data: " + e.getMessage());
            e.printStackTrace();
            data2025 = new ArrayList<>();
            data2024 = new ArrayList<>();
            data2023 = new ArrayList<>();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<EnvBudgetEntry> parseYearData(String year, Map<String, Object> dataByYear) {
        List<EnvBudgetEntry> entries = new ArrayList<>();
        
        if (!dataByYear.containsKey(year)) {
            return entries;
        }
        
        Map<String, Object> yearData = (Map<String, Object>) dataByYear.get(year);
        
        // Iterate through sectors
        for (Map.Entry<String, Object> sectorEntry : yearData.entrySet()) {
            String sectorKey = sectorEntry.getKey();
            String sectorName = translator.translateCategory(sectorKey);
            
            Map<String, Object> sectorData = (Map<String, Object>) sectorEntry.getValue();
            
            // Iterate through units
            for (Map.Entry<String, Object> unitEntry : sectorData.entrySet()) {
                String unitKey = unitEntry.getKey();
                String unitName = translator.translateCategory(unitKey);
                
                Map<String, Object> unitData = (Map<String, Object>) unitEntry.getValue();
                
                // Iterate through entries (personnel_costs, purchase_of_goods_and_services, etc.)
                for (Map.Entry<String, Object> entryEntry : unitData.entrySet()) {
                    String entryKey = entryEntry.getKey();
                    String entryName = translator.translateCategory(entryKey);
                    double amount = ((Number) entryEntry.getValue()).doubleValue();
                    
                    entries.add(new EnvBudgetEntry(sectorName, unitName, entryName, amount));
                }
            }
        }
        
        return entries;
    }
    
    public List<EnvBudgetEntry> getData2025() {
        return data2025;
    }
    
    public List<EnvBudgetEntry> getData2024() {
        return data2024;
    }
    
    public List<EnvBudgetEntry> getData2023() {
        return data2023;
    }
    
    public List<EnvBudgetEntry> getDataForYear(String year) {
        switch (year) {
            case "2025":
                return data2025;
            case "2024":
                return data2024;
            case "2023":
                return data2023;
            default:
                return new ArrayList<>();
        }
    }
    
    /**
     * Simple data class to hold a single budget entry
     */
    public static class EnvBudgetEntry {
        private final String sector;
        private final String unit;
        private final String entry;
        private final double amount;
        
        public EnvBudgetEntry(String sector, String unit, String entry, double amount) {
            this.sector = sector;
            this.unit = unit;
            this.entry = entry;
            this.amount = amount;
        }
        
        public String getSector() { return sector; }
        public String getUnit() { return unit; }
        public String getEntry() { return entry; }
        public double getAmount() { return amount; }
    }
}

