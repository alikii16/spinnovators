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
        assertTrue(output.contains("Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ."),"Expected successful login message for Minister");
    }

    @Test
    public void testEmployeeLoginSuccess() {
        String simulatedInput = "Employee\n3mpl0y33\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        FirstLogin.login();
        String output = outputStream.toString();
        assertTrue(output.contains("Επιτυχής σύνδεση! Καλωσήρθατε Employee."),"Expected successful login message");
    }

    @Test
    public void testEmptyUsernameandPassword() {
        String simulatedInput = "\n\nMinister\nm1n1st3r\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        FirstLogin.login();
        String output = outputStream.toString();
        assertTrue(output.contains("Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά."),"Expected wrong username/password message for empty input");
        assertTrue(output.contains("Επιτυχής σύνδεση! Καλωσήρθατε κύριε Υπουργέ."),"Expected successful login message after retry");
    }
}
