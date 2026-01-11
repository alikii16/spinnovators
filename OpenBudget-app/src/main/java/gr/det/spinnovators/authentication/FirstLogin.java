package gr.det.spinnovators.authentication;

import java.util.Scanner;

/**
 * Provides authentication functionalities for the application's users.
 *
 * <p>This utility class validates user credentials (username and password)
 * and grants system access based on predefined roles, such as Minister
 * or Employee. It implements a simple credential-based authentication system
 * with role-based access control.</p>
 *
 * <p>This is a utility class and cannot be instantiated.</p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public final class FirstLogin {

  /**
   * Private constructor for utility class to prevent instantiation.
   *
   * @throws UnsupportedOperationException if instantiation is attempted
   */
  private FirstLogin() {
  }

  /**
   * Manages the user login process via the console interface.
   * It prompts the user for credentials in a loop until valid information
   * is provided. The method distinguishes between the "Minister" role
   * and standard "Employees" based on the provided password.
   *
   * <p>Valid credentials are:
   * <ul>
   *   <li>Minister: username="Minister", password="m1n1st3r"</li>
   *   <li>Employee: any username (except "Minister"), password="3mpl0y33"</li>
   * </ul>
   * </p>
   *
   * @param input the Scanner object for reading user input from console
   * @return a String representing the user role: "a" for Minister, "b" for Employee
   * @throws NullPointerException if input is null
   */
  public static String login(Scanner input) {
    final String minister = "Minister";
    final String passwordMinister = "m1n1st3r";
    final String passwordEmployee = "3mpl0y33";
    boolean isValid;
    String userRole = " ";

    do {
      System.out.print("Εισάγετε όνομα χρήστη: ");
      String username = input.nextLine();

      System.out.print("Εισάγετε κωδικό: ");
      String password = input.nextLine();

      if (username.equals(minister) && password.equals(passwordMinister)) {
        isValid = true;
        userRole = "a";
        System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ.");
      } else if (!username.equals(minister) && password.equals(passwordEmployee)) {
        isValid = true;
        userRole = "b";
        System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε " + username + ".");
      } else {
        isValid = false;
        System.out.println("Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.");
      }

    } while (!isValid);
    return userRole;
  }
}
