package id.ac.ui.cs.advprog.berating.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Review> reviewsAsPatient;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Review> reviewsAsDoctor;
}
