# spinnovators
# [Prime] Minister for a Day 
##  Budget Management System
### Title: OpenBudget
This is a command-line and web-based application that allows users to view, edit, and analyze the Greek state budget, focusing on the Ministry of Environment and Energy — as if someone were the *Prime Minister for a day*.
The program provides an overview of key budget categories, supports the introduction of changes, checks for rule violations, and compares made-up scenarios.   
## Main Features
### 1. Budget Overview:
Displays all main categories of the national budget (e.g. Education, Health, Defense, Infrastructure, etc.) for years 2023-2026.
### 2. Data Editing and Updates
Users can input changes directly through the command line or web interface to the Ministry of Environment and Energy. The system allows simulation of revised budget allocations — for instance, increasing investments in renewable energy or reducing permanent assets — and automatically recalculates totals and balances.
### 3. Restriction and Validation Checks
Automatic checks ensure that user modifications comply with fiscal and legal rules (e.g., no negative budgets, total spending cannot exceed total income, warnings for changes exceeding 30%).
### 4. Ministry of Environment and Energy Focus
Special emphasis on environmental policies, green energy funding, and sustainability initiatives. 
### 5. Yearly Budget Comparison
Compare the current budget with previous years' data, highlighting increases or decreases per sector.
### 6. ESG Score Evaluation
Evaluate Environmental, Social, and Governance (ESG) indicators to measure how sustainable and responsible the proposed budget changes are. The system tags budget categories as "GREEN" or "NEUTRAL" and calculates a sustainability score (scale from 0 to 100).
### 7. Updated Budget and Initial Budget Comparison
Compare the new budget, changed by the Minister, with the initial Ministry's of Environment and Energy budget, highlighting increases or decreases per sector, presenting pie charts and top changes.

---

## Purpose
The goal of **OpenBudget** is to encourage critical thinking about how national budgets are structured and how policy decisions influence sustainability, equality, and progress.  

By simulating the decision-making process, users can experience the challenges of real-world governance — stepping into the role of the **Prime Minister for a Day**.

---

## Technical Architecture and Components

### 1. Application Entry Point (OpenBudgetApplication.java)
The OpenBudgetApplication class serves as the main entry point of OpenBudget.
- It starts by invoking `FirstLogin.login()` to authenticate users as either a Minister or an employee.  
- After login, it initializes `MinistryDataInput` and `FullBudgetPrinter` objects.  
- Users can select the year of the budget to display (2023, 2024, 2025, 2026) or exit with 0000.
- The system prints a formatted table of ministries and their allocated budgets with a total summary.
- Automatically starts an embedded HTTP server on port 8080 and opens the web interface in the default browser.

### 2. Data Model (MinistryDataInput.java)
- Contains the budget data for four years: 2023, 2024, 2025, 2026.
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

## 5. Environment Ministry Data Handling
The application also includes a **hierarchical and detailed structure** for the Ministry of Environment and Energy:

#### a. Model Classes (envdatamodel package)
- `EnvBudgetData`: Holds all budget data per year, including **total ministry budget**.  
- `EnvYear`: Represents budget data for a specific year, including multiple `EnvSector` objects.  
- `EnvSector`: Represents major policy sectors (e.g., energy, natural resources) and contains `EnvUnit` objects.  
- `EnvUnit`: Represents administrative units (e.g., General Secretariat) with multiple `EnvEntry` items.  
- `EnvEntry`: Represents the smallest budget entry (e.g., personnel or operational costs) with **amounts and setters** for updating.

#### b. Data Loading (EnvBudgetLoader.java)
- Reads JSON data from `env_budget_data.json` in `src/main/resources`.  
- Constructs the complete hierarchy: year → sector → unit → entry.  
- Handles **JSON syntax errors** and **file-not-found exceptions** using `EnvDataLoadException`.
- Uses Gson library for robust JSON parsing with type safety.

#### c. Translation Support (EnvBudgetTranslator.java)
- Loads `env_budget_translations.properties` to map JSON keys to official Greek ministry descriptions.  
- Provides safe translation with fallback to readable names if a key is missing.  
- Enhances user-friendliness in CLI output.

#### d. Budget Editing (EditsApplier.java)
- Manages the interactive editing workflow for budget modifications.
- Implements state management for multi-step editing process (sector → unit → entry → value).
- Tracks balance changes and enforces budget equilibrium before allowing session termination.
- Integrates with BudgetValidator for comprehensive validation.
- Allows the comparison of 2 budgets.
- Presents the ESG Score of the Ministry's budget and offers valuable insights and recommendations.

### e. Budget Validation (BudgetValidator.java)
- **CHECK 1**: Prevents negative budget values
- **CHECK 2**: Ensures new values don't exceed total ministry budget
- **CHECK 3**: Warns about extreme deviations (>30%) and requires explicit confirmation
- Provides user-friendly error messages in Greek
- Handles non-numeric input gracefully

### f. Budget Printing Services
- EnvBudgetPrinter.java: Formats and displays detailed Environment Ministry budget with hierarchical structure
- EditsPrinter.java: Shows updated budget data after modifications

### g. ESG Scoring System
- ESG_Category.java: Enumeration representing ESG (Environmental, Social, Governance) categories for budget classification and sustainability scoring.
- ESG_Report.java: Represents a complete ESG sustainability report for a ministry budget.
- ESG_Classifier.java: Classifies budget sectors, units, and entries into ESG categories.
- ESG_Score_Calculator.java: Calculates ESG sustainability scores for ministry budgets.
- ESG_Printer.java: Prints formatted ESG sustainability reports to the console.
- ESG_Loader.java: Provides centralized access to ESG weights, thresholds, classifications, and display settings defined in esg_mappings.json.

### h. Updated Budget Comparison with the Initial Budget (InitialBudgetComparison.java)
- Analyzes and compares budget data before and after changes.
- Provides comprehensive comparison including:
  - Sector-by-sector breakdown
  - Percentage changes
  - Visual pie charts
  - ESG impact analysis
  - Recommendations based on changes
  - 
### 6. System Capabilities for Environmental Analysis
- Users can **view detailed breakdowns** of environmental sector allocations.  
- Supports **updating entries** via code (in future versions, may include CLI editing).  
- Enables **scenario testing**: modify amounts and observe impacts on total budgets.  
- Integrates **translation services** for internationalization or clear Greek descriptions.
- Integrates **balance tracking**, as running balance is maintained to ensure budget equilibrium.



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

### 3. ESG Configuration File (esg_mappings.json)
- Defines the ESG (Environmental, Social, Governance) evaluation logic, scoring rules, and display behavior used by the system to analyze and present budget data.
- Makes the ESG system fully configurable without code changes
- Ensures transparent, explainable, and consistent ESG evaluation

### 4. Maven Configuration (pom.xml)
- Maven project configuration for OpenBudget-app.
- The Project Object Model (pom.xml) manages the project's dependencies and build process.
- Java 17, dependencies for Gson and JUnit, plugins for testing and execution.

---
## Front-End HTML Interfaces 
---

### 1. Web Server, Authentication & Dynamic Budget Rendering (LoginWebServer.java)
- Implements an embedded HTTP server using com.sun.net.httpserver.HttpServer
- Serves HTML pages from src/main/resources/frontend/
- Authenticates users as either Minister or Employee and redirects them to the appropriate dashboard.
- Dynamic Budget Rendering: Injects budget data into HTML templates
- Change Budget Workflow: Multi-step state machine for web-based budget editing
- Provides endpoints for:
  - /login - User authentication
  - /minister_statebudget.html - Full state budget view
  - /minister_budget.html - Environment ministry budget
  - /employee_statebudget.html - Employee view
  - /change-budget - Interactive budget modification

### 2. Display of the employee dashboard for selecting a year and viewing Environment Ministry budget data (employee_budget.html)
- Description: Main interface for employees to choose a fiscal year.
- Function: Sends the selected year to the server for budget processing.
- Backend Interaction: Communicates with /employee/year POST handler.
- UI Elements: Year dropdown, submit button, navigation links.

### 3. Employee interface for accessing State Budget data (employee_statebudget.html)
- Description: Dashboard for navigating to State Budget tools.
- Input Handling: Captures the chosen fiscal year for processing.
- Dynamic Placeholders: Displays the logged-in employee name.
- Navigation: Links to return to login or go to detailed analysis pages.

### 4. Login interface for all system users (login.html)
- Description: Landing page with username/password fields.
- Function: Sends credentials to /login for verification.
- Backend Interaction: Works with the login POST handler.
- UI Elements: Input fields, submit button, error message area.

### 5. Display of the minister dashboard for selecting a budget year (minister_budget.html)
- Description: Main page for the minister to enter a year and view the Environment Ministry budget.
- Function: Sends the chosen year to minister_statebudget.html for processing.
- UI Elements: Year input field, “Show Budget” button, navigation link back to State Budget page.

### 6. Display of the detailed state budget results for the minister (minister_statebudget.html) 
- Description: Displays the selected year’s State Budget for the minister.
- Function: Receives the year input and shows budget data in a structured format.
- UI Elements: Year input field, “Show Budget” button, link to ministry budget overview.

--- 
##  System Capabilities and Constraints
---

### a. Capabilities
- Budget Review: Displays budget data for 2023, 2024, 2025, and 2026.
- Editing Functionality: Allows the user to select any leaf node (EnvEntry) and introduce a new monetary value (setAmount).
- Real-time Changes: Changes are immediately reflected in the in-memory data structure.
- Localization: Uses EnvBudgetTranslator to display all official names (Sectors, Units, Entries) in Greek.
- Web & CLI Interface: Dual-mode operation for flexibility.
- Balance Tracking: Maintains running balance during edit sessions to ensure fiscal equilibrium.
- State Machine Editing: Multi-step workflow guides users through budget modifications.

### b. Constraints and Restrictions
- Validation Rules:
  - No negative budget values
  - New values cannot exceed total ministry budget
  - Changes >30% trigger confirmation warnings
- Data Persistence: Changes are currently not saved back to the JSON file. Modifications are only valid for the current application session.
- Scope: Detailed editing is currently limited to the Ministry of Environment and Energy (ΥΠΕΝ). Other ministries show summary data only.
- Concurrent Access: ThreadLocal session management supports concurrent users but does not persist across server restarts.
  
---
## Installation and Running
---
### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Steps
- 1. Clone the repository
  - git clone https://github.com/yourusername/openbudget.git
  - cd openbudget
- 2. Build the project
mvn clean install
-  3. Run the application
mvn exec:java -Dexec.mainClass="gr.det.spinnovators.OpenBudgetApplication"
- 4. Open browser at:
http://localhost:8080/login.html

#### Login Credentials 
| Role | Username | Password |
|-----|---------|----------|
| Minister | Minister | `m1n1st3r` |
| Employee | Any | `3mpl0y33` |

---
### Usage
---
#### Web Interface
- Login: Enter credentials at http://localhost:8080/login.html
- View Budget: Select a year (2023-2026) to view state or ministry budget
- Edit Budget (Ministry of Environment and Energy / Minister only):
  - Navigate to "Change Budget" option
  - Select year
  - Choose Sector → Unit → Entry
  - Enter new value
  - System validates and tracks balance changes
  - Balance Management: System prevents termination until budget is balanced

#### Terminal Interface
- Login: Terminal prompts for credentials
- Year Selection: Enter year or 0000 to exit
- View Data: Budget data displayed in formatted tables
- Edit Budget (Ministry of Environment and Energy / Minister only):
  - Select year
  - Choose Sector → Unit → Entry
  - Enter new value
  - System validates and tracks balance changes
  - Balance Management: System prevents termination until budget is balanced

---
## Future Extensions
---
- Budget Forecasting: Predict 2027 budget using linear regression on 2024-2026 data
- Crisis Mode Simulation: Simulate emergency scenarios
  - Floods: 10% reduction
  - Pandemic: 15% reduction
  - Energy Crisis: 12% reduction
  - Automatic reallocation to crisis response ministries

---
## Planned Features
---
- Chart Visualization: Integration with Chart.js for pie and bar charts
  - Budget distribution per sector
  - Year-over-year comparisons
  - Real-time updates during editing

- Advanced Comparison: Side-by-side year comparisons with delta highlighting
- Advanced ESG Metrics: Integration with international sustainability frameworks
---
## Testing
---
### Run all tests
mvn test

### Run specific test class
mvn test -Dtest=EnvBudgetLoaderTest

### Run tests with coverage
mvn clean test jacoco:report

---
## License
---
This project is developed for educational purposes as part of the Java Programming course.
### Team - Spinnovators
Developed by the Spinnovators team for academic coursework.

## ⚡ OpenBudget - Be Prime Minister for a Day!
