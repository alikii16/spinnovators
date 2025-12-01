package gr.det.spinnovators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class EnvBudgetService {

  private ArrayList<EnvBudgetEntry> list2025;
  private ArrayList<EnvBudgetEntry> list2026;
  
  public void loadData(String filename) {
    Gson gson = new Gson();

    try (FileReader reader = new FileReader(filename)) {
        
        Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>>(){}.getType();
    
        Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> rootMap = gson.fromJson(reader, type);

        if (rootMap == null || !rootMap.containsKey("data_by_year")) {
            System.out.println("Σφάλμα: Δεν βρέθηκε το κλειδί 'data_by_year'.");
            return;
        }

        Map<String, Map<String, Map<String, Map<String, Double>>>> yearsMap = rootMap.get("data_by_year");

        for (Map.Entry<String, Map<String, Map<String, Map<String, Double>>>> yearEntry : yearsMap.entrySet()) {
          String year = yearEntry.getKey(); 
          for (Map.Entry<String, Map<String, Map<String, Double>>> sectorEntry : yearEntry.getValue().entrySet()) {
            String sector = sectorEntry.getKey();    
              for (Map.Entry<String, Map<String, Double>> unitEntry : sectorEntry.getValue().entrySet()) {
                String unit = unitEntry.getKey();      
                  for (Map.Entry<String, Double> costEntry : unitEntry.getValue().entrySet()) {
                    String costType = costEntry.getKey();
                    Double amount = costEntry.getValue();

                      EnvBudgetEntry record = new EnvBudgetEntry(sector, unit, costType, amount);

                      if (year.equals("2025")) {
                        list2025.add(record);
                      } else if (year.equals("2026")) {
                        list2026.add(record);
                      }
                  }
              }
          }
        }

    } catch (IOException e) {
        System.out.println("Σφάλμα: " + e.getMessage());
    }
  }
    public ArrayList<EnvBudgetEntry> getList2025() { return list2025; }
    public ArrayList<EnvBudgetEntry> getList2026() { return list2026; }
}
