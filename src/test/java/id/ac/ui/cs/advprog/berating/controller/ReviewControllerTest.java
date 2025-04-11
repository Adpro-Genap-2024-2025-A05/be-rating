package id.ac.ui.cs.advprog.berating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.model.ReviewStatus;
import id.ac.ui.cs.advprog.berating.model.User;
import id.ac.ui.cs.advprog.berating.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review review;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();

        User patient = new User();
        patient.setId(UUID.randomUUID());
        patient.setName("Patient A");

        User doctor = new User();
        doctor.setId(UUID.randomUUID());
        doctor.setName("Doctor B");

        review = new Review();
        review.setId(reviewId);
        review.setPatient(patient);
        review.setDoctor(doctor);
        review.setRating(5);
        review.setComment("Pelayanan bagus");
        review.setStatus(ReviewStatus.PENDING);
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateReview() throws Exception {
        when(reviewService.createReview(review)).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Pelayanan bagus"));
    }

    @Test
    void testGetReviewById() throws Exception {
        when(reviewService.getReviewById(reviewId)).thenReturn(review);

        mockMvc.perform(get("/api/reviews/" + reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Pelayanan bagus"));
    }

    @Test
    void testUpdateReview() throws Exception {
        review.setComment("Updated comment");
        review.setRating(4);

        when(reviewService.updateReview(reviewId, review)).thenReturn(review);

        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

    @Test
    void testDeleteReview() throws Exception {
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/reviews/" + reviewId))
                .andExpect(status().isNoContent());
    }
}
