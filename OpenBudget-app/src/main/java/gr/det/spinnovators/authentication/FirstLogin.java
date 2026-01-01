package gr.det.spinnovators.authentication;

import java.util.Scanner;

// This file will be used for users login.

/**
 * Provides authentication functionalities for the application's users.
 * This utility class validates user credentials (username and password)
 * and grants system access based on predefined roles, such as Minister 
 * or Employee.
 */
public final class FirstLogin {

  /**
   * Private constructor for utility class to prevent instantiation.
   */
  private FirstLogin() {
  }

  /**
   * Manages the user login process via the console interface.
   * It prompts the user for credentials in a loop until valid information 
   * is provided. The method distinguishes between the "Minister" role 
   * and standard "Employees" based on the provided password.
   */
  public static void login() {
    @SuppressWarnings("resource")
    Scanner input = new Scanner(System.in);

    final String minister = "Minister";
    final String passwordMinister = "m1n1st3r";
    final String passwordEmployee = "3mpl0y33";
    boolean isValid;

    do {
      System.out.print("Εισάγετε όνομα χρήστη: ");
      String username = input.nextLine();
      
      System.out.print("Εισάγετε κωδικό: ");
      String password = input.nextLine();

      if (username.equals(minister) && password.equals(passwordMinister)) {
        isValid = true;
        System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ.");

      } else if (!username.equals(minister) && password.equals(passwordEmployee)) {
        isValid = true;
        System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε " + username + ".");
      } else {
        isValid = false;
        System.out.println("Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.");
      }

    } while (!isValid);
  }
}
