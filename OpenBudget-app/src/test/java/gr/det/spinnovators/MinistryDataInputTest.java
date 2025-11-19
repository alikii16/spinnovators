package gr.det.spinnovators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MinistryDataInputTest {

    @Test
    public void testData2025() {
        MinistryDataInput data = new MinistryDataInput();
        assertEquals(30, data.getSize25());
        assertTrue(data.getNames25()[0].contains("Προεδρία της Δημοκρατίας"));
        assertTrue(data.getBudgetAmount25()[0] > 0);
    }

    @Test
    public void testData2024() {
        MinistryDataInput data = new MinistryDataInput();
        assertEquals(30, data.getSize24());
        assertTrue(data.getNames24()[1].contains("Βουλή των Ελλήνων"));
        assertTrue(data.getBudgetAmount24()[1] > 0);
    }

    @Test
    public void testData2023() {
        MinistryDataInput data = new MinistryDataInput();
        assertEquals(29, data.getSize23());
        assertTrue(data.getNames23()[2].contains("Προεδρία της Κυβέρνησης"));
        assertTrue(data.getBudgetAmount23()[2] > 0);
    }
}
