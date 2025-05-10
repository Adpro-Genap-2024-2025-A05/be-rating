package id.ac.ui.cs.advprog.berating.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RoleTest {
    
    @Test
    void testEnumValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        
        assertEquals(Role.PACILLIANS, Role.valueOf("PACILLIANS"));
        assertEquals(Role.CAREGIVER, Role.valueOf("CAREGIVER"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, Role.PACILLIANS.ordinal());
        assertEquals(1, Role.CAREGIVER.ordinal());
    }

    @Test
    void testEnumEquality() {
        Role role1 = Role.PACILLIANS;
        Role role2 = Role.PACILLIANS;
        Role role3 = Role.CAREGIVER;

        assertTrue(role1 == role2);
        assertFalse(role1 == role3);
        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
    }

    @Test
    void testEnumToString() {
        assertEquals("PACILLIANS", Role.PACILLIANS.toString());
        assertEquals("CAREGIVER", Role.CAREGIVER.toString());
    }

    @Test
    void testEnumValueOf() {
        assertEquals(Role.PACILLIANS, Role.valueOf("PACILLIANS"));
        assertEquals(Role.CAREGIVER, Role.valueOf("CAREGIVER"));
    }

    @Test
    void testInvalidEnumValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("INVALID_ROLE");
        });
    }
}
