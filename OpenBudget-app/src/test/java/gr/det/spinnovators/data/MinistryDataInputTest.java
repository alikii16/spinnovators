package gr.det.spinnovators.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link MinistryDataInput} class.
 *
 * <p>This test class verifies the integrity and correctness of ministry
 * budget data across multiple fiscal years (2023-2026), ensuring that
 * all data arrays are properly populated and contain valid values.</p>
 *
 * <p>The test suite covers:
 * <ul>
 * <li>Data consistency for year 2026 (names, amounts, sizes)</li>
 * <li>Data consistency for year 2025 (names, amounts, sizes)</li>
 * <li>Data consistency for year 2024 (names, amounts, sizes)</li>
 * <li>Data consistency for year 2023 (names, amounts, sizes)</li>
 * <li>Validation of specific ministry entries at known positions</li>
 * <li>Verification that all arrays have the expected length</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class MinistryDataInputTest {

  private static final int MINISTRIES_COUNT_COMMON = 30;
  private static final int MINISTRIES_COUNT_2023 = 29;

  /**
   * Verifies the integrity of 2026 ministry data.
   */
  @Test
  public void testData2026() {
    MinistryDataInput data = new MinistryDataInput();

    assertEquals(MINISTRIES_COUNT_COMMON, data.getNames26().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getBudgetAmount26().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getSize26());

    assertNotNull(data.getNames26()[3]);
    assertTrue(data.getNames26()[3].contains("Υπουργείο Εσωτερικών"));
    assertTrue(data.getBudgetAmount26()[3] > 0);

    int last = data.getSize26() - 1;
    assertNotNull(data.getNames26()[last], "last 2026 name should be filled");
    assertTrue(data.getBudgetAmount26()[last] > 0, "last 2026 amount should be > 0");
  }

  /**
   * Verifies the integrity of 2025 ministry data.
   */
  @Test
  public void testData2025() {
    MinistryDataInput data = new MinistryDataInput();

    assertEquals(MINISTRIES_COUNT_COMMON, data.getNames25().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getBudgetAmount25().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getSize25());

    assertNotNull(data.getNames25()[0]);
    assertTrue(data.getNames25()[0].contains("Προεδρία της Δημοκρατίας"));
    assertTrue(data.getBudgetAmount25()[0] > 0);

    int last = data.getSize25() - 1;
    assertNotNull(data.getNames25()[last], "last 2025 name should be filled");
    assertTrue(data.getBudgetAmount25()[last] > 0, "last 2025 amount should be > 0");
  }

  /**
   * Verifies the integrity of 2024 ministry data.
   */
  @Test
  public void testData2024() {
    MinistryDataInput data = new MinistryDataInput();

    assertEquals(MINISTRIES_COUNT_COMMON, data.getNames24().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getBudgetAmount24().length);
    assertEquals(MINISTRIES_COUNT_COMMON, data.getSize24());

    assertNotNull(data.getNames24()[1]);
    assertTrue(data.getNames24()[1].contains("Βουλή των Ελλήνων"));
    assertTrue(data.getBudgetAmount24()[1] > 0);

    int last = data.getSize24() - 1;
    assertNotNull(data.getNames24()[last], "last 2024 name should be filled");
    assertTrue(data.getBudgetAmount24()[last] > 0, "last 2024 amount should be > 0");
  }

  /**
   * Verifies the integrity of 2023 ministry data.
   */
  @Test
  public void testData2023() {
    MinistryDataInput data = new MinistryDataInput();

    assertEquals(MINISTRIES_COUNT_2023, data.getNames23().length);
    assertEquals(MINISTRIES_COUNT_2023, data.getBudgetAmount23().length);
    assertEquals(MINISTRIES_COUNT_2023, data.getSize23());

    assertNotNull(data.getNames23()[2]);
    assertTrue(data.getNames23()[2].contains("Προεδρία της Κυβέρνησης"));
    assertTrue(data.getBudgetAmount23()[2] > 0);

    int lastfilled = data.getSize23() - 1;
    assertNotNull(data.getNames23()[lastfilled], "last filled 2023 name should be set");
    assertTrue(data.getBudgetAmount23()[lastfilled] > 0, "last filled 2023 amount should be > 0");
  }
}