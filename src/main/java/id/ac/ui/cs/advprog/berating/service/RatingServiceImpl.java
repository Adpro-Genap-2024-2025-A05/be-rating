package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.client.ExternalServiceClient;
import id.ac.ui.cs.advprog.berating.dto.*;
import id.ac.ui.cs.advprog.berating.exception.RatingException;
import id.ac.ui.cs.advprog.berating.model.Rating;
import id.ac.ui.cs.advprog.berating.enums.Role;
import id.ac.ui.cs.advprog.berating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ExternalServiceClient externalServiceClient;
    private final TokenVerificationService tokenVerificationService;

    @Override
    @Transactional
    public RatingResponseDto createRating(CreateRatingDto dto, String token) {
        TokenVerificationResponseDto verification = tokenVerificationService.verifyToken(token);
        if (verification.getRole() != Role.PACILIAN) {
            throw new RatingException("Only pacilians can create ratings");
        }
        
        UUID pacilianId = UUID.fromString(verification.getUserId());
        
        if (ratingRepository.existsByKonsultasiId(dto.getKonsultasiId())) {
            throw new RatingException("Rating already exists for this consultation");
        }
        
        KonsultasiResponseDto konsultasi = externalServiceClient.getKonsultasiById(
            dto.getKonsultasiId(), token);
        
        if (!konsultasi.getPacilianId().equals(pacilianId)) {
            throw new RatingException("You can only rate your own consultations");
        }
        
        if (!"DONE".equals(konsultasi.getStatus())) {
            throw new RatingException("You can only rate completed consultations");
        }
        
        String pacilianName = verification.getUserName();
        
        Rating rating = Rating.builder()
                .konsultasiId(dto.getKonsultasiId())
                .pacilianId(pacilianId)
                .caregiverId(konsultasi.getCaregiverId())
                .rating(dto.getRating())
                .review(dto.getReview())
                .pacilianName(pacilianName) 
                .build();
        
        Rating savedRating = ratingRepository.save(rating);
        return convertToResponseDto(savedRating);
    }

    @Override
    @Transactional
    public RatingResponseDto updateRating(UUID ratingId, UpdateRatingDto dto, String token) {
        TokenVerificationResponseDto verification = tokenVerificationService.verifyToken(token);
        if (verification.getRole() != Role.PACILIAN) {
            throw new RatingException("Only pacilians can update ratings");
        }

        UUID pacilianId = UUID.fromString(verification.getUserId());

        Rating rating = findRatingById(ratingId);

        if (!rating.getPacilianId().equals(pacilianId)) {
            throw new RatingException("You can only update your own ratings");
        }

        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());

        Rating updatedRating = ratingRepository.save(rating);
        return convertToResponseDto(updatedRating);
    }

    @Override
    @Transactional
    public void deleteRating(UUID ratingId, String token) {
        TokenVerificationResponseDto verification = tokenVerificationService.verifyToken(token);
        if (verification.getRole() != Role.PACILIAN) {
            throw new RatingException("Only pacilians can delete ratings");
        }

        UUID pacilianId = UUID.fromString(verification.getUserId());

        Rating rating = findRatingById(ratingId);

        if (!rating.getPacilianId().equals(pacilianId)) {
            throw new RatingException("You can only delete your own ratings");
        }

        ratingRepository.deleteById(ratingId);
    }

    @Override
    public List<RatingResponseDto> getRatingsByCaregiver(UUID caregiverId) {
        List<Rating> ratings = ratingRepository.findByCaregiverId(caregiverId);
        return ratings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingResponseDto> getRatingsByPacilian(UUID pacilianId, String token) {
        TokenVerificationResponseDto verification = tokenVerificationService.verifyToken(token);

        if (verification.getRole() == Role.PACILIAN) {
            UUID tokenPacilianId = UUID.fromString(verification.getUserId());
            if (!tokenPacilianId.equals(pacilianId)) {
                throw new RatingException("You can only view your own ratings");
            }
        }

        List<Rating> ratings = ratingRepository.findByPacilianId(pacilianId);
        return ratings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CaregiverRatingStatsDto getCaregiverRatingStats(UUID caregiverId) {
        List<Rating> ratings = ratingRepository.findByCaregiverId(caregiverId);
        Double averageRating = ratingRepository.getAverageRatingForCaregiver(caregiverId);
        Long totalRatings = ratingRepository.getTotalRatingsForCaregiver(caregiverId);

        List<RatingResponseDto> ratingDtos = ratings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return CaregiverRatingStatsDto.builder()
                .caregiverId(caregiverId)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalRatings(totalRatings != null ? totalRatings : 0L)
                .ratings(ratingDtos)
                .build();
    }

    @Override
    public RatingResponseDto getRatingById(UUID ratingId) {
        Rating rating = findRatingById(ratingId);
        return convertToResponseDto(rating);
    }

    private Rating findRatingById(UUID ratingId) {
        return ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingException("Rating not found"));
    }

    private RatingResponseDto convertToResponseDto(Rating rating) {
        return RatingResponseDto.builder()
                .id(rating.getId())
                .konsultasiId(rating.getKonsultasiId())
                .pacilianId(rating.getPacilianId())
                .caregiverId(rating.getCaregiverId())
                .rating(rating.getRating())
                .review(rating.getReview())
                .pacilianName(rating.getPacilianName()) 
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}