package com.example.budgetapp.model;

/**
 * Sector model for 'spatial_planning_and_urban_environment'.
 */
public class SpatialPlanningSector {
    
    private BudgetEntry general_secretariat_for_spatial_and_urban_planning;
    private BudgetEntry other_ministerial_units_sp_ue;
    private BudgetEntry recovery_and_resilience_fund_expenses_sp_ue;

    public SpatialPlanningSector() {}
    
    // Getters
    public BudgetEntry getGeneral_secretariat_for_spatial_and_urban_planning()
    {
        return general_secretariat_for_spatial_and_urban_planning;
    }
    
    public BudgetEntry getOther_ministerial_units_sp_ue()
    {
        return other_ministerial_units_sp_ue;
    }
    
    public BudgetEntry getRecovery_and_resilience_fund_expenses_sp_ue()
    {
        return recovery_and_resilience_fund_expenses_sp_ue;
    }
    
    // Setters
    public void setGeneral_secretariat_for_spatial_and_urban_planning(BudgetEntry general_secretariat_for_spatial_and_urban_planning)
    {
        this.general_secretariat_for_spatial_and_urban_planning = general_secretariat_for_spatial_and_urban_planning;
    }
    
    public void setOther_ministerial_units_sp_ue(BudgetEntry other_ministerial_units_sp_ue)
    {
        this.other_ministerial_units_sp_ue = other_ministerial_units_sp_ue;
    }
    
    public void setRecovery_and_resilience_fund_expenses_sp_ue(BudgetEntry recovery_and_resilience_fund_expenses_sp_ue)
    {
        this.recovery_and_resilience_fund_expenses_sp_ue = recovery_and_resilience_fund_expenses_sp_ue;
    }
}
