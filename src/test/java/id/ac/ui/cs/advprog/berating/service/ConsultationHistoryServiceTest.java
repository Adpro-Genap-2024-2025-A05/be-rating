package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.ConsultationHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationHistoryServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ConsultationHistoryService consultationHistoryService;

    private ConsultationHistoryDTO consultationHistoryDTO;
    private String consultationId;

    @BeforeEach
    void setUp() {
        consultationId = "consultation123";

        consultationHistoryDTO = new ConsultationHistoryDTO();
        consultationHistoryDTO.setId(consultationId);
        consultationHistoryDTO.setUserId("user123");
        consultationHistoryDTO.setDoctorId("doctor123");
        consultationHistoryDTO.setDate(LocalDateTime.now());
        consultationHistoryDTO.setNotes("Regular checkup completed");

        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Initialize the service with the mocked WebClient
        consultationHistoryService = new ConsultationHistoryService(webClient);
    }

    // POSITIVE CASES

    @Test
    void getConsultationHistoryById_WithValidId_ShouldReturnConsultationHistoryDTO() {
        when(responseSpec.bodyToMono(ConsultationHistoryDTO.class)).thenReturn(Mono.just(consultationHistoryDTO));

        ConsultationHistoryDTO result = consultationHistoryService.getConsultationHistoryById(consultationId);

        assertNotNull(result);
        assertEquals(consultationHistoryDTO.getId(), result.getId());
        assertEquals(consultationHistoryDTO.getUserId(), result.getUserId());
        assertEquals(consultationHistoryDTO.getDoctorId(), result.getDoctorId());
        assertEquals(consultationHistoryDTO.getDate(), result.getDate());
        assertEquals(consultationHistoryDTO.getNotes(), result.getNotes());
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/consultiation/" + consultationId);
    }

    // NEGATIVE CASES

    @Test
    void getConsultationHistoryById_WhenResponseIsNull_ShouldThrowException() {
        when(responseSpec.bodyToMono(ConsultationHistoryDTO.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> 
            consultationHistoryService.getConsultationHistoryById(consultationId)
        );
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/consultiation/" + consultationId);
    }

    // CORNER CASES

    @Test
    void getConsultationHistoryById_WithEmptyId_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(ConsultationHistoryDTO.class)).thenReturn(Mono.just(consultationHistoryDTO));

        ConsultationHistoryDTO result = consultationHistoryService.getConsultationHistoryById("");

        assertNotNull(result);
        verify(requestHeadersUriSpec).uri("/api/consultiation/");
    }

    @Test
    void getConsultationHistoryById_WithNullId_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(ConsultationHistoryDTO.class)).thenReturn(Mono.just(consultationHistoryDTO));

        ConsultationHistoryDTO result = consultationHistoryService.getConsultationHistoryById(null);

        assertNotNull(result);
        verify(requestHeadersUriSpec).uri("/api/consultiation/null");
    }

    @Test
    void getConsultationHistoryById_WithSpecialCharacters_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(ConsultationHistoryDTO.class)).thenReturn(Mono.just(consultationHistoryDTO));

        ConsultationHistoryDTO result = consultationHistoryService.getConsultationHistoryById("consultation@123");

        assertNotNull(result);
        verify(requestHeadersUriSpec).uri("/api/consultiation/consultation@123");
    }
} 