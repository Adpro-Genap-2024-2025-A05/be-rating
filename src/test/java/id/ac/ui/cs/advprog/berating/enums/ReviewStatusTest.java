package id.ac.ui.cs.advprog.berating.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ReviewStatusTest {
    
    @Test
    void testEnumValues() {
        ReviewStatus[] statuses = ReviewStatus.values();
        assertEquals(3, statuses.length);
        
        assertEquals(ReviewStatus.PENDING, ReviewStatus.valueOf("PENDING"));
        assertEquals(ReviewStatus.APPROVED, ReviewStatus.valueOf("APPROVED"));
        assertEquals(ReviewStatus.REJECTED, ReviewStatus.valueOf("REJECTED"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, ReviewStatus.PENDING.ordinal());
        assertEquals(1, ReviewStatus.APPROVED.ordinal());
        assertEquals(2, ReviewStatus.REJECTED.ordinal());
    }

    @Test
    void testEnumEquality() {
        ReviewStatus status1 = ReviewStatus.PENDING;
        ReviewStatus status2 = ReviewStatus.PENDING;
        ReviewStatus status3 = ReviewStatus.APPROVED;

        assertTrue(status1 == status2);
        assertFalse(status1 == status3);
        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
    }

    @Test
    void testEnumToString() {
        assertEquals("PENDING", ReviewStatus.PENDING.toString());
        assertEquals("APPROVED", ReviewStatus.APPROVED.toString());
        assertEquals("REJECTED", ReviewStatus.REJECTED.toString());
    }

    @Test
    void testEnumValueOf() {
        assertEquals(ReviewStatus.PENDING, ReviewStatus.valueOf("PENDING"));
        assertEquals(ReviewStatus.APPROVED, ReviewStatus.valueOf("APPROVED"));
        assertEquals(ReviewStatus.REJECTED, ReviewStatus.valueOf("REJECTED"));
    }

    @Test
    void testInvalidEnumValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewStatus.valueOf("INVALID_STATUS");
        });
    }
}
