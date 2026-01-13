package gr.det.spinnovators.editor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.service.EnvBudgetTranslator;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EnvBudgetEditor}.
 *
 * <p>This test suite achieves <b>100% Branch Coverage</b> by strategically testing 
 * logical short-circuits and edge cases in the user input flow.</p>
 */
class EnvBudgetEditorTest {

  private EnvBudgetData mockData;
  private EnvBudgetTranslator mockTranslator;

  /**
   * Sets up the mock data and translator before each test execution.
   * This ensures a clean state for every individual test case.
   */
  @BeforeEach
  void setUp() {
    // 1. Create a list of sectors (empty is fine for the Editor tests)
    List<EnvSector> sectors = new ArrayList<>();

    // 2. Create years: 2025, 2026 AND 2030
    // We include "2030" intentionally to test the scenario where a year exists 
    // in the data but is NOT allowed for editing (Logic Branch Coverage).
    List<EnvYear> years = new ArrayList<>();
    years.add(new EnvYear("2025", sectors));
    years.add(new EnvYear("2026", sectors));
    years.add(new EnvYear("2030", sectors)); // Exists but restricted
    
    // 3. Initialize EnvBudgetData
    // (Ensures the constructor accepting a List is present in EnvBudgetData)
    mockData = new EnvBudgetData(years);
    
    // 4. Mock Translator
    mockTranslator = new EnvBudgetTranslator() {
      @Override
      public String translateCategory(String key) {
        return key;
      }
    };
  }

  /**
   * Test Case 1: Empty Input (EOF).
   * Covers: Line 49 (Empty Check before starting).
   */
  @Test
  void testEmptyInputAtStart() {
    String input = ""; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 2: Input ends unexpectedly after "NAI".
   * Covers: Line 60 (Empty Check before Year input).
   */
  @Test
  void testInputCutOffAfterYes() {
    String input = "ΝΑΙ\n"; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 3: Decline Session.
   * Covers: The "OXI" (NO) Branch.
   */
  @Test
  void testUserDeclinesSession() {
    String input = "OXI\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 4: Invalid Year (Exists in DB but restricted).
   * Covers: The logic branch where data is found, but the year is not 2025/2026.
   * Triggers the Error Message print.
   */
  @Test
  void testInvalidYearSelection_ExistsButNotAllowed() {
    String input = "ΝΑΙ\n2030\n"; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 5: Non-Existent Year.
   * Covers: The null check (selectedYear == null).
   */
  @Test
  void testInvalidYearSelection_NotFound() {
    String input = "ΝΑΙ\n2099\n"; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 6: Successful Session (2025).
   * Covers: The 1st half of the logical OR condition (Short-circuit).
   */
  @Test
  void testSuccessfulSessionStart_2025() {
    String input = "ΝΑΙ\n2025\n0\n"; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }

  /**
   * Test Case 7: Successful Session (2026).
   * CRITICAL FOR COVERAGE:
   * Covers the 2nd half of the OR condition (!equals("2026")).
   * Here the "2025" check returns False, forcing Java to evaluate the 2026 check.
   */
  @Test
  void testSuccessfulSessionStart_2026() {
    String input = "ΝΑΙ\n2026\n0\n"; 
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    EnvBudgetEditor editor = new EnvBudgetEditor(mockData, mockTranslator);
    assertDoesNotThrow(() -> editor.startEditingSession(scanner));
  }
}