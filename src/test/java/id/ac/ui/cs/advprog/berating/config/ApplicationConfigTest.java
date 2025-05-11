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
    private AuthenticationConfiguration authenticationConfiguration;

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig(jwtService);
    }

    // POSITIVE CASES

    @Test
    void userDetailsService_ShouldCreateUserDetailsServiceImpl() {
        // Act
        UserDetailsService service = applicationConfig.userDetailsService();

        // Assert
        assertNotNull(service);
        assertTrue(service instanceof id.ac.ui.cs.advprog.berating.service.UserDetailsServiceImpl);
    }

    @Test
    void authenticationProvider_ShouldCreateDaoAuthenticationProvider() {
        // Act
        AuthenticationProvider provider = applicationConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;
        assertNotNull(ReflectionTestUtils.getField(daoProvider, "userDetailsService"));
        assertNotNull(ReflectionTestUtils.getField(daoProvider, "passwordEncoder"));
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        // Arrange
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        // Act
        AuthenticationManager result = applicationConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertEquals(mockAuthManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Act
        PasswordEncoder encoder = applicationConfig.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    // NEGATIVE CASES

    @Test
    void authenticationManager_WhenExceptionOccurs_ShouldPropagateException() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager())
            .thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            applicationConfig.authenticationManager(authenticationConfiguration));
    }

    // CORNER CASES

    @Test
    void authenticationProvider_WithNullJwtService_ShouldThrowException() {
        // Arrange
        ApplicationConfig configWithNullService = new ApplicationConfig(null);
        ReflectionTestUtils.setField(configWithNullService, "jwtService", null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            configWithNullService.authenticationProvider());
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentHashesForSamePassword() {
        // Arrange
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        String password = "testPassword";

        // Act
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);

        // Assert
        assertNotEquals(hash1, hash2); // BCrypt should generate different salts
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }
}