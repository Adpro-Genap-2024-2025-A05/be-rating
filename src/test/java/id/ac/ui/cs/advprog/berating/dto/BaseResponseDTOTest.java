package id.ac.ui.cs.advprog.berating.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseResponseDTOTest {

    // POSITIVE CASES

    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        int status = 200;
        String message = "Success";
        Date timestamp = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");

        // Act
        BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>(status, message, timestamp, data);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(data, response.getData());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        BaseResponseDTO<String> response = new BaseResponseDTO<>();

        // Assert
        assertEquals(0, response.getStatus());
        assertNull(response.getMessage());
        assertNull(response.getTimestamp());
        assertNull(response.getData());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        int status = 404;
        String message = "Not Found";
        Date timestamp = new Date();
        String data = "test data";

        // Act
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(timestamp);
        response.setData(data);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(data, response.getData());
    }

    // NEGATIVE CASES

    @Test
    void testWithNegativeStatus() {
        // Arrange
        int negativeStatus = -1;
        BaseResponseDTO<String> response = new BaseResponseDTO<>();

        // Act
        response.setStatus(negativeStatus);

        // Assert
        assertEquals(negativeStatus, response.getStatus());
    }

    @Test
    void testWithEmptyMessage() {
        // Arrange
        String emptyMessage = "";
        BaseResponseDTO<String> response = new BaseResponseDTO<>();

        // Act
        response.setMessage(emptyMessage);

        // Assert
        assertEquals(emptyMessage, response.getMessage());
    }

    // CORNER CASES

    @Test
    void testWithNullData() {
        // Arrange
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        String nullData = null;

        // Act
        response.setData(nullData);

        // Assert
        assertNull(response.getData());
    }

    @Test
    void testWithNullMessage() {
        // Arrange
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        String nullMessage = null;

        // Act
        response.setMessage(nullMessage);

        // Assert
        assertNull(response.getMessage());
    }

    @Test
    void testWithNullTimestamp() {
        // Arrange
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        Date nullTimestamp = null;

        // Act
        response.setTimestamp(nullTimestamp);

        // Assert
        assertNull(response.getTimestamp());
    }

    @Test
    void testWithComplexGenericType() {
        // Arrange
        Map<String, Map<String, Object>> complexData = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("nestedKey", "nestedValue");
        complexData.put("outerKey", innerMap);

        // Act
        BaseResponseDTO<Map<String, Map<String, Object>>> response = new BaseResponseDTO<>();
        response.setData(complexData);

        // Assert
        assertEquals(complexData, response.getData());
        assertEquals(innerMap, response.getData().get("outerKey"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Date timestamp = new Date();
        BaseResponseDTO<String> response1 = new BaseResponseDTO<>(200, "Success", timestamp, "data");
        BaseResponseDTO<String> response2 = new BaseResponseDTO<>(200, "Success", timestamp, "data");
        BaseResponseDTO<String> response3 = new BaseResponseDTO<>(404, "Not Found", timestamp, "data");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        BaseResponseDTO<String> response = new BaseResponseDTO<>(200, "Success", new Date(), "data");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("status=200"));
        assertTrue(toString.contains("message=Success"));
        assertTrue(toString.contains("data=data"));
    }
}
