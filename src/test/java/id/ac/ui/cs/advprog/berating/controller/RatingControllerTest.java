package id.ac.ui.cs.advprog.berating.controller;

import id.ac.ui.cs.advprog.berating.controller.RatingController;
import id.ac.ui.cs.advprog.berating.dto.*;
import id.ac.ui.cs.advprog.berating.exception.GlobalExceptionHandler;
import id.ac.ui.cs.advprog.berating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private MockMvc mockMvc;
    private UUID ratingId;
    private UUID caregiverId;
    private UUID pacilianId;
    private String token;
    private RatingResponseDto ratingResponse;
    private CreateRatingDto createRatingDto;
    private UpdateRatingDto updateRatingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        ratingId = UUID.randomUUID();
        caregiverId = UUID.randomUUID();
        pacilianId = UUID.randomUUID();
        token = "test-token";

        ratingResponse = RatingResponseDto.builder()
                .id(ratingId)
                .rating(5)
                .review("Great service!")
                .build();

        createRatingDto = CreateRatingDto.builder()
                .konsultasiId(UUID.randomUUID())
                .rating(5)
                .review("Great service!")
                .build();

        updateRatingDto = UpdateRatingDto.builder()
                .rating(4)
                .review("Updated review")
                .build();
    }

    @Test
    void testCreateRating_Success() throws Exception {
        when(ratingService.createRating(any(), any())).thenReturn(ratingResponse);

        mockMvc.perform(post("/rating")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"konsultasiId\":\"" + createRatingDto.getKonsultasiId() + "\",\"rating\":5,\"review\":\"Great service!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Rating created successfully"))
                .andExpect(jsonPath("$.data.id").value(ratingId.toString()));
    }

    @Test
    void testCreateRating_MissingToken() throws Exception {
        mockMvc.perform(post("/rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"konsultasiId\":\"" + createRatingDto.getKonsultasiId() + "\",\"rating\":5,\"review\":\"Great service!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authorization header is missing or invalid"));
    }

    @Test
    void testCreateRating_InvalidTokenFormat() throws Exception {
        mockMvc.perform(post("/rating")
                .header("Authorization", "InvalidFormat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"konsultasiId\":\"" + createRatingDto.getKonsultasiId() + "\",\"rating\":5,\"review\":\"Great service!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authorization header is missing or invalid"));
    }

    @Test
    void testUpdateRating_Success() throws Exception {
        when(ratingService.updateRating(any(), any(), any())).thenReturn(ratingResponse);

        mockMvc.perform(put("/rating/" + ratingId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":4,\"review\":\"Updated review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Rating updated successfully"))
                .andExpect(jsonPath("$.data.id").value(ratingId.toString()));
    }

    @Test
    void testDeleteRating_Success() throws Exception {
        mockMvc.perform(delete("/rating/" + ratingId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Rating deleted successfully"));
    }

    @Test
    void testGetRatingsByCaregiver_Success() throws Exception {
        List<RatingResponseDto> ratings = Arrays.asList(ratingResponse);
        when(ratingService.getRatingsByCaregiver(caregiverId)).thenReturn(ratings);

        mockMvc.perform(get("/rating/caregiver/" + caregiverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Ratings retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(ratingId.toString()));
    }

    @Test
    void testGetCaregiverRatingStats_Success() throws Exception {
        CaregiverRatingStatsDto stats = CaregiverRatingStatsDto.builder()
                .caregiverId(caregiverId)
                .averageRating(4.5)
                .totalRatings(2L)
                .build();

        when(ratingService.getCaregiverRatingStats(caregiverId)).thenReturn(stats);

        mockMvc.perform(get("/rating/caregiver/" + caregiverId + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Caregiver rating stats retrieved successfully"))
                .andExpect(jsonPath("$.data.caregiverId").value(caregiverId.toString()))
                .andExpect(jsonPath("$.data.averageRating").value(4.5))
                .andExpect(jsonPath("$.data.totalRatings").value(2));
    }

    @Test
    void testGetRatingsByPacilian_Success() throws Exception {
        List<RatingResponseDto> ratings = Arrays.asList(ratingResponse);
        when(ratingService.getRatingsByPacilian(pacilianId, token)).thenReturn(ratings);

        mockMvc.perform(get("/rating/pacilian/" + pacilianId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Pacilian ratings retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(ratingId.toString()));
    }

    @Test
    void testGetRatingById_Success() throws Exception {
        when(ratingService.getRatingById(ratingId)).thenReturn(ratingResponse);

        mockMvc.perform(get("/rating/" + ratingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Rating retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(ratingId.toString()));
    }
} 