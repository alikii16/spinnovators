# spinnovators
# [Prime] Minister for a Day 
##  Budget Management System
### Title: OpenBudget
This is a command-line application that allows the user to view, edit, and analyze the Greek state budget, focusing on Ministry of Environment and Energy — as if someone were the *Prime Minister for a day*.  
The program provides an overview of key budget categories, supports the introduction of changes, checks for rule violations and compares made-up scenarios. 
## Main Features
### 1. Budget Overview:
Displays all main categories of the national budget (e.g. Education, Health, Defense, Infrastructure, etc.).
### 2. Data Editing and Updates
Users can input changes directly through the command line. The system allows simulation of revised budget allocations — for instance, increasing investments in renewable energy or reducing defense spending — and automatically recalculates totals and balances.
### 3. Restriction and Validation Checks
Automatic checks ensure that user modifications comply with fiscal and legal rules (e.g., no negative budgets, total spending cannot exceed total income).
### 4. Change Tracking
A detailed log shows every modification made during the session, including before-and-after comparisons for each category.
### 5. Ministry of Environment and Energy Focus
Special emphasis on environmental policies, green energy funding, and sustainability initiatives, exploring how changes affect long-term national performance.
### 6. Yearly Budget Comparison
Compare the current budget with the previous year’s data, highlighting increases or decreases per sector.
### 7. Scenario Analysis
Simulate hypothetical “what-if” cases and analyze impacts on Technology, Health, and Environment pillars.
### 8. ESG Score Evaluation
Evaluate Environmental, Social, and Governance (ESG) indicators to measure how sustainable and responsible the proposed budget changes are.
### 9. Results and Conclusions
Summarizes user actions and outlines potential long-term effects, balancing economic growth, social welfare, and environmental protection.

---

## Purpose
The goal of **OpenBudget** is to encourage critical thinking about how national budgets are structured and how policy decisions influence sustainability, equality, and progress.  
By simulating the decision-making process, users can experience the challenges of real-world governance — stepping into the role of the **Prime Minister for a Day**.

---

## Technical Architecture and Components

### 1. Application Entry Point (App.java)
The `App` class serves as the main entry point of **OpenBudget**.  
- It starts by invoking `FirstLogin.login()` to authenticate users as either a Minister or an employee.  
- After login, it initializes `MinistryDataInput` and `FullBudgetPrinter` objects.  
- Users can select the year of the budget to display (2023, 2024, 2025) or exit with `0000`.  
- The system prints a formatted table of ministries and their allocated budgets with a total summary.

### 2. Data Model (MinistryDataInput.java)
- Contains the budget data for three years: 2023, 2024, 2025.  
- Each year has an array of **ministry names** and their respective **budget amounts**.  
- Provides getter methods to access names, amounts, and the number of ministries for each year.  
- Supports fast lookup for printing or calculations in the CLI.

### 3. Budget Display (FullBudgetPrinter.java)
- Accepts a `MinistryDataInput` object and formats the display of each year's budget.  
- Prints each ministry’s allocation with **currency formatting**, totals, and separators.  
- Flexible for expansion with additional formatting or highlighting changes between years.

### 4. User Authentication (FirstLogin.java)
- Handles **login functionality** for the system.  
- Users can log in as: 
  - `Minister` with password `m1n1st3r`
  - Any other employee with password `3mpl0y33`
- The system enforces repeated login attempts until valid credentials are entered.

### 5. Environment Ministry Data Handling
The application also includes a **hierarchical and detailed structure** for the Ministry of Environment and Energy:

#### a. Model Classes (Inside envdatamodel file)
- `EnvBudgetData`: Holds all budget data per year, including **total ministry budget**.  
- `EnvYear`: Represents budget data for a specific year, including multiple `EnvSector` objects.  
- `EnvSector`: Represents major policy sectors (e.g., energy, natural resources) and contains `EnvUnit` objects.  
- `EnvUnit`: Represents administrative units (e.g., General Secretariat) with multiple `EnvEntry` items.  
- `EnvEntry`: Represents the smallest budget entry (e.g., personnel or operational costs) with **amounts and setters** for updating.

#### b. Data Loading (EnvBudgetLoader.java)
- Reads JSON data from `env_budget_data.json` in `src/main/resources`.  
- Constructs the complete hierarchy: year → sector → unit → entry.  
- Handles **JSON syntax errors** and **file-not-found exceptions** using `EnvDataLoadException`.

#### c. Translation Support (EnvBudgetTranslator.java)
- Loads `env_budget_translations.properties` to map JSON keys to official Greek ministry descriptions.  
- Provides safe translation with fallback to readable names if a key is missing.  
- Enhances user-friendliness in CLI output.

#### d. Error Handling (EnvDataLoadException.java)
- Custom exception for any **loading or parsing errors** in the Environment Ministry budget.  
- Includes constructors to wrap `IOException` or `JsonSyntaxException` with meaningful messages.

### 6. System Capabilities for Environmental Analysis
- Users can **view detailed breakdowns** of environmental sector allocations.  
- Supports **updating entries** via code (in future versions, may include CLI editing).  
- Enables **scenario testing**: modify amounts and observe impacts on total budgets.  
- Integrates **translation services** for internationalization or clear Greek descriptions.

---
## JSON and Translation Files
---

### 1. Main JSON file (env_budget_data.json)
- Contains hierarchical budget data per year.  
- Structure: year → sector → unit → entries (personnel, operational costs, assets, credits).  
- Example:
{
  "2025": {
    "executive_coordination_and_investments": {
      "ministerial_secretariats_and_offices": {
        "personnel_costs": 2256000.00,
        "purchase_of_goods_and_services": 1725000.00,
        "permanent_assets": 15000.00
      }
    }
  }
}

### 2. Maping English JSON keys to official Greek ministry descriptions (env_budget_translations.properties)
- Maps JSON keys to official Greek ministry descriptions.
- Format: key=value
- Example:
executive_coordination_and_investments=ΕΠΙΤΕΛΙΚΟΣ ΣΥΝΤΟΝΙΣΜΟΣ ΚΑΙ ΕΠΕΝΔΥΣΕΙΣ ΥΠΟΥΡΓΕΙΟΥ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ
ministerial_secretariats_and_offices=Γραμματείες Υπουργού και Γραφεία
other_ministerial_units=Λοιπές αυτοτελείς μονάδες του ΥΠΕΝ, υπαγόμενες στην πολιτική ηγεσία
recovery_and_resilience_fund_expenses=Δαπάνες Ταμείου Ανάκαμψης και Ανθεκτικότητας
personnel_costs=Παροχές σε εργαζομένους
purchase_of_goods_and_services=Αγορές αγαθών και υπηρεσιών
permanent_assets=Πάγια Περιουσιακά Στοιχεία

### 3. Ensuring the application runs correctly from the command line (pom.xml)
- Maven project configuration for OpenBudget-app.
- The Project Object Model (pom.xml) manages the project's dependencies and build process.
- Java 17, dependencies for Gson and JUnit, plugins for testing and execution.

--- 
##  System Capabilities and Constraints
---

### a. Capabilities
- Budget Review: Displays budget data for 2023, 2024, and 2025.
- Editing Functionality: Allows the user to select any leaf node (EnvEntry) and introduce a new monetary value (setAmount).
- Real-time Changes: Changes are immediately reflected in the in-memory data structure.
- Localization: Uses EnvBudgetTranslator to display all official names (Sectors, Units, Entries) in Greek.

### b. Constraints and Restrictions
- Minimum Requirement Check: The application includes a check to ensure that a newly entered budget amount is not negative (though more complex restrictions should be added by the user).
- Data Persistence: Changes are currently not saved back to the JSON file. Modifications are only valid for the current application session.
- Scope: The system focuses exclusively on the budget of the Ministry of Environment and Energy (ΥΠΕΝ).
  
---
## Future Extensions
The current system lays a foundation for advanced features:
- Interactive command-line editing for Environment Ministry entries.  
- Comparison across years with **highlighted differences** in key sectors.  
- ESG (Environmental, Social, Governance) scoring based on budget allocations.   
- Integration with GUI frontends for enhanced user experience.
