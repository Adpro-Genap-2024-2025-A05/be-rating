package id.ac.ui.cs.advprog.berating.client;

import id.ac.ui.cs.advprog.berating.dto.ApiResponseDto;
import id.ac.ui.cs.advprog.berating.dto.KonsultasiResponseDto;
import id.ac.ui.cs.advprog.berating.dto.UserProfileDto;
import id.ac.ui.cs.advprog.berating.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.konsultasi.url}")
    private String konsultasiServiceUrl;

    @Value("${services.auth.url}")
    private String authServiceUrl;

    public KonsultasiResponseDto getKonsultasiById(UUID konsultasiId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<ApiResponseDto<KonsultasiResponseDto>> response = restTemplate.exchange(
                konsultasiServiceUrl + "/konsultasi/" + konsultasiId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponseDto<KonsultasiResponseDto>>() {}
            );
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            throw new ExternalServiceException("Konsultasi not found");
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch konsultasi: " + e.getMessage());
        }
    }

    public UserProfileDto getUserProfile(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponseDto<UserProfileDto>> response = restTemplate.exchange(
                    authServiceUrl + "/profile",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponseDto<UserProfileDto>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            throw new ExternalServiceException("User profile not found");
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch user profile: " + e.getMessage());
        }
    }
}