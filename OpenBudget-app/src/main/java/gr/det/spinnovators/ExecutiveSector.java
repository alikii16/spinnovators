package com.example.budgetapp.model;

/**
 * Sector model for 'executive_coordination_and_investments'.
 */
public class ExecutiveSector {
    
    private BudgetEntry ministerial_secretariats_and_offices;
    private BudgetEntry other_ministerial_units;
    private BudgetEntry recovery_and_resilience_fund_expenses;

    public ExecutiveSector() {}
    
    // Getters
    public BudgetEntry getMinisterial_secretariats_and_offices()
    {
        return ministerial_secretariats_and_offices;
    }
    
    public BudgetEntry getOther_ministerial_units()
    {
        return other_ministerial_units;
    }
    
    public BudgetEntry getRecovery_and_resilience_fund_expenses()
    {
        return recovery_and_resilience_fund_expenses;
    }
    
    // Setters
    public void setMinisterial_secretariats_and_offices(BudgetEntry ministerial_secretariats_and_offices)
    {
        this.ministerial_secretariats_and_offices = ministerial_secretariats_and_offices;
    }
    
    public void setOther_ministerial_units(BudgetEntry other_ministerial_units)
    {
        this.other_ministerial_units = other_ministerial_units;
    }
    
    public void setRecovery_and_resilience_fund_expenses(BudgetEntry recovery_and_resilience_fund_expenses)
    {
        this.recovery_and_resilience_fund_expenses = recovery_and_resilience_fund_expenses;
    }
}
