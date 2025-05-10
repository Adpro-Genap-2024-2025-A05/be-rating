package id.ac.ui.cs.advprog.berating.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ErrorResponseTest {
    
    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> details = new HashMap<>();
        details.put("field", "error message");

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .details(details)
                .path("/api/test")
                .build();

        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid input", response.getMessage());
        assertEquals(details, response.getDetails());
        assertEquals("/api/test", response.getPath());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response1 = ErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .build();

        ErrorResponse response2 = ErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .build();

        ErrorResponse response3 = ErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("Resource not found")
                .path("/api/other")
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("400"));
        assertTrue(toString.contains("Bad Request"));
        assertTrue(toString.contains("Invalid input"));
        assertTrue(toString.contains("/api/test"));
    }

    @Test
    void testWithNullDetails() {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .build();

        assertNull(response.getDetails());
    }

    @Test
    void testWithEmptyDetails() {
        Map<String, String> emptyDetails = new HashMap<>();
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .details(emptyDetails)
                .path("/api/test")
                .build();

        assertNotNull(response.getDetails());
        assertTrue(((Map<?, ?>) response.getDetails()).isEmpty());
    }
}
