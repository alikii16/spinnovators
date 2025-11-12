package com.example.budgetapp.model;

/**
 * Sector model for 'energy_and_mineral_resources_management'.
 */
public class EnergySector {
    
    private BudgetEntry general_secretariat_for_energy_and_mineral_resources;
    private BudgetEntry other_ministerial_units_energy_mgt;
    private BudgetEntry recovery_and_resilience_fund_expenses_energy_mgt;

    public EnergySector() {}
    
    // Getters
    public BudgetEntry getGeneral_secretariat_for_energy_and_mineral_resources()
    {
        return general_secretariat_for_energy_and_mineral_resources;
    }
    
    public BudgetEntry getOther_ministerial_units_energy_mgt()
    {
        return other_ministerial_units_energy_mgt;
    }
    
    public BudgetEntry getRecovery_and_resilience_fund_expenses_energy_mgt()
    {
        return recovery_and_resilience_fund_expenses_energy_mgt;
    }
    
    // Setters
    public void setGeneral_secretariat_for_energy_and_mineral_resources(BudgetEntry general_secretariat_for_energy_and_mineral_resources)
    {
        this.general_secretariat_for_energy_and_mineral_resources = general_secretariat_for_energy_and_mineral_resources;
    }
    
    public void setOther_ministerial_units_energy_mgt(BudgetEntry other_ministerial_units_energy_mgt)
    {
        this.other_ministerial_units_energy_mgt = other_ministerial_units_energy_mgt;
    }
    
    public void setRecovery_and_resilience_fund_expenses_energy_mgt(BudgetEntry recovery_and_resilience_fund_expenses_energy_mgt)
    {
        this.recovery_and_resilience_fund_expenses_energy_mgt = recovery_and_resilience_fund_expenses_energy_mgt;
    }
}
