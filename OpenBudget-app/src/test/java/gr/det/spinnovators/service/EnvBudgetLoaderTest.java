package gr.det.spinnovators.service;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetLoaderTest {

    @Test
    public void testLoadBudget_fileNotFound_returnsEmptyModel() {
        ClassLoader cl = getClass().getClassLoader();
        if (cl.getResourceAsStream("env_budget_data.json") != null) {
            System.out.println("Skipping test because JSON exists");
            return;
        }

        EnvBudgetLoader loader = new EnvBudgetLoader();
        EnvBudgetData data = loader.loadBudget();

        assertNotNull(data);
        assertNotNull(data.getEnvMinistryTotalBudget());
        assertEquals(0, data.getEnvMinistryTotalBudget().size());
        assertNull(data.getBudgetForYear("2020"));
    }
}
