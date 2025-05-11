package id.ac.ui.cs.advprog.berating.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String doctorId = "123";
        DoctorNotFoundException exception = new DoctorNotFoundException(doctorId);
        
        assertEquals("Doctor not found with id: " + doctorId, exception.getMessage());
    }

    @Test
    void testExceptionWithNullId() {
        DoctorNotFoundException exception = new DoctorNotFoundException(null);
        
        assertEquals("Doctor not found with id: null", exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyId() {
        DoctorNotFoundException exception = new DoctorNotFoundException("");
        
        assertEquals("Doctor not found with id: ", exception.getMessage());
    }
} 