/**
 * POJO model class for mapping the sub-units within a Ministry Sector (e.g., Natural Environment).
 * * This class serves as a container for multiple BudgetEntry objects.
 * Key: The name of the Sub-unit (e.g., "general_secretariat_for_forestry").
 * Value: The BudgetEntry object containing the specific expenditures for that unit.
 * This structure helps organize the budget data within each sector.
 * Each class explicitly defines its sub-units as fields of type BudgetEntry.
 */

package com.example.budgetapp.model;

public class DepartmentBudget {

    private BudgetEntry general_secretariat_for_spatial_and_urban_planning;
    private BudgetEntry other_ministerial_units_sp_ue;
    private BudgetEntry recovery_and_resilience_fund_expenses_sp_ue;

    public DepartmentBudget() {}

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


