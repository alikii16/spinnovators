/**
 * POJO (Plain Old Java Object) model class for mapping individual expenditure amounts
 * found in the innermost JSON structure (e.g., "personnel_costs").
 * It holds the actual financial data for a specific budget unit.
 */
package com.example.budgetapp.model;

public class BudgetEntry {

    private Double personnel_costs;
    private Double purchase_of_goods_and_services;
    private Double permanent_assets;
    private Double credits_under_allocation;
    private Double transfers;
    private Double community_benefits;

    // Default constructor
    public BudgetEntry() {}

    // Getters and Setters 

    public Double getPersonnel_costs() { 
	    return personnel_costs; 
    }
    public void setPersonnel_costs(Double personnel_costs) { 
	    this.personnel_costs = personnel_costs;
    }

    public Double getPurchase_of_goods_and_services() { 
	    return purchase_of_goods_and_services;
    }
    public void setPurchase_of_goods_and_services(Double purchase_of_goods_and_services) { 
	    this.purchase_of_goods_and_services = purchase_of_goods_and_services; 
    }

    public Double getPermanent_assets() { 
	    return permanent_assets; 
    }
    public void setPermanent_assets(Double permanent_assets) { 
	    this.permanent_assets = permanent_assets; 
    }

    public Double getCredits_under_allocation() { 
	    return credits_under_allocation; 
    }
    public void setCredits_under_allocation(Double credits_under_allocation) { 
	    this.credits_under_allocation = credits_under_allocation;
    }

    public Double getTransfers() { 
	    return transfers; 
    }
    public void setTransfers(Double transfers) { 
	    this.transfers = transfers; 
    }

    public Double getCommunity_benefits() { 
	    return community_benefits; 
    }
    public void setCommunity_benefits(Double community_benefits) { 
	    this.community_benefits = community_benefits; 
    }
}
