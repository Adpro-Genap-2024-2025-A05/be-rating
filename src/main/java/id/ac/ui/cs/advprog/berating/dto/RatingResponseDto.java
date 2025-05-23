package id.ac.ui.cs.advprog.berating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {
    private UUID id;
    private UUID konsultasiId;
    private UUID pacilianId;
    private UUID caregiverId;
    private Integer rating;
    private String review;
    private String pacilianName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}