package com.example.budgetapp.model;

/**
 * POJO container for all four sectors' budgets within a single year (e.g., "2025" or "2023").
 */
public class YearlyBudget {
    
    private ExecutiveSector executive_coordination_and_investments;
    private NaturalEnvironmentSector natural_environment_and_water_protection;
    private SpatialPlanningSector spatial_planning_and_urban_environment;
    private EnergySector energy_and_mineral_resources_management;

    public YearlyBudget() {}
    
    // Getters
    public ExecutiveSector getExecutive_coordination_and_investments()
    {
        return executive_coordination_and_investments;
    }
    
    public NaturalEnvironmentSector getNatural_environment_and_water_protection()
    {
        return natural_environment_and_water_protection;
    }
    
    public SpatialPlanningSector getSpatial_planning_and_urban_environment()
    {
        return spatial_planning_and_urban_environment;
    }
    
    public EnergySector getEnergy_and_mineral_resources_management()
    {
        return energy_and_mineral_resources_management;
    }
    
    // Setters
    public void setExecutive_coordination_and_investments(ExecutiveSector executive_coordination_and_investments)
    {
        this.executive_coordination_and_investments = executive_coordination_and_investments;
    }
    
    public void setNatural_environment_and_water_protection(NaturalEnvironmentSector natural_environment_and_water_protection)
    {
        this.natural_environment_and_water_protection = natural_environment_and_water_protection;
    }
    
    public void setSpatial_planning_and_urban_environment(SpatialPlanningSector spatial_planning_and_urban_environment)
    {
        this.spatial_planning_and_urban_environment = spatial_planning_and_urban_environment;
    }
    
    public void setEnergy_and_mineral_resources_management(EnergySector energy_and_mineral_resources_management)
    {
        this.energy_and_mineral_resources_management = energy_and_mineral_resources_management;
    }
}
