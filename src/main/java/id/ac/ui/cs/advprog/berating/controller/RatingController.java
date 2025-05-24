package id.ac.ui.cs.advprog.berating.controller;

import id.ac.ui.cs.advprog.berating.dto.*;
import id.ac.ui.cs.advprog.berating.service.RatingService;
import id.ac.ui.cs.advprog.berating.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<RatingResponseDto>> createRating(
            @Valid @RequestBody CreateRatingDto dto,
            HttpServletRequest request) {

        String token = extractToken(request);
        RatingResponseDto response = ratingService.createRating(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(201, "Rating created successfully", response));
    }

    @PutMapping(path = "/{ratingId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<RatingResponseDto>> updateRating(
            @PathVariable UUID ratingId,
            @Valid @RequestBody UpdateRatingDto dto,
            HttpServletRequest request) {

        String token = extractToken(request);
        RatingResponseDto response = ratingService.updateRating(ratingId, dto, token);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Rating updated successfully", response));
    }

    @DeleteMapping(path = "/{ratingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<Void>> deleteRating(
            @PathVariable UUID ratingId,
            HttpServletRequest request) {

        String token = extractToken(request);
        ratingService.deleteRating(ratingId, token);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Rating deleted successfully", null));
    }

    @GetMapping(path = "/caregiver/{caregiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<RatingResponseDto>>> getRatingsByCaregiver(
            @PathVariable UUID caregiverId) {

        List<RatingResponseDto> ratings = ratingService.getRatingsByCaregiver(caregiverId);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Ratings retrieved successfully", ratings));
    }

    @GetMapping(path = "/caregiver/{caregiverId}/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<CaregiverRatingStatsDto>> getCaregiverRatingStats(
            @PathVariable String caregiverId) {

        UUID caregiverUuid = UUID.fromString(caregiverId);
        CaregiverRatingStatsDto stats = ratingService.getCaregiverRatingStats(caregiverUuid);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Caregiver rating stats retrieved successfully", stats));
    }

    @GetMapping(path = "/pacilian/{pacilianId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<RatingResponseDto>>> getRatingsByPacilian(
            @PathVariable UUID pacilianId,
            HttpServletRequest request) {

        String token = extractToken(request);
        List<RatingResponseDto> ratings = ratingService.getRatingsByPacilian(pacilianId, token);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Pacilian ratings retrieved successfully", ratings));
    }

    @GetMapping(path = "/{ratingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<RatingResponseDto>> getRatingById(
            @PathVariable UUID ratingId) {

        RatingResponseDto rating = ratingService.getRatingById(ratingId);

        return ResponseEntity.ok(
                ApiResponseDto.success(200, "Rating retrieved successfully", rating));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }
}