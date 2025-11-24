package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvBudgetLoaderTest {

    @Test
    public void testLoadBudget_fileNotFound_returnsEmptyModel() {
        EnvBudgetLoader loader = new EnvBudgetLoader();
        EnvBudgetData data = loader.loadBudget();

        // The returned object should never be null
        assertNotNull(data);

         // Total budget map should be initialized
         assertNotNull(data.getEnvMinistryTotalBudget());

         // Since JSON is missing, total budget map should be empty
        assertEquals(0, data.getEnvMinistryTotalBudget().size());

        // There should be no year data
        assertNull(data.getBudgetForYear("2020")); // or any year
    }
}
