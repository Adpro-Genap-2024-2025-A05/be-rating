package id.ac.ui.cs.advprog.berating.model;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(name="patient_id", nullable = false)
    private UUID patientId;

    @Column(name="doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name="rating", nullable = false)
    private Integer rating;

    @Column(name="comment", nullable = false, length = 500)
    private String comment;

    @Column(name="status", nullable = false)
    private ReviewStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
