package id.ac.ui.cs.advprog.berating.model;

import id.ac.ui.cs.advprog.berating.model.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    private int rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    private LocalDateTime createdAt;
}
