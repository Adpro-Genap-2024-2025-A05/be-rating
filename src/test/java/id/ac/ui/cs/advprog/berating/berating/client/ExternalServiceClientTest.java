package id.ac.ui.cs.advprog.berating.client;

import id.ac.ui.cs.advprog.berating.dto.ApiResponseDto;
import id.ac.ui.cs.advprog.berating.dto.KonsultasiResponseDto;
import id.ac.ui.cs.advprog.berating.dto.UserProfileDto;
import id.ac.ui.cs.advprog.berating.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalServiceClient externalServiceClient;

    private String konsultasiServiceUrl;
    private String authServiceUrl;
    private UUID konsultasiId;
    private String token;
    private KonsultasiResponseDto konsultasiResponse;
    private UserProfileDto userProfileResponse;

    @BeforeEach
    void setUp() {
        konsultasiServiceUrl = "http://konsultasi-service";
        authServiceUrl = "http://auth-service";
        konsultasiId = UUID.randomUUID();
        token = "test-token";

        ReflectionTestUtils.setField(externalServiceClient, "konsultasiServiceUrl", konsultasiServiceUrl);
        ReflectionTestUtils.setField(externalServiceClient, "authServiceUrl", authServiceUrl);

        konsultasiResponse = KonsultasiResponseDto.builder()
                .id(konsultasiId)
                .status("DONE")
                .build();

        userProfileResponse = UserProfileDto.builder()
                .id(UUID.randomUUID().toString())
                .name("Test User")
                .build();
    }

    @Test
    void testGetKonsultasiById_Success() {
        // Arrange
        ApiResponseDto<KonsultasiResponseDto> apiResponse = ApiResponseDto.success(200, "Success", konsultasiResponse);
        ResponseEntity<ApiResponseDto<KonsultasiResponseDto>> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(konsultasiServiceUrl + "/konsultasi/" + konsultasiId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        KonsultasiResponseDto result = externalServiceClient.getKonsultasiById(konsultasiId, token);

        // Assert
        assertNotNull(result);
        assertEquals(konsultasiId, result.getId());
        assertEquals("DONE", result.getStatus());
    }

    @Test
    void testGetKonsultasiById_NotFound() {
        // Arrange
        ApiResponseDto<KonsultasiResponseDto> apiResponse = ApiResponseDto.success(200, "Success", null);
        ResponseEntity<ApiResponseDto<KonsultasiResponseDto>> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(konsultasiServiceUrl + "/konsultasi/" + konsultasiId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            externalServiceClient.getKonsultasiById(konsultasiId, token));
    }

    @Test
    void testGetKonsultasiById_ServiceError() {
        // Arrange
        when(restTemplate.exchange(
                eq(konsultasiServiceUrl + "/konsultasi/" + konsultasiId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            externalServiceClient.getKonsultasiById(konsultasiId, token));
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        ApiResponseDto<UserProfileDto> apiResponse = ApiResponseDto.success(200, "Success", userProfileResponse);
        ResponseEntity<ApiResponseDto<UserProfileDto>> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(authServiceUrl + "/profile"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDto result = externalServiceClient.getUserProfile(token);

        // Assert
        assertNotNull(result);
        assertEquals(userProfileResponse.getId(), result.getId());
        assertEquals(userProfileResponse.getName(), result.getName());
    }

    @Test
    void testGetUserProfile_NotFound() {
        // Arrange
        ApiResponseDto<UserProfileDto> apiResponse = ApiResponseDto.success(200, "Success", null);
        ResponseEntity<ApiResponseDto<UserProfileDto>> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(authServiceUrl + "/profile"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            externalServiceClient.getUserProfile(token));
    }

    @Test
    void testGetUserProfile_ServiceError() {
        // Arrange
        when(restTemplate.exchange(
                eq(authServiceUrl + "/profile"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            externalServiceClient.getUserProfile(token));
    }
} 