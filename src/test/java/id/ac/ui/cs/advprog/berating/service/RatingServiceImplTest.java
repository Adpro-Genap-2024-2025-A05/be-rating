package id.ac.ui.cs.advprog.test.service;

import id.ac.ui.cs.advprog.berating.berating.service.RatingServiceImpl;
import id.ac.ui.cs.advprog.berating.berating.service.TokenVerificationService;
import id.ac.ui.cs.advprog.berating.client.ExternalServiceClient;
import id.ac.ui.cs.advprog.berating.dto.*;
import id.ac.ui.cs.advprog.berating.enums.Role;
import id.ac.ui.cs.advprog.berating.exception.RatingException;
import id.ac.ui.cs.advprog.berating.model.Rating;
import id.ac.ui.cs.advprog.berating.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @Mock
    private TokenVerificationService tokenVerificationService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private UUID pacilianId;
    private UUID caregiverId;
    private UUID konsultasiId;
    private String token;
    private TokenVerificationResponseDto verificationResponse;
    private KonsultasiResponseDto konsultasiResponse;

    @BeforeEach
    void setUp() {
        pacilianId = UUID.randomUUID();
        caregiverId = UUID.randomUUID();
        konsultasiId = UUID.randomUUID();
        token = "test-token";
        
        verificationResponse = TokenVerificationResponseDto.builder()
                .userId(pacilianId.toString())
                .userName("Test Pacilian")
                .role(Role.PACILIAN)
                .build();

        konsultasiResponse = KonsultasiResponseDto.builder()
                .id(konsultasiId)
                .pacilianId(pacilianId)
                .caregiverId(caregiverId)
                .status("DONE")
                .build();
    }

    @Test
    void testCreateRating_Success() {
        // Arrange
        CreateRatingDto dto = CreateRatingDto.builder()
                .konsultasiId(konsultasiId)
                .rating(5)
                .review("Great service!")
                .build();

        when(tokenVerificationService.verifyToken(token)).thenReturn(verificationResponse);
        when(ratingRepository.existsByKonsultasiId(konsultasiId)).thenReturn(false);
        when(externalServiceClient.getKonsultasiById(konsultasiId, token)).thenReturn(konsultasiResponse);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RatingResponseDto response = ratingService.createRating(dto, token);

        // Assert
        assertNotNull(response);
        assertEquals(konsultasiId, response.getKonsultasiId());
        assertEquals(pacilianId, response.getPacilianId());
        assertEquals(caregiverId, response.getCaregiverId());
        assertEquals(5, response.getRating());
        assertEquals("Great service!", response.getReview());
    }

    @Test
    void testCreateRating_NonPacilian() {
        // Arrange
        CreateRatingDto dto = CreateRatingDto.builder()
                .konsultasiId(konsultasiId)
                .rating(5)
                .review("Great service!")
                .build();

        verificationResponse.setRole(Role.CAREGIVER);
        when(tokenVerificationService.verifyToken(token)).thenReturn(verificationResponse);

        // Act & Assert
        assertThrows(RatingException.class, () -> ratingService.createRating(dto, token));
    }

    @Test
    void testUpdateRating_Success() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        UpdateRatingDto dto = UpdateRatingDto.builder()
                .rating(4)
                .review("Updated review")
                .build();

        Rating existingRating = Rating.builder()
                .id(ratingId)
                .konsultasiId(konsultasiId)
                .pacilianId(pacilianId)
                .caregiverId(caregiverId)
                .rating(5)
                .review("Old review")
                .build();

        when(tokenVerificationService.verifyToken(token)).thenReturn(verificationResponse);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RatingResponseDto response = ratingService.updateRating(ratingId, dto, token);

        // Assert
        assertNotNull(response);
        assertEquals(4, response.getRating());
        assertEquals("Updated review", response.getReview());
    }

    @Test
    void testDeleteRating_Success() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        Rating existingRating = Rating.builder()
                .id(ratingId)
                .pacilianId(pacilianId)
                .build();

        when(tokenVerificationService.verifyToken(token)).thenReturn(verificationResponse);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existingRating));

        // Act
        ratingService.deleteRating(ratingId, token);

        // Assert
        verify(ratingRepository).deleteById(ratingId);
    }

    @Test
    void testGetRatingsByCaregiver() {
        // Arrange
        List<Rating> ratings = Arrays.asList(
            Rating.builder()
                .id(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(5)
                .build(),
            Rating.builder()
                .id(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(4)
                .build()
        );

        when(ratingRepository.findByCaregiverId(caregiverId)).thenReturn(ratings);

        // Act
        List<RatingResponseDto> response = ratingService.getRatingsByCaregiver(caregiverId);

        // Assert
        assertEquals(2, response.size());
        assertTrue(response.stream().allMatch(r -> r.getCaregiverId().equals(caregiverId)));
    }

    @Test
    void testGetRatingsByPacilian_Success() {
        // Arrange
        List<Rating> ratings = Arrays.asList(
            Rating.builder()
                .id(UUID.randomUUID())
                .pacilianId(pacilianId)
                .rating(5)
                .build()
        );

        when(tokenVerificationService.verifyToken(token)).thenReturn(verificationResponse);
        when(ratingRepository.findByPacilianId(pacilianId)).thenReturn(ratings);

        // Act
        List<RatingResponseDto> response = ratingService.getRatingsByPacilian(pacilianId, token);

        // Assert
        assertEquals(1, response.size());
        assertEquals(pacilianId, response.get(0).getPacilianId());
    }

    @Test
    void testGetCaregiverRatingStats() {
        // Arrange
        List<Rating> ratings = Arrays.asList(
            Rating.builder()
                .id(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(5)
                .build(),
            Rating.builder()
                .id(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(4)
                .build()
        );

        when(ratingRepository.findByCaregiverId(caregiverId)).thenReturn(ratings);
        when(ratingRepository.getAverageRatingForCaregiver(caregiverId)).thenReturn(4.5);
        when(ratingRepository.getTotalRatingsForCaregiver(caregiverId)).thenReturn(2L);

        // Act
        CaregiverRatingStatsDto stats = ratingService.getCaregiverRatingStats(caregiverId);

        // Assert
        assertNotNull(stats);
        assertEquals(caregiverId, stats.getCaregiverId());
        assertEquals(4.5, stats.getAverageRating());
        assertEquals(2L, stats.getTotalRatings());
        assertEquals(2, stats.getRatings().size());
    }

    @Test
    void testGetRatingById_Success() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        Rating rating = Rating.builder()
                .id(ratingId)
                .rating(5)
                .build();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act
        RatingResponseDto response = ratingService.getRatingById(ratingId);

        // Assert
        assertNotNull(response);
        assertEquals(ratingId, response.getId());
        assertEquals(5, response.getRating());
    }

    @Test
    void testGetRatingById_NotFound() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RatingException.class, () -> ratingService.getRatingById(ratingId));
    }
} 