package gr.det.spinnovators;

import gr.det.spinnovators.envdatamodel.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;

class EnvBudgetEditorTest {

    @Mock
    private EnvBudgetData data;

    @Mock
    private EnvBudgetTranslator translator;

    @Mock
    private EnvYear envYear2025;

    private EnvBudgetEditor editor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        editor = new EnvBudgetEditor(data, translator);
    }

    @Test
    void testUserChoosesNoEditing() {
        // User input: ΟΧΙ
        String input = "ΟΧΙ\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        editor.startEditingSession();

        // Δεν πρέπει να γίνει καμία αλληλεπίδραση με τα δεδομένα
        verifyNoInteractions(data);
    }

    @Test
    void testInvalidYearSelected() {
        // User input:
        // ΝΑΙ → 2024
        String input = "ΝΑΙ\n2024\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(data.getBudgetForYear("2024")).thenReturn(null);

        editor.startEditingSession();

        verify(data).getBudgetForYear("2024");
        verifyNoMoreInteractions(data);
    }

    @Test
    void testValidYearEditing2025() {
        // User input:
        // ΝΑΙ → 2025
        String input = "ΝΑΙ\n2025\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(data.getBudgetForYear("2025")).thenReturn(envYear2025);

        editor.startEditingSession();

        verify(data).getBudgetForYear("2025");
    }
}
