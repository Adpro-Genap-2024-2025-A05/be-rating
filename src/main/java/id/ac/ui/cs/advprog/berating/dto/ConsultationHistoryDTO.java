package id.ac.ui.cs.advprog.berating.dto;

import java.time.LocalDateTime;

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
public class ConsultationHistoryDTO {
    private String id;
    private String userId;
    private String doctorId;
    private LocalDateTime date;
    private String notes;

}
