package id.ac.ui.cs.advprog.berating.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Spy
    @InjectMocks
    private JwtService jwtService;

    private String secretKey;
    private long jwtExpirationTime;
    private Key signingKey;
    private UserDetails userDetails;
    private String validToken;
    private String expiredToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        // Use a fixed secret key for testing
        secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        jwtExpirationTime = 3600000; // 1 hour
        userDetails = new User("testuser", "password", java.util.Collections.emptyList());

        // Set the fields in the actual service
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationTime", jwtExpirationTime);

        // Create signing key
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        // Create valid token
        Map<String, Object> claims = new HashMap<>();
        validToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        
        // Create expired token
        expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - jwtExpirationTime))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        // Create invalid token (properly formatted but with invalid signature)
        invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJpbnZhbGlkdXNlciIsImlhdCI6MTUxNjIzOTAyMn0.invalid_signature_part";
    }

    // POSITIVE CASES

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        String username = jwtService.extractUsername(validToken);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_WithValidTokenAndMatchingUser_ShouldReturnTrue() {
        boolean isValid = jwtService.isTokenValid(validToken, userDetails);
        assertTrue(isValid);
    }

    @Test
    void getExpirationTime_ShouldReturnConfiguredTime() {
        long expirationTime = jwtService.getExpirationTime();
        assertEquals(jwtExpirationTime, expirationTime);
    }

    @Test
    void getRemainingTime_WithValidToken_ShouldReturnPositiveTime() {
        long remainingTime = jwtService.getRemainingTime(validToken);
        assertTrue(remainingTime > 0);
    }

    // NEGATIVE CASES

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithNonMatchingUser_ShouldReturnFalse() {
        UserDetails differentUser = new User("differentuser", "password", java.util.Collections.emptyList());
        boolean isValid = jwtService.isTokenValid(validToken, differentUser);
        assertFalse(isValid);
    }

    @Test
    void getRemainingTime_WithExpiredToken_ShouldReturnZero() {
        long remainingTime = jwtService.getRemainingTime(expiredToken);
        assertEquals(0, remainingTime);
    }

    // CORNER CASES

    @Test
    void extractUsername_WithInvalidToken_ShouldHandleException() {
        try {
            jwtService.extractUsername(invalidToken);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Expected exception
            assertTrue(true);
        }
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithNullToken_ShouldReturnFalse() {
        boolean isValid = jwtService.isTokenValid(null, userDetails);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithNullUserDetails_ShouldHandleException() {
        try {
            jwtService.isTokenValid(validToken, null);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Expected exception
            assertTrue(true);
        }
    }

    @Test
    void getRemainingTime_WithInvalidToken_ShouldReturnZero() {
        long remainingTime = jwtService.getRemainingTime(invalidToken);
        assertEquals(0, remainingTime);
    }

    @Test
    void getRemainingTime_WithNullToken_ShouldReturnZero() {
        long remainingTime = jwtService.getRemainingTime(null);
        assertEquals(0, remainingTime);
    }
}