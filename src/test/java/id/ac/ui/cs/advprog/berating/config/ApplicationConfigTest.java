package id.ac.ui.cs.advprog.berating.config;

import id.ac.ui.cs.advprog.berating.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig(jwtService, userDetailsService);
    }

    // POSITIVE CASES

    @Test
    void authenticationProvider_ShouldCreateDaoAuthenticationProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;
        assertEquals(userDetailsService, ReflectionTestUtils.getField(daoProvider, "userDetailsService"));
        assertNotNull(ReflectionTestUtils.getField(daoProvider, "passwordEncoder"));
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager result = applicationConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(mockAuthManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    // NEGATIVE CASES

    @Test
    void authenticationManager_WhenExceptionOccurs_ShouldPropagateException() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager())
            .thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> 
            applicationConfig.authenticationManager(authenticationConfiguration));
    }

    // CORNER CASES

    @Test
    void constructor_WithNullUserDetailsService_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ApplicationConfig(jwtService, null));
    }

    @Test
    void constructor_WithNullJwtService_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ApplicationConfig(null, userDetailsService));
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentHashesForSamePassword() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        String password = "testPassword";

        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);

        assertNotEquals(hash1, hash2);
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }
}