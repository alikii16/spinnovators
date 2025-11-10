package gr.det.spinnovators;
//This file will be used for users login
import java.util.Scanner;

public class FirstLogin {
    public static void login() {
        Scanner input = new Scanner(System.in);

        String minister = "Minister";
        String passwordMinister = "m1n1st3r";
        String passwordEmployee = "3mpl0y33";


        String username;

        
        System.out.print("Εισάγετε όνομα χρήστη: ");
        username = input.nextLine();
            
        if (username.equals(minister)) {
            String password;
            boolean isValid = false;
        
            do {
                System.out.print("Εισάγετε κωδικό: ");
                password = input.nextLine();
            
                if (password.equals(passwordMinister)) {
                    isValid = true;
                } else {
                    isValid = false;
                    System.out.println("Λάθος κωδικός. Προσπαθήστε ξανά.");
                }
            } while (!isValid);

        } else {
            String password;
            boolean isValid = false;

            do {
                System.out.println("Εισάγετε κωδικό: ");
                password = input.nextLine();

                if (password.equals(passwordEmployee)) {
                    isValid = true;
                } else {
                    isValid = false;
                    System.out.println("Λάθος κωδικός. Προσπαθήστε ξανά.");
                }
            } while (!isValid); 

        }

    System.out.println("Επιτυχής σύνδεση! Καλωσήρθατε.");
    input.close();

    }

}
