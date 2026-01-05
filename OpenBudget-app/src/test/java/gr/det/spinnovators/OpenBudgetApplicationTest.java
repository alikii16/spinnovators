package gr.det.spinnovators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class OpenBudgetApplicationTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void testMinisterFlow() {
        // Minister -> Exit
        String input = "Minister\nm1n1st3r\n3\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        
        OpenBudgetApplication.main(new String[]{});
        
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        Assertions.assertTrue(output.contains("Καλωσήρθατε κύριε Υπουργέ"));
        Assertions.assertTrue(output.contains("Έξοδος..."));
    }

    @Test
    public void testEmployeeFlow() {
        // Employee -> Exit (Καλύπτει το role="b")
        String input = "JohnDoe\n3mpl0y33\n3\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        
        OpenBudgetApplication.main(new String[]{});
        
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        Assertions.assertTrue(output.contains("Καλωσήρθατε JohnDoe"));
        Assertions.assertTrue(output.contains("Έξοδος..."));
    }
}
