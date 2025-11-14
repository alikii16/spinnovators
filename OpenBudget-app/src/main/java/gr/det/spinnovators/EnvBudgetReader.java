/**
 * BudgetReader is a utility class responsible for loading and managing JSON-based budget data.
 *
 * In detail, this class uses the Gson library to read a JSON file from a specified
 * filesystem path and convert it into a nested Map<String,Object> 
 *
 * The main responsibilities of this class are:
 *
 * Reading a JSON file safely
 * Handling common file and JSON parsing errors
 * Storing the parsed data in memory for later use
 * Providing methods to access or print the loaded data
 */

package com.example.budgetapp.service;

/* These commands bring into the class the tools from the Gson library that we need in order to read and convert JSON files into Java data structures */
import com.google.gson.Gson; /* The “translator” between JSON and Java */
import com.google.gson.GsonBuilder; /* Helps us configure a Gson object */
import com.google.gson.JsonIOException; /* Special error types thrown */
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException; /* Reads the JSON file */
import java.io.FileReader; /* Exception thrown if the file path does not exist */
import java.util.Map; /* We use a Map<String, Object> structure to store the JSON after we read it */

public class EnvBudgetReader { 

	private final String filePath; /* A field that stores the path of the JSON file */ 
	private Map<String,Object> BudgetData; /* A field that stores the loaded JSON data. */

	public BudgetReader(String filePath) { 
		this.filePath = filePath; 
	}

      /* Reads the JSON file and loads it into memory */
        
	public void load() {
        
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try (FileReader reader = new FileReader(filePath)) {
                // Parse JSON into Map
                this.budgetData = gson.fromJson(reader, Map.class);
                
		System.out.println("JSON budget file loaded successfully!");
		}
                
		catch (FileNotFoundException e) {
                
			System.err.println("Error! JSON file: " + filePath + "not found");
                }
                catch (JsonSyntaxException e) {
                        
			System.err.println("Error! Invalid JSON syntax in the file.");
                }
                catch (JsonIOException e) {
                       
			System.err.println("Error! Cannot read the JSON file.");
                }
                catch (Exception e) {
            
			System.err.println("Unexpected error: " + e.getMessage());
                } //Safety net

		
		/* Returns the entire JSON structure as a Map */
                public Map<String, Object> getBudgetData() {
			return this.budgetData;
		}
               
		/* Utility: prints the JSON as formatted text */
		
		public void printBudget() {
			if (budgetData == null) {
				System.out.println("No data loaded yet.");
				return;
			}

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			System.out.println(gson.toJson(budgetData));
		}
	}

