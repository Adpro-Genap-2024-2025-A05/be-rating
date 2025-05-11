package id.ac.ui.cs.advprog.berating.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private String validToken;
    private String username;
    private String role;

    @BeforeEach
    void setUp() {
        username = "testuser";
        role = "ROLE_USER";
        validToken = "valid.jwt.token";
    }

    // POSITIVE CASES

    @Test
    void loadUserByUsername_WithValidToken_ShouldReturnUserDetails() {
        when(jwtService.extractUsername(validToken)).thenReturn(username);
        when(jwtService.extractRole(validToken)).thenReturn(role);

        UserDetails userDetails = userDetailsService.loadUserByUsername(validToken);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role)));
    }

    @Test
    void loadUserByUsername_WithValidTokenNoRole_ShouldReturnUserDetailsWithoutRole() {
        when(jwtService.extractUsername(validToken)).thenReturn(username);
        when(jwtService.extractRole(validToken)).thenReturn(null);

        UserDetails userDetails = userDetailsService.loadUserByUsername(validToken);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    // NEGATIVE CASES

    @Test
    void loadUserByUsername_WithNullUsername_ShouldThrowException() {
        when(jwtService.extractUsername(validToken)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(validToken));
    }

    @Test
    void loadUserByUsername_WhenJwtServiceThrowsException_ShouldPropagateException() {
        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("JWT error"));

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(validToken));
    }

    // CORNER CASES

    @Test
    void loadUserByUsername_WithEmptyToken_ShouldThrowException() {
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(""));
    }

    @Test
    void loadUserByUsername_WithNullToken_ShouldThrowException() {
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(null));
    }
} 