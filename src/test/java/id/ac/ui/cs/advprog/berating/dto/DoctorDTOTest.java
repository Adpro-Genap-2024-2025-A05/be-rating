package id.ac.ui.cs.advprog.berating.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DoctorDTOTest {
    
    @Test
    void testNoArgsConstructor() {
        DoctorDTO doctorDTO = new DoctorDTO();
        assertNotNull(doctorDTO);
        assertNull(doctorDTO.getId());
        assertNull(doctorDTO.getName());
        assertNull(doctorDTO.getPracticeAddress());
        assertNull(doctorDTO.getWorkSchedule());
        assertNull(doctorDTO.getEmail());
        assertNull(doctorDTO.getPhoneNumber());
        assertEquals(0.0, doctorDTO.getRating());
    }

    @Test
    void testAllArgsConstructor() {
        DoctorDTO doctorDTO = new DoctorDTO(
            "123", "Dr. John Doe", "123 Medical St", 
            "Mon-Fri 9-5", "john@example.com", "1234567890", 4.5
        );

        assertEquals("123", doctorDTO.getId());
        assertEquals("Dr. John Doe", doctorDTO.getName());
        assertEquals("123 Medical St", doctorDTO.getPracticeAddress());
        assertEquals("Mon-Fri 9-5", doctorDTO.getWorkSchedule());
        assertEquals("john@example.com", doctorDTO.getEmail());
        assertEquals("1234567890", doctorDTO.getPhoneNumber());
        assertEquals(4.5, doctorDTO.getRating());
    }

    @Test
    void testSettersAndGetters() {
        DoctorDTO doctorDTO = new DoctorDTO();
        
        doctorDTO.setId("123");
        doctorDTO.setName("Dr. John Doe");
        doctorDTO.setPracticeAddress("123 Medical St");
        doctorDTO.setWorkSchedule("Mon-Fri 9-5");
        doctorDTO.setEmail("john@example.com");
        doctorDTO.setPhoneNumber("1234567890");
        doctorDTO.setRating(4.5);

        assertEquals("123", doctorDTO.getId());
        assertEquals("Dr. John Doe", doctorDTO.getName());
        assertEquals("123 Medical St", doctorDTO.getPracticeAddress());
        assertEquals("Mon-Fri 9-5", doctorDTO.getWorkSchedule());
        assertEquals("john@example.com", doctorDTO.getEmail());
        assertEquals("1234567890", doctorDTO.getPhoneNumber());
        assertEquals(4.5, doctorDTO.getRating());
    }

    @Test
    void testEqualsAndHashCode() {
        DoctorDTO doctorDTO1 = new DoctorDTO("123", "Dr. John Doe", "123 Medical St", 
            "Mon-Fri 9-5", "john@example.com", "1234567890", 4.5);
        DoctorDTO doctorDTO2 = new DoctorDTO("123", "Dr. John Doe", "123 Medical St", 
            "Mon-Fri 9-5", "john@example.com", "1234567890", 4.5);
        DoctorDTO doctorDTO3 = new DoctorDTO("456", "Dr. Jane Doe", "456 Medical St", 
            "Mon-Sat 8-6", "jane@example.com", "0987654321", 4.8);

        assertEquals(doctorDTO1, doctorDTO2);
        assertNotEquals(doctorDTO1, doctorDTO3);
        assertEquals(doctorDTO1.hashCode(), doctorDTO2.hashCode());
        assertNotEquals(doctorDTO1.hashCode(), doctorDTO3.hashCode());
    }

    @Test
    void testToString() {
        DoctorDTO doctorDTO = new DoctorDTO("123", "Dr. John Doe", "123 Medical St", 
            "Mon-Fri 9-5", "john@example.com", "1234567890", 4.5);
        
        String toString = doctorDTO.toString();
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("Dr. John Doe"));
        assertTrue(toString.contains("123 Medical St"));
        assertTrue(toString.contains("Mon-Fri 9-5"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("1234567890"));
        assertTrue(toString.contains("4.5"));
    }

    @Test
    void testRatingPrecision() {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setRating(4.56789);
        assertEquals(4.56789, doctorDTO.getRating());
    }
}
