package id.ac.ui.cs.advprog.berating.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ReviewRequest {
    @NotNull(message = "Patient ID cannot be null")
    private UUID patientId;
    
    @NotNull(message = "Doctor ID cannot be null")
    private UUID doctorId;

    @NotNull(message = "Rating cannot be null")
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 500, message = "Comment cannot be more than 500 characters")
    private String comment;
}
