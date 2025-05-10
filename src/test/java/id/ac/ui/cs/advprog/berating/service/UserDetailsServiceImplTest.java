package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.UserDTO;
import id.ac.ui.cs.advprog.berating.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private UserDTO userDTO;
    private String token;

    @BeforeEach
    void setUp() {
        token = "valid.jwt.token";
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setRole(Role.PACILLIANS);
    }

    // POSITIVE CASES

    @Test
    void loadUserByUsername_WithValidToken_ShouldReturnUserDetails() {
        when(userService.getUserLogin(token)).thenReturn(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertNotNull(userDetails);
        assertEquals(userDTO.getUsername(), userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(userDTO.getRole().toString())));
        verify(userService).getUserLogin(token);
    }

    @Test
    void loadUserByUsername_WithDifferentRole_ShouldReturnCorrectAuthority() {
        userDTO.setRole(Role.CAREGIVER);
        when(userService.getUserLogin(token)).thenReturn(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.CAREGIVER.toString())));
        verify(userService).getUserLogin(token);
    }

    // NEGATIVE CASES

    @Test
    void loadUserByUsername_WhenUserServiceThrowsException_ShouldReturnNull() {
        when(userService.getUserLogin(token)).thenThrow(new RuntimeException("Service error"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertNull(userDetails);
        verify(userService).getUserLogin(token);
    }

    @Test
    void loadUserByUsername_WithInvalidToken_ShouldReturnNull() {
        when(userService.getUserLogin(token)).thenThrow(new UsernameNotFoundException("Invalid token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertNull(userDetails);
        verify(userService).getUserLogin(token);
    }

    // CORNER CASES

    @Test
    void loadUserByUsername_WithEmptyToken_ShouldReturnNull() {
        when(userService.getUserLogin("")).thenThrow(new UsernameNotFoundException("Empty token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("");

        assertNull(userDetails);
        verify(userService).getUserLogin("");
    }

    @Test
    void loadUserByUsername_WithNullToken_ShouldReturnNull() {
        when(userService.getUserLogin(null)).thenThrow(new UsernameNotFoundException("Null token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(null);

        assertNull(userDetails);
        verify(userService).getUserLogin(null);
    }

    @Test
    void loadUserByUsername_WithNullRole_ShouldReturnUserDetailsWithEmptyAuthorities() {
        // Create a new UserDTO with null role
        UserDTO userWithNullRole = new UserDTO();
        userWithNullRole.setUsername("testuser");
        userWithNullRole.setRole(null);
        
        when(userService.getUserLogin(token)).thenReturn(userWithNullRole);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertNotNull(userDetails);
        assertEquals(userWithNullRole.getUsername(), userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userService).getUserLogin(token);
    }
} 