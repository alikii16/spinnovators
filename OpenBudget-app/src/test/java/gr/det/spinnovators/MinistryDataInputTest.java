package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MinistryDataInputTest {

    private static final int MAX_MINISTRIES = 30;

    @Test
    public void testData2026() {
        MinistryDataInput data = new MinistryDataInput();

        assertEquals(MAX_MINISTRIES, data.getNames26().length);
        assertEquals(MAX_MINISTRIES, data.getBudgetAmount26().length);
        assertEquals(30, data.getSize26());

        assertNotNull(data.getNames26()[3]);
        assertTrue(data.getNames26()[3].contains("Υπουργείο Εσωτερικών"));
        assertTrue(data.getBudgetAmount26()[3] > 0);

        int last = data.getSize26() -1;
        assertNotNull(data.getNames26()[last], "last 2026 name should be filled");
        assertTrue(data.getBudgetAmount26()[last] > 0, "last 2026 amount should be > 0");
    }

    @Test
    public void testData2025() {
        MinistryDataInput data = new MinistryDataInput();

        assertEquals(MAX_MINISTRIES, data.getNames25().length);
        assertEquals(MAX_MINISTRIES,data.getBudgetAmount25().length);
        assertEquals(30, data.getSize25());

        assertNotNull(data.getNames25()[0]);
        assertTrue(data.getNames25()[0].contains("Προεδρία της Δημοκρατίας"));
        assertTrue(data.getBudgetAmount25()[0] > 0);

        int last = data.getSize25() - 1;
        assertNotNull(data.getNames25()[last], "last 2025 name should be filled");
        assertTrue(data.getBudgetAmount25()[last] > 0, "last 2025 amount should be > 0");
    }

    @Test
    public void testData2024() {
        MinistryDataInput data = new MinistryDataInput();

        assertEquals(MAX_MINISTRIES, data.getNames24().length);
        assertEquals(MAX_MINISTRIES,data.getBudgetAmount24().length);
        assertEquals(30,data.getSize24());

        assertNotNull(data.getNames24()[1]);
        assertTrue(data.getNames24()[1].contains("Βουλή των Ελλήνων"));
        assertTrue(data.getBudgetAmount24()[1] > 0);

        int last = data.getSize24() -1;
        assertNotNull(data.getNames24()[last], "last 2024 name should be filled");
        assertTrue(data.getBudgetAmount24()[last] > 0, "last 2024 amount should be > 0");
    }

    @Test
    public void testData2023() {
        MinistryDataInput data = new MinistryDataInput();

        assertEquals(MAX_MINISTRIES, data.getNames23().length);
        assertEquals(MAX_MINISTRIES,data.getBudgetAmount23().length);
        assertEquals(29,data.getSize23());

        assertNotNull(data.getNames23()[2]);
        assertTrue(data.getNames23()[2].contains("Προεδρία της Κυβέρνησης"));
        assertTrue(data.getBudgetAmount23()[2] > 0);

        int lastfilled = data.getSize23() - 1;
        assertNotNull(data.getNames23()[lastfilled], "last filled 2023 name should be set");
        assertTrue(data.getBudgetAmount23()[lastfilled] > 0, "last filled 2023 amount should be > 0");

        for (int i = data.getSize23(); i < MAX_MINISTRIES; i++) {
            assertNull(data.getNames23()[i], "names23[" + i + "] should be null beyong size23");
            assertEquals(0.0, data.getBudgetAmount23()[i], 0.0, "budgetAmount23[" + i + "] should be 0.0 beyond size23");
        }
    }
}
