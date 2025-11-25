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
                
            System.out.print("Εισάγετε όνομα χρήστη: ");
            username = input.nextLine();
            System.out.print("Εισάγετε κωδικό: ");
            password = input.nextLine();
 if (username.equals(minister)
                    && password.equals(passwordMinister)) { // ΔΙΟΡΘΩΣΗ: Correct Indentation (8 spaces)
                isValid = true;
                System.out.println(
                    "Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ."
                ); // ΔΙΟΡΘΩΣΗ: Correct Indentation
            } else if (!username.equals(minister)
                && password.equals(passwordEmployee)) { // ΔΙΟΡΘΩΣΗ: Correct Indentation
                isValid = true;
                System.out.println(
                    "Επιτυχής σύνδεση! Καλωσήρθατε " + username + "."
                ); // ΔΙΟΡΘΩΣΗ: Correct Indentation
            } else {
                isValid = false;
                System.out.println(
                    "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά."
                ); // ΔΙΟΡΘΩΣΗ: Correct Indentation
            }
        } while (!isValid); 
    }
}


           