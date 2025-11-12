package com.example.budgetapp.model;

/**
 * POJO model for total budget amounts per year.
 * Assumes Java naming can implicitly map 
 * to the numeric JSON keys (e.g., year2023 maps to "2023"), which is unconventional.
 */
public class TotalBudgetDetails {

    // Using simple names
    private Double year2023;
    private Double year2024;
    private Double year2025;

    public TotalBudgetDetails() {}
    
    // Getters
    public Double getYear2023()
    {
        return year2023;
    }
    
    public Double getYear2024()
    {
        return year2024;
    }
    
    public Double getYear2025()
    {
        return year2025;
    }
    
    // Setters
    public void setYear2023(Double year2023)
    {
        this.year2023 = year2023;
    }
    
    public void setYear2024(Double year2024)
    {
        this.year2024 = year2024;
    }
    
    public void setYear2025(Double year2025)
    {
        this.year2025 = year2025;
    }
}
