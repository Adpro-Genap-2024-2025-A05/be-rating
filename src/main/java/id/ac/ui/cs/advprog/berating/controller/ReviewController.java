package id.ac.ui.cs.advprog.berating.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.berating.dto.BaseResponseDTO;
import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.exception.ReviewNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;
    private static final String successMessage = "Success to retrieve doctor reviews.";

    @GetMapping(value = "/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponseDTO<List<Review>>> getDoctorReviews(@PathVariable("doctorId") UUID doctorId) {
        BaseResponseDTO<List<Review>> baseResponseDTO = new BaseResponseDTO<>();
        List<Review> reviews = reviewService.getDoctorReviews(doctorId);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reviews);
        baseResponseDTO.setMessage(successMessage);
        baseResponseDTO.setTimestamp(new Date());

        return ResponseEntity.ok(baseResponseDTO);
    }

    @PostMapping(value = "/create/{consultationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReview(
            @PathVariable("consultationId") UUID consultationId,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        try {
            if (reviewRequest.getRating() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating is required");
            }

            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5");
            }

            Review review = reviewService.createReview(consultationId, reviewRequest);
            BaseResponseDTO<Review> baseResponseDTO = new BaseResponseDTO<>();
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(review);
            baseResponseDTO.setMessage(successMessage);
            baseResponseDTO.setTimestamp(new Date());

            return ResponseEntity.status(HttpStatus.CREATED).body(baseResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping(value = "/{reviewId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReviewStatus(
            @PathVariable("reviewId") UUID reviewId,
            @RequestParam("status") ReviewStatus status) {

        if (status == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status");
        }

        try {
            Review updatedReview = reviewService.updateReviewStatus(reviewId, status);
            if (updatedReview == null) {
                throw new ReviewNotFoundException(reviewId.toString());
            }
            
            BaseResponseDTO<Review> baseResponseDTO = new BaseResponseDTO<>();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedReview);
            baseResponseDTO.setMessage(successMessage);
            baseResponseDTO.setTimestamp(new Date());

            return ResponseEntity.ok(baseResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
