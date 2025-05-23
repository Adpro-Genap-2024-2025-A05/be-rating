package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.service.TokenVerificationService;
import id.ac.ui.cs.advprog.berating.dto.TokenVerificationResponseDto;
import id.ac.ui.cs.advprog.berating.enums.Role;
import id.ac.ui.cs.advprog.berating.exception.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenVerificationServiceTest {

    @InjectMocks
    private TokenVerificationService tokenVerificationService;

    private String secretKey;
    private Key signingKey;
    private String validToken;
    private String expiredToken;
    private UUID userId;
    private String email;
    private String userName;

    @BeforeEach
    void setUp() {
        // Use the same secret key as in application-test.properties
        secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        ReflectionTestUtils.setField(tokenVerificationService, "secretKey", secretKey);

        userId = UUID.randomUUID();
        email = "test@example.com";
        userName = "Test User";

        // Create a valid token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId.toString());
        claims.put("role", Role.PACILIAN.name());
        claims.put("name", userName);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // 1 hour from now

        validToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        // Create an expired token
        Date pastDate = new Date(now.getTime() - 3600000); // 1 hour ago
        expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(pastDate)
                .setExpiration(pastDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testVerifyToken_Success() {
        TokenVerificationResponseDto response = tokenVerificationService.verifyToken(validToken);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals(email, response.getEmail());
        assertEquals(userName, response.getUserName());
        assertEquals(Role.PACILIAN, response.getRole());
        assertTrue(response.getExpiresIn() > 0);
    }

    @Test
    void testVerifyToken_Expired() {
        assertThrows(AuthenticationException.class, () -> 
            tokenVerificationService.verifyToken(expiredToken));
    }

    @Test
    void testVerifyToken_InvalidToken() {
        assertThrows(AuthenticationException.class, () -> 
            tokenVerificationService.verifyToken("invalid.token.here"));
    }

    @Test
    void testGetUserIdFromToken() {
        UUID result = tokenVerificationService.getUserIdFromToken(validToken);
        assertEquals(userId, result);
    }

    @Test
    void testGetUserRoleFromToken() {
        Role role = tokenVerificationService.getUserRoleFromToken(validToken);
        assertEquals(Role.PACILIAN, role);
    }

    @Test
    void testGetUserNameFromToken() {
        String name = tokenVerificationService.getUserNameFromToken(validToken);
        assertEquals(userName, name);
    }

    @Test
    void testValidateRole_Success() {
        assertDoesNotThrow(() -> 
            tokenVerificationService.validateRole(validToken, Role.PACILIAN));
    }

    @Test
    void testValidateRole_Failure() {
        assertThrows(AuthenticationException.class, () -> 
            tokenVerificationService.validateRole(validToken, Role.CAREGIVER));
    }

    @Test
    void testVerifyToken_MissingClaims() {
        // Create token without required claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", Role.PACILIAN.name());
        // Missing id claim

        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);

        String invalidToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(AuthenticationException.class, () -> 
            tokenVerificationService.verifyToken(invalidToken));
    }

    @Test
    void testVerifyToken_InvalidRole() {
        // Create token with invalid role
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId.toString());
        claims.put("role", "INVALID_ROLE");
        claims.put("name", userName);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);

        String invalidToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(AuthenticationException.class, () -> 
            tokenVerificationService.verifyToken(invalidToken));
    }
} 