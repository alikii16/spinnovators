package gr.det.spinnovators;

/**
 * This file will be used for users login.
 * */
import java.util.Scanner;

/**
 * Utility class to handle user login process. 
 * (Διορθώνει το MissingJavadocType στη γραμμή 5)
 */
public final class FirstLogin {

    /**
     * Private constructor for utility class to prevent instantiation.
     */ // ΔΙΟΡΘΩΣΗ: Σωστή εσεντάτωση

    private FirstLogin() {

    }

    /**
     * It has to do with the login of the user,
     * repeating it until connection.
     */
    public static void login() {
        @SuppressWarnings("resource")
        Scanner input = new Scanner(System.in);

        String minister = "Minister";
        String passwordMinister = "m1n1st3r";
        String passwordEmployee = "3mpl0y33";

        boolean isValid;


        do {
                // ΔΙΟΡΘΩΣΗ: Correct Indentation (8 spaces)
            String username;
                
                // ΔΙΟΡΘΩΣΗ: VariableDeclarationUsageDistance (password)
            String password; 
                
  