package com.example.budgetapp.model;

/**
 * POJO model for detailed budget structure per year.
 *Assumes implicit mapping for numeric keys.
 */
public class DetailedDataByYear {

    // Using simple names
    private YearlyBudget year2023;
    private YearlyBudget year2024;
    private YearlyBudget year2025;

    public DetailedDataByYear() {}
    
    // Getters
    public YearlyBudget getYear2023()
    {
        return year2023;
    }
    
    public YearlyBudget getYear2024()
    {
        return year2024;
    }
    
    public YearlyBudget getYear2025()
    {
        return year2025;
    }
    
    // Setters
    public void setYear2023(YearlyBudget year2023)
    {
        this.year2023 = year2023;
    }
    
    public void setYear2024(YearlyBudget year2024)
    {
        this.year2024 = year2024;
    }
    
    public void setYear2025(YearlyBudget year2025)
    {
        this.year2025 = year2025;
    }
