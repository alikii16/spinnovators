package gr.det.spinnovators.service;

import gr.det.spinnovators.data.MinistryDataInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Unit tests for the {@link BudgetPercentageService} class.
 * <p>
 * Ensures correct calculation of budget percentages and handles edge cases
 * such as invalid years or zero-total budgets using Mockito.
 * </p>
 */
public class BudgetPercentageServiceTest {

  private BudgetPercentageService service;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    // Redirect System.out to capture console output
    System.setOut(new PrintStream(outputStreamCaptor, true, StandardCharsets.UTF_8.name()));
    
    // Default initialization (Standard Integration Test)
    service = new BudgetPercentageService();
  }

  // --- STANDARD SCENARIOS (Integration Tests with real data) ---

  @Test
  public void testDisplayEnvironmentPercentage_2023() throws UnsupportedEncodingException {
    service.displayEnvironmentPercentage(2023);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    
    Assertions.assertTrue(output.contains("Υπουργείο Περιβάλλοντος και Ενέργειας"), 
        "Should print ministry name for 2023");
    Assertions.assertTrue(output.contains("%"), "Should print percentage");
  }

  @Test
  public void testDisplayEnvironmentPercentage_2024() throws UnsupportedEncodingException {
    service.displayEnvironmentPercentage(2024);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    Assertions.assertTrue(output.contains("Υπουργείο Περιβάλλοντος"), "Should work for 2024");
  }

  @Test
  public void testDisplayEnvironmentPercentage_2025() throws UnsupportedEncodingException {
    service.displayEnvironmentPercentage(2025);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    Assertions.assertTrue(output.contains("Υπουργείο Περιβάλλοντος"), "Should work for 2025");
  }

  @Test
  public void testDisplayEnvironmentPercentage_2026() throws UnsupportedEncodingException {
    service.displayEnvironmentPercentage(2026);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    Assertions.assertTrue(output.contains("Υπουργείο Περιβάλλοντος"), "Should work for 2026");
  }

  @Test
  public void testDisplayEnvironmentPercentage_InvalidYear() throws UnsupportedEncodingException {
    service.displayEnvironmentPercentage(1990);
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    
    Assertions.assertTrue(output.contains("δεν υποστηρίζεται"), 
        "Should print error message for invalid year");
  }

  // --- EDGE CASE (Zero Total Budget - Requires Mocking) ---

  /**
   * Tests the scenario where the total budget sum is 0.
   * This covers the 'else' branch of 'if (totalSum > 0)'.
   */
  @Test
  public void testDisplayEnvironmentPercentage_ZeroTotal() throws UnsupportedEncodingException {
    // 1. Create a Mock of the data source
    MinistryDataInput mockData = Mockito.mock(MinistryDataInput.class);
    
    // 2. Configure it to return empty arrays (Sum = 0) for year 2026
    Mockito.when(mockData.getBudgetAmount26()).thenReturn(new double[0]);
    Mockito.when(mockData.getNames26()).thenReturn(new String[0]);

    // 3. Inject the mock into the service
    BudgetPercentageService mockedService = new BudgetPercentageService(mockData);

    // 4. Run method
    mockedService.displayEnvironmentPercentage(2026);

    // 5. Verify output
    String output = outputStreamCaptor.toString(StandardCharsets.UTF_8.name());
    
    // Should NOT print the percentage line because totalSum is 0
    Assertions.assertFalse(output.contains("αντιπροσωπεύει"), 
        "Should skip printing if total sum is 0");
  }
}
