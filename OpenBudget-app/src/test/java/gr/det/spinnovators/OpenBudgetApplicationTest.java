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

    // === Preserve original System streams ===
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    // === Capture output ===
    private final ByteArrayOutputStream outputStreamCaptor =
            new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(
                outputStreamCaptor, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    //Minister → Exit immediately
    @Test
    void ministerExitImmediately() {
        String input = "Minister\nm1n1st3r\n3\n";
        System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        Assertions.assertTrue(output.contains("Έξοδος"));
    }

    // Employee → Exit immediately
    @Test
    void employeeExitImmediately() {
        String input = "John\n3mpl0y33\n3\n";
        System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        Assertions.assertTrue(output.contains("Έξοδος"));
    }

    // Minister → General Budget → View
    @Test
    void ministerViewGeneralBudget() {
        String input =
                "Minister\nm1n1st3r\n" +
                "1\n" +
                "1\n" +
                "2024\n" +
                "2\n" +
                "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        Assertions.assertTrue(outputStreamCaptor.toString().contains("Κρατικός"));
    }

    // Employee → General Budget → View
    @Test
    void employeeViewGeneralBudget() {
        String input =
                "John\n3mpl0y33\n" +
                "1\n" +
                "1\n" +
                "2023\n" +
                "2\n" +
                "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        Assertions.assertTrue(outputStreamCaptor.toString().contains("Κρατικός"));
    }

    // Minister → Environment Ministry → View Year (subChoice "1")
    @Test
    void ministerViewEnvBudgetYear() {
        String input =
                "Minister\nm1n1st3r\n" + // login
                "2\n" + // mainChoice = Ministry
                "1\n" + // subChoice = View Year
                "2025\n" + // year input
                "5\n" + // back to main menu
                "3\n";   // exit
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        Assertions.assertTrue(output.contains("Υπουργείο"));
    }

    // Minister → ESG Report (subChoice "4") - coverage for else if
    @Test
    void ministerEsgReport() {
        String input =
                "Minister\nm1n1st3r\n" + // login
                "2\n" + // mainChoice = Ministry
                "4\n" + // subChoice = ESG Report
                "2025\n" + // year input
                "5\n" + // back to main menu
                "3\n";   // exit
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        // Ensure ESG report block executed
        Assertions.assertTrue(output.contains("ΑΝΑΦΟΡΑ ΒΙΩΣΙΜΟΤΗΤΑΣ ΕΤΟΥΣ"));
    }

    // Minister → Compare Years Invalid
    @Test
    void ministerCompareInvalidYears() {
        String input =
                "Minister\nm1n1st3r\n" +
                "2\n" +
                "3\n" +
                "1900\n" +
                "1800\n" +
                "5\n" +
                "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        Assertions.assertTrue(outputStreamCaptor.toString().contains("Σφάλμα"));
    }

    // Employee → Compare Years Valid
    @Test
    void employeeCompareYears() {
        String input =
                "John\n3mpl0y33\n" +
                "2\n" +
                "2\n" +
                "2024\n" +
                "2025\n" +
                "4\n" +
                "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        Assertions.assertTrue(outputStreamCaptor.toString().length() > 0);
    }

    // Invalid menu choice
    @Test
    void invalidMenuChoice() {
        String input =
                "Minister\nm1n1st3r\n" +
                "9\n" +
                "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        OpenBudgetApplication.main(new String[]{});

        Assertions.assertTrue(outputStreamCaptor.toString().contains("Μη έγκυρη"));
    }
}
