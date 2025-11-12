package com.example.budgetapp.model;

/**
 * The central POJO model representing the entire JSON budget file.
 */
public class MinistryBudget {

 
    private TotalBudgetDetails env_ministry_total_budget;
    private DetailedDataByYear data_by_year;

    public MinistryBudget() {}
    
    // Getters
    public TotalBudgetDetails getEnv_ministry_total_budget()
    {
        return env_ministry_total_budget;
    }
    
    public DetailedDataByYear getData_by_year()
    {
        return data_by_year;
    }

    // Setters
    public void setEnv_ministry_total_budget(TotalBudgetDetails env_ministry_total_budget)
    {
        this.env_ministry_total_budget = env_ministry_total_budget;
    }
    
    public void setData_by_year(DetailedDataByYear data_by_year)
    {
        this.data_by_year = data_by_year;
    }
    
    /**
     * Helper method easily retrieves the DetailedDataByYear data container.
     */
    public DetailedDataByYear getDetailedBudget()
    {
        return data_by_year;
    }
}
