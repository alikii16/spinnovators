package gr.det.spinnovators;
//This file will be used for users login
import java.util.Scanner;

public final class FirstLogin {

    /**
     * Private constructor for utility class to prevent intantiation.
     */
    private FirstLogin() {
    }

    /**
     * It has to do with the login of the user,
     * repeating it until connection.
     */

    public static void login() {
        try (Scanner input = new Scanner(System.in)) {

            String minister = "Minister";
            String passwordMinister = "m1n1st3r";
            String passwordEmployee = "3mpl0y33";

            boolean isValid;

            do {
                String username;
                String password;

                System.out.print("Εισάγετε όνομα χρήστη: ");
                username = input.nextLine();

                System.out.print("Εισάγετε κωδικό: ");
                password = input.nextLine();

                if (username.equals(minister)
                        && password.equals(passwordMinister)) {
                    isValid = true;
                    System.out.println(
                        "Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ."
                    );
                } else if (!username.equals(minister)
                        && password.equals(passwordEmployee)) {
                    isValid = true;
                    System.out.println(
                        "Επιτυχής σύνδεση! Καλωσήρθατε " + username + "."
                    );
                } else {
                    isValid = false;
                    System.out.println(
                        "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά."
                    );
                }

            } while (!isValid);
        }
    }
}