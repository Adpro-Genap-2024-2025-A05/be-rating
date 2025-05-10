package id.ac.ui.cs.advprog.berating.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ConsultationHistoryDTOTest {
    
    @Test
    void testNoArgsConstructor() {
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getDoctorId());
        assertNull(dto.getDate());
        assertNull(dto.getNotes());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO(
            "123", "user123", "doctor123", now, "Regular checkup"
        );

        assertEquals("123", dto.getId());
        assertEquals("user123", dto.getUserId());
        assertEquals("doctor123", dto.getDoctorId());
        assertEquals(now, dto.getDate());
        assertEquals("Regular checkup", dto.getNotes());
    }

    @Test
    void testSettersAndGetters() {
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId("123");
        dto.setUserId("user123");
        dto.setDoctorId("doctor123");
        dto.setDate(now);
        dto.setNotes("Regular checkup");

        assertEquals("123", dto.getId());
        assertEquals("user123", dto.getUserId());
        assertEquals("doctor123", dto.getDoctorId());
        assertEquals(now, dto.getDate());
        assertEquals("Regular checkup", dto.getNotes());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ConsultationHistoryDTO dto1 = new ConsultationHistoryDTO(
            "123", "user123", "doctor123", now, "Regular checkup"
        );
        ConsultationHistoryDTO dto2 = new ConsultationHistoryDTO(
            "123", "user123", "doctor123", now, "Regular checkup"
        );
        ConsultationHistoryDTO dto3 = new ConsultationHistoryDTO(
            "456", "user456", "doctor456", now.plusDays(1), "Follow-up"
        );

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO(
            "123", "user123", "doctor123", now, "Regular checkup"
        );
        
        String toString = dto.toString();
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("doctor123"));
        assertTrue(toString.contains("Regular checkup"));
    }

    @Test
    void testDatePrecision() {
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO();
        LocalDateTime now = LocalDateTime.now();
        dto.setDate(now);
        assertEquals(now, dto.getDate());
    }

    @Test
    void testEmptyNotes() {
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO();
        dto.setNotes("");
        assertEquals("", dto.getNotes());
    }
}
