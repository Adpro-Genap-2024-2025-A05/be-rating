package id.ac.ui.cs.advprog.berating.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class WebClientConfigTest {

    private WebClientConfig webClientConfig;
    private static final String EXPECTED_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        webClientConfig = new WebClientConfig();
    }

    @Test
    void loadProfileUrl_WhenDotenvFails_ShouldReturnDefaultUrl() {
        try (MockedStatic<Dotenv> mockedDotenv = mockStatic(Dotenv.class)) {
            // Mock Dotenv to throw an exception
            mockedDotenv.when(() -> Dotenv.configure().load())
                    .thenThrow(new RuntimeException("Failed to load .env file"));

            // Call the private method using reflection
            String result = (String) ReflectionTestUtils.invokeMethod(webClientConfig, "loadProfileUrl");

            // Verify the result is the default URL
            assertEquals(EXPECTED_URL, result);
        }
    }

    @Test
    void loadProfileUrl_WhenDotenvSucceeds_ShouldUseDotenvValue() {
        try (MockedStatic<Dotenv> mockedDotenv = mockStatic(Dotenv.class)) {
            // Create mock DotenvBuilder and Dotenv
            DotenvBuilder mockBuilder = Mockito.mock(DotenvBuilder.class);
            Dotenv mockDotenv = Mockito.mock(Dotenv.class);
            
            // Setup the mock chain
            Mockito.when(mockDotenv.get("PROFILE_SERVICE_URL")).thenReturn(EXPECTED_URL);
            Mockito.when(mockBuilder.load()).thenReturn(mockDotenv);
            mockedDotenv.when(Dotenv::configure).thenReturn(mockBuilder);

            // Call the private method using reflection
            String result = (String) ReflectionTestUtils.invokeMethod(webClientConfig, "loadProfileUrl");

            // Verify the result
            assertEquals(EXPECTED_URL, result);
        }
    }
} 