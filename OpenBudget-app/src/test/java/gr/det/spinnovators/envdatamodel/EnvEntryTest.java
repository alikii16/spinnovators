package gr.det.spinnovators.envdatamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link EnvEntry} class.
 *
 * <p>This test class verifies the basic functionality of budget entries,
 * including getter and setter methods and the string representation format.</p>
 *
 * <p>The test suite covers:
 * <ul>
 *   <li>Getter methods (getJsonKey, getAmount)</li>
 *   <li>Setter method (setAmount) for updating budget values</li>
 *   <li>String representation (toString) formatting</li>
 * </ul>
 * </p>
 *
 * @author Spinnovators Team
 * @version 1.0
 */
public class EnvEntryTest {

    @Test
    public void testGetters() {
        EnvEntry entry = new EnvEntry("personnel_costs", 2256000.0);

        assertEquals("personnel_costs", entry.getJsonKey(),
            "getJsonKey should return the correct JSON key");

        assertEquals(2256000.0, entry.getAmount(),
            "getAmount should return the correct amount");
    }

    @Test
    public void testSetAmount() {
        EnvEntry entry = new EnvEntry("personnel_costs", 2256000.0);

        entry.setAmount(3000000.0);

        assertEquals(3000000.0, entry.getAmount(),
            "setAmount should update the amount correctly");
    }

    @Test
    public void testToString() {
        EnvEntry entry = new EnvEntry("personnel_costs", 2256000.0);

        String expected = "{personnel_costs: 2256000.0}";

        assertEquals(expected, entry.toString(),
            "toString should return the expected string format");
    }
}
