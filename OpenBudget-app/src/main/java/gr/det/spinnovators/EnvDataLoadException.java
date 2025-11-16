package gr.det.spinnovators.service;

/**
 * Custom exception for errors during the loading or parsing of budget data.
 */
    public class EnvDataLoadException extends Exception {
    
    // Constructor with message and cause (for wrapping other exceptions like IOException/JsonSyntaxException)

    public EnvDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // Constructor with message only (e.g., "File not found")

    public EnvDataLoadException(String message) {
        super(message);
    }
}
