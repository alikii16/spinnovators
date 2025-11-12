package com.example.budgetapp.service;

import com.example.budgetapp.model.MinistryBudget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class BudgetReader {

    private final String filePath;

    public BudgetReader(String filePath) {
        this.filePath = filePath;
    }

     /**
     * Reads the JSON file using Gson and maps the data to a Java object.
     * @return A MinistryBudget object containing the budget data.
     * @throws IOException If an error occurs while reading the file.
     */
    public MinistryBudget readBudget() throws IOException, JsonSyntaxException {
         // Gson is the main class used to work with JSON in Java
        Gson gson = new GsonBuilder().create();
        
        File jsonFile = new File(filePath);
        
        if (!jsonFile.exists()) {
             throw new IOException("Το αρχείο JSON δεν βρέθηκε στο μονοπάτι: " + jsonFile.getAbsolutePath());
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            // Gson reads from the FileReader and directly converts 
            // the JSON data into a MinistryBudget object.
            MinistryBudget budget = gson.fromJson(reader, MinistryBudget.class);
            System.out.println("Επιτυχής ανάγνωση του JSON με τη Gson.");
            return budget;
        } catch (JsonSyntaxException e) {
            System.err.println("Σφάλμα σύνταξης JSON κατά τη χαρτογράφηση: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("Σφάλμα κατά την ανάγνωση του αρχείου: " + e.getMessage());
            throw e;
        }
    }
}
