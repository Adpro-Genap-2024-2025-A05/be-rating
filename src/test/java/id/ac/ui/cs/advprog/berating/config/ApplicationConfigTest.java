package id.ac.ui.cs.advprog.berating.config;

import id.ac.ui.cs.advprog.berating.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletMapping servletMapping;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthFilter, authenticationProvider);
    }

    // POSITIVE CASES

    @Test
    void securityFilterChain_ShouldConfigureSecurityCorrectly() throws Exception {
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        assertNotNull(filterChain);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).cors(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(authenticationProvider);
        verify(httpSecurity).addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    }

    @Test
    void corsConfigurationSource_ShouldConfigureCorsCorrectly() {
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getContextPath()).thenReturn("");
        when(request.getServletPath()).thenReturn("");
        when(request.getHttpServletMapping()).thenReturn(servletMapping);
        when(servletMapping.getMappingMatch()).thenReturn(null);

        CorsConfigurationSource corsConfig = securityConfig.corsConfigurationSource();

        assertNotNull(corsConfig);
        CorsConfiguration config = corsConfig.getCorsConfiguration(request);
        assertNotNull(config);
        assertEquals(List.of("*"), config.getAllowedOrigins());
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"), config.getAllowedMethods());
        assertEquals(List.of("Authorization", "Content-Type"), config.getAllowedHeaders());
    }

    // NEGATIVE CASES

    @Test
    void securityFilterChain_WhenExceptionOccurs_ShouldPropagateException() throws Exception {
        when(httpSecurity.csrf(any())).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> securityConfig.securityFilterChain(httpSecurity));
    }

    // CORNER CASES

    @Test
    void securityFilterChain_WithNullHttpSecurity_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> securityConfig.securityFilterChain(null));
    }

    @Test
    void corsConfigurationSource_ShouldHandleNullRequest() {
        CorsConfigurationSource corsConfig = securityConfig.corsConfigurationSource();

        assertThrows(NullPointerException.class, () -> corsConfig.getCorsConfiguration(null));
    }

    @Test
    void securityFilterChain_ShouldConfigureStatelessSession() throws Exception {
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        securityConfig.securityFilterChain(httpSecurity);

        verify(httpSecurity).sessionManagement(any());
    }
}