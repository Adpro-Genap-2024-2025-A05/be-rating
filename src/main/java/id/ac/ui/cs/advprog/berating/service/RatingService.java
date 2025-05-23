package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.*;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    RatingResponseDto createRating(CreateRatingDto dto, String token);
    RatingResponseDto updateRating(UUID ratingId, UpdateRatingDto dto, String token);
    void deleteRating(UUID ratingId, String token);
    List<RatingResponseDto> getRatingsByCaregiver(UUID caregiverId);
    List<RatingResponseDto> getRatingsByPacilian(UUID pacilianId, String token);
    CaregiverRatingStatsDto getCaregiverRatingStats(UUID caregiverId);
    RatingResponseDto getRatingById(UUID ratingId);
}