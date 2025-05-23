package id.ac.ui.cs.advprog.berating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaregiverRatingStatsDto {
    private UUID caregiverId;
    private Double averageRating;
    private Long totalRatings;
    private List<RatingResponseDto> ratings;
}