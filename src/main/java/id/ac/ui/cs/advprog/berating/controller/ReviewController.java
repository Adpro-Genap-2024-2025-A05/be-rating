package id.ac.ui.cs.advprog.berating.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.berating.dto.BaseResponseDTO;
import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import id.ac.ui.cs.advprog.berating.model.Review;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping(value = "/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponseDTO<List<Review>>> getDoctorReviews(@PathVariable("doctorId") UUID doctorId) {
        BaseResponseDTO<List<Review>> baseResponseDTO = new BaseResponseDTO<>();
        List<Review> reviews = reviewService.getDoctorReviews(doctorId);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reviews);
        baseResponseDTO.setMessage("Success to retrieve doctor reviews.");
        baseResponseDTO.setTimestamp(new Date());

        return ResponseEntity.ok(baseResponseDTO);
    }

    @PostMapping(value = "/create/{consultationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('PACILIAN')")
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
            baseResponseDTO.setMessage("Success to create review.");
            baseResponseDTO.setTimestamp(new Date());

            return ResponseEntity.status(HttpStatus.CREATED).body(baseResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{patientId}/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponseDTO<List<Review>>> getReviewUser(@PathVariable("patientId") UUID patientId) {
        BaseResponseDTO<List<Review>> baseResponseDTO = new BaseResponseDTO<>();
        List<Review> reviews = reviewService.getReviewUser(patientId);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reviews);
        baseResponseDTO.setMessage("Success to retrieve patient reviews.");
        baseResponseDTO.setTimestamp(new Date());

        return ResponseEntity.ok(baseResponseDTO);
    }

    @GetMapping(value = "/{reviewId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponseDTO<List<Review>>> getReviewHistory(@PathVariable("reviewId") UUID reviewId) {
        BaseResponseDTO<List<Review>> baseResponseDTO = new BaseResponseDTO<>();
        List<Review> reviews = reviewService.getReviewHistory(reviewId);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reviews);
        baseResponseDTO.setMessage("Success to retrieve review history.");
        baseResponseDTO.setTimestamp(new Date());

        return ResponseEntity.ok(baseResponseDTO);
    }

    @PutMapping(value = "/{reviewId}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('PACILIAN')")
    public ResponseEntity<?> updateReview(
        @PathVariable("reviewId") UUID reviewId,
        @RequestBody ReviewRequest reviewRequest) {
        try {
            if (reviewRequest.getRating() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating is required");
            }

            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5");
            }
            Review review = reviewService.updateReview(reviewId, reviewRequest);
            BaseResponseDTO<Review> baseResponseDTO = new BaseResponseDTO<>();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(review);
            baseResponseDTO.setMessage("Success to update review.");
            baseResponseDTO.setTimestamp(new Date());

            return ResponseEntity.status(HttpStatus.OK).body(baseResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{reviewId}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('PACILIAN')")
    public ResponseEntity<BaseResponseDTO<Review>> deleteReview(@PathVariable("reviewId") UUID reviewId) {
        Review review = reviewService.deleteReview(reviewId);
        BaseResponseDTO<Review> baseResponseDTO = new BaseResponseDTO<>();
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(review);
        baseResponseDTO.setMessage("Success to delete review.");
        baseResponseDTO.setTimestamp(new Date());

        return ResponseEntity.status(HttpStatus.OK).body(baseResponseDTO);
    }
}
