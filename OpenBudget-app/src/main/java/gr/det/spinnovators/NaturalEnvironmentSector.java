package com.example.budgetapp.model;

/**
 * Sector model for 'natural_environment_and_water_protection'.
 */
public class NaturalEnvironmentSector {
    
    private BudgetEntry general_secretariat_for_natural_environment_and_water;
    private BudgetEntry general_secretariat_for_waste_management;
    private BudgetEntry general_secretariat_for_forestry;
    private BudgetEntry other_units_minister_secretary;
    private BudgetEntry recovery_and_resilience_fund_expenses;

    public NaturalEnvironmentSector() {}
    
    // Getters
    public BudgetEntry getGeneral_secretariat_for_natural_environment_and_water()
    {
        return general_secretariat_for_natural_environment_and_water;
    }
    
    public BudgetEntry getGeneral_secretariat_for_waste_management()
    {
        return general_secretariat_for_waste_management;
    }
    
    public BudgetEntry getGeneral_secretariat_for_forestry()
    {
        return general_secretariat_for_forestry;
    }
    
    public BudgetEntry getOther_units_minister_secretary()
    {
        return other_units_minister_secretary;
    }
    
    public BudgetEntry getRecovery_and_resilience_fund_expenses()
    {
        return recovery_and_resilience_fund_expenses;
    }
    
    // Setters
    public void setGeneral_secretariat_for_natural_environment_and_water(BudgetEntry general_secretariat_for_natural_environment_and_water)
    {
        this.general_secretariat_for_natural_environment_and_water = general_secretariat_for_natural_environment_and_water;
    }
    
    public void setGeneral_secretariat_for_waste_management(BudgetEntry general_secretariat_for_waste_management)
    {
        this.general_secretariat_for_waste_management = general_secretariat_for_waste_management;
    }
    
    public void setGeneral_secretariat_for_forestry(BudgetEntry general_secretariat_for_forestry)
    {
        this.general_secretariat_for_forestry = general_secretariat_for_forestry;
    }
    
    public void setOther_units_minister_secretary(BudgetEntry other_units_minister_secretary)
    {
        this.other_units_minister_secretary = other_units_minister_secretary;
    }
    
    public void setRecovery_and_resilience_fund_expenses(BudgetEntry recovery_and_resilience_fund_expenses)
    {
        this.recovery_and_resilience_fund_expenses = recovery_and_resilience_fund_expenses;
    }
}
