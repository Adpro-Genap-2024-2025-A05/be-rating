package id.ac.ui.cs.advprog.berating.dto;

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
public class DoctorDTO {
    private String id;
    private String name;
    private String practiceAddress;
    private String workSchedule;
    private String email;
    private String phoneNumber;
    private double rating;
}
