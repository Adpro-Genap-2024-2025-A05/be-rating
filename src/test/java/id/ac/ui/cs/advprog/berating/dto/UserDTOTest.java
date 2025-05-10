package id.ac.ui.cs.advprog.berating.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.berating.enums.Role;

public class UserDTOTest {
    
    @Test
    void testNoArgsConstructor() {
        UserDTO userDTO = new UserDTO();
        assertNotNull(userDTO);
        assertNull(userDTO.getId());
        assertNull(userDTO.getFullname());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getGender());
        assertNull(userDTO.getUsername());
        assertNull(userDTO.getPassword());
        assertNull(userDTO.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        UserDTO userDTO = new UserDTO(
            "123", "John Doe", "john@example.com", 
            "Male", "johndoe", "password123", Role.PACILLIANS
        );

        assertEquals("123", userDTO.getId());
        assertEquals("John Doe", userDTO.getFullname());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals("Male", userDTO.getGender());
        assertEquals("johndoe", userDTO.getUsername());
        assertEquals("password123", userDTO.getPassword());
        assertEquals(Role.PACILLIANS, userDTO.getRole());
    }

    @Test
    void testSettersAndGetters() {
        UserDTO userDTO = new UserDTO();
        
        userDTO.setId("123");
        userDTO.setFullname("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setGender("Male");
        userDTO.setUsername("johndoe");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.PACILLIANS);

        assertEquals("123", userDTO.getId());
        assertEquals("John Doe", userDTO.getFullname());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals("Male", userDTO.getGender());
        assertEquals("johndoe", userDTO.getUsername());
        assertEquals("password123", userDTO.getPassword());
        assertEquals(Role.PACILLIANS, userDTO.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDTO userDTO1 = new UserDTO("123", "John Doe", "john@example.com", 
            "Male", "johndoe", "password123", Role.PACILLIANS);
        UserDTO userDTO2 = new UserDTO("123", "John Doe", "john@example.com", 
            "Male", "johndoe", "password123", Role.PACILLIANS);
        UserDTO userDTO3 = new UserDTO("456", "Jane Doe", "jane@example.com", 
            "Female", "janedoe", "password456", Role.CAREGIVER);

        assertEquals(userDTO1, userDTO2);
        assertNotEquals(userDTO1, userDTO3);
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode());
        assertNotEquals(userDTO1.hashCode(), userDTO3.hashCode());
    }

    @Test
    void testToString() {
        UserDTO userDTO = new UserDTO("123", "John Doe", "john@example.com", 
            "Male", "johndoe", "password123", Role.PACILLIANS);
        
        String toString = userDTO.toString();
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("Male"));
        assertTrue(toString.contains("johndoe"));
        assertTrue(toString.contains("PACILLIANS"));
    }
}
