//Τranslator class responsible for converting English JSON keys into their official Greek descriptions. 
//It reads the 'env_budget_translations.properties' file and provides a translation service
package gr.det.spinnovators;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class EnvBudgetTranslator {
  
// The name of the properties file located in src/main/resources
  private static final String BUNDLE_NAME = "env_budget_translations";
// ResourceBundle holds the key-translations
  private final ResourceBundle categoryBundle;
  
  public EnvBudgetTranslator() {
        try {
            this.categoryBundle = ResourceBundle.getBundle(BUNDLE_NAME); 
        } catch (MissingResourceException e) {
            // Silent failure cause of frontend
            this.categoryBundle = null; 
        }
  }
  public String translateCategory(String jsonKey) {
        // Input Validation 
        if (jsonKey == null || jsonKey.trim().isEmpty()) {
            return "";
        }
        // Check if the translation bundle was loaded successfully
        if (categoryBundle != null) {
            try {
        // Attempt to find and return the translation
                return categoryBundle.getString(jsonKey);
            } catch (MissingResourceException e) {
            }
        }
        // For Safety:  Returns the key by replacing underscores with spaces.
        return jsonKey.replace('_', ' '); 
    }  
}
