package id.ac.ui.cs.advprog.berating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.filter.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;
    
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID patientId;
    private UUID consultationId;
    private UUID reviewId;
    private Review review;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        consultationId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        // Setup Review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Pelayanan bagus")
                .status(ReviewStatus.PENDING)
                .createdAt(new Date())
                .build();

        // Setup ReviewRequest
        reviewRequest = new ReviewRequest();
        reviewRequest.setDoctorId(doctorId);
        reviewRequest.setPatientId(patientId);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Pelayanan bagus");
    }

    // POSITIVE CASES

    @Test
    @WithMockUser
    void testGetDoctorReviews_Success() throws Exception {
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.getDoctorReviews(doctorId)).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/doctor/{doctorId}", doctorId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(reviewId.toString()))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Pelayanan bagus"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(reviewService).getDoctorReviews(doctorId);
    }

    @Test
    @WithMockUser
    void testCreateReview_Success() throws Exception {
        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews/create/{consultationId}", consultationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Pelayanan bagus"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    @WithMockUser
    void testUpdateReviewStatus_Success() throws Exception {
        Review updatedReview = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Pelayanan bagus")
                .status(ReviewStatus.APPROVED)
                .createdAt(new Date())
                .build();

        when(reviewService.updateReviewStatus(reviewId, ReviewStatus.APPROVED)).thenReturn(updatedReview);

        mockMvc.perform(patch("/api/reviews/{reviewId}/status", reviewId)
                .param("status", "APPROVED")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(reviewService).updateReviewStatus(reviewId, ReviewStatus.APPROVED);
    }

    // NEGATIVE CASES

    @Test
    @WithMockUser
    void testCreateReview_InvalidRating() throws Exception {
        reviewRequest.setRating(6); // Invalid rating (should be 1-5)

        mockMvc.perform(post("/api/reviews/create/{consultationId}", consultationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(), any());
    }

    @Test
    @WithMockUser
    void testCreateReview_MissingRequiredField() throws Exception {
        reviewRequest.setRating(null); // Required field

        mockMvc.perform(post("/api/reviews/create/{consultationId}", consultationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(), any());
    }

    @Test
    @WithMockUser
    void testUpdateReviewStatus_InvalidStatus() throws Exception {
        doThrow(new IllegalArgumentException("Invalid status")).when(reviewService)
                .updateReviewStatus(any(UUID.class), any(ReviewStatus.class));

        mockMvc.perform(patch("/api/reviews/{reviewId}/status", reviewId)
                .param("status", "INVALID_STATUS"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        verify(reviewService).updateReviewStatus(any(), any());
    }

    @Test
    @WithMockUser
    void testUpdateReviewStatus_ReviewNotFound() throws Exception {
        when(reviewService.updateReviewStatus(any(UUID.class), any(ReviewStatus.class)))
                .thenThrow(new RuntimeException("Review not found"));

        mockMvc.perform(patch("/api/reviews/{reviewId}/status", reviewId)
                .param("status", "APPROVED"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

        verify(reviewService).updateReviewStatus(reviewId, ReviewStatus.APPROVED);
    }

    // CORNER CASES

    @Test
    @WithMockUser
    void testGetDoctorReviews_EmptyList() throws Exception {
        when(reviewService.getDoctorReviews(doctorId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reviews/doctor/{doctorId}", doctorId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reviewService).getDoctorReviews(doctorId);
    }

    @Test
    @WithMockUser
    void testCreateReview_WithoutComment() throws Exception {
        reviewRequest.setComment(null); // Comment is optional
        
        Review reviewWithoutComment = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .status(ReviewStatus.PENDING)
                .createdAt(new Date())
                .build();

        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(reviewWithoutComment);

        mockMvc.perform(post("/api/reviews/create/{consultationId}", consultationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.comment").doesNotExist());

        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    @WithMockUser
    void testCreateReview_MinimumRating() throws Exception {
        reviewRequest.setRating(1); // Minimum valid rating
        
        Review reviewWithMinRating = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(1)
                .comment("Pelayanan bagus")
                .status(ReviewStatus.PENDING)
                .createdAt(new Date())
                .build();

        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(reviewWithMinRating);

        mockMvc.perform(post("/api/reviews/create/{consultationId}", consultationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(1));

        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    @WithMockUser
    void testUpdateReviewStatus_SameStatus() throws Exception {
        when(reviewService.updateReviewStatus(reviewId, ReviewStatus.PENDING)).thenReturn(review);

        mockMvc.perform(patch("/api/reviews/{reviewId}/status", reviewId)
                .param("status", "PENDING")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reviewService).updateReviewStatus(reviewId, ReviewStatus.PENDING);
    }
}