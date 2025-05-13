package id.ac.ui.cs.advprog.berating.model;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
@Builder
public class Review {
    @Id
    private UUID id;

    @Column(name="patient_id", nullable = false)
    private UUID patientId;

    @Column(name="doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name="consultation_id", nullable = false)
    private UUID consultationId;

    @Min(value = 1, message = "version must be at least 1")
    @Column(name="version", nullable = false)
    private Integer version;

    @Column(name="parent_id")
    private UUID parentId;

    @Column(name="is_current_version", nullable = false)
    private Boolean isCurrentVersion;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name="rating", nullable = false)
    private Integer rating;

    @Column(name="comment", length = 500)
    private String comment;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (version == null) {
            version = 1;
        }
        if (isCurrentVersion == null) {
            isCurrentVersion = true;
        }
        if (parentId == null) {
            parentId = id;  // For the first version, parentId is same as id
        }
    }
}
