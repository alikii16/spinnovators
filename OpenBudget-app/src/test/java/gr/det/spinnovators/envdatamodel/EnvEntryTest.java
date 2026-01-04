package gr.det.spinnovators.envdatamodel;

import gr.det.spinnovators.envdatamodel.EnvEntry;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for EnvEntry class.
 * These tests verify the getter, setter, and toString methods.
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
