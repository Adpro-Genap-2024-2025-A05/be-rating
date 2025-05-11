package id.ac.ui.cs.advprog.berating.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String userId = "123";
        UserNotFoundException exception = new UserNotFoundException(userId);
        
        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    @Test
    void testExceptionWithNullId() {
        UserNotFoundException exception = new UserNotFoundException(null);
        
        assertEquals("User not found with id: null", exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyId() {
        UserNotFoundException exception = new UserNotFoundException("");
        
        assertEquals("User not found with id: ", exception.getMessage());
    }
} 