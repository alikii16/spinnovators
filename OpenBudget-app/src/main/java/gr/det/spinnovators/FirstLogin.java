package gr.det.spinnovators;

import java.util.Scanner;

public class FirstLogin {

    private static final String MINISTER_USERNAME = "Minister";
    private static final String MINISTER_PASSWORD = "m1n1st3r";
    private static final String EMPLOYEE_PASSWORD = "3mploy33";

    public enum LoginResult {
        MINISTER,
        EMPLOYEE,
        INVALID
    }

    public static LoginResult authenticate(String username, String password) {
        if (username == null || password == null) {
            return LoginResult.INVALID;
        }

        if (username.equals(MINISTER_USERNAME) && password.equals(MINISTER_PASSWORD)) {
            return LoginResult.MINISTER;
        }

        if (!username.equals(MINISTER_USERNAME) && password.equals(EMPLOYEE_PASSWORD)) {
            return LoginResult.EMPLOYEE;
        }

        return LoginResult.INVALID;
    }

    public static void login() {
        Scanner input = new Scanner(System.in);

        LoginResult result;

        do {
            System.out.print("Εισάγετε όνομα χρήστη: ");
            String username = input.nextLine();

            System.out.print("Εισάγετε κωδικό: ");
            String password = input.nextLine();

            result = authenticate(username, password);

            switch (result) {
                case MINISTER:
                    System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ.");
                    break;
                case EMPLOYEE:
                    System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε " + username + ".");
                    break;
                default:
                    System.out.println("Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.");
            }

        } while (result == LoginResult.INVALID);
    }
}
