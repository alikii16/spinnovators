package gr.det.spinnovators.EnvDataMODEL;

import java.util.Map;

public class EnvBudgetData {
    private final Map<String, EnvYear> dataByYear;
    private final Map<String, Double> envMinistryTotalBudget;

    public EnvBudgetData(Map<String, EnvYear> dataByYear, Map<String, Double> envMinistryTotalBudget) {
        this.dataByYear = dataByYear;
        this.envMinistryTotalBudget = envMinistryTotalBudget;
    }

    public EnvYear getBudgetForYear(String year) {
        if (dataByYear == null) return null;
        return dataByYear.get(year);
    }

    public Map<String, Double> getEnvMinistryTotalBudget() {
        return envMinistryTotalBudget;
    }
    
    public Map<String, EnvYear> getDataByYear() {
        return dataByYear;
    }
}
