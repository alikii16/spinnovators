package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstLoginTest {
    @Test
    public void testMiisterLoginSuccess() {
        String simulatedInput = "Minister\nm1n1st3r\n";
         System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
           System.setOut(new PrintStream(outputStream));
            FirstLogin.login();
             String output = outputStream.toString();
              assertTrue(output.contains("Επιτυχής σύνδεση! Καλωσήρθατε."),"Expected suvvessful login message");              
    }

    @Test
    public void testEmployeeLoginSuccess() {
        String simulatedInput = "Employee\n3mpl0y33\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        FirstLogin.login();
        String output = outputStream.toString();
        assertTrue(output.contains("Επιτυχής σύνδεση! Καλωσήρθατε."),"Expected successful login message");
    }

    @Test
    public void testWrongPassword() {
        String simulatedInput = "Minister\nwrongpassword\nm1n1st3r\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        FirstLogin.login();
        String output = outputStream.toString();
        assertTrue(output.contains("Λάθος κωδικός. Προσπαθήστε ξανά."),"Expected wrong password message");
    }
}
