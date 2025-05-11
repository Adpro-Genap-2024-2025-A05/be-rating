package id.ac.ui.cs.advprog.berating.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsultationHistoryNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String consultationId = "123";
        ConsultationHistoryNotFoundException exception = new ConsultationHistoryNotFoundException(consultationId);
        
        assertEquals("Consultation history not found with id: " + consultationId, exception.getMessage());
    }

    @Test
    void testExceptionWithNullId() {
        ConsultationHistoryNotFoundException exception = new ConsultationHistoryNotFoundException(null);
        
        assertEquals("Consultation history not found with id: null", exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyId() {
        ConsultationHistoryNotFoundException exception = new ConsultationHistoryNotFoundException("");
        
        assertEquals("Consultation history not found with id: ", exception.getMessage());
    }
} 