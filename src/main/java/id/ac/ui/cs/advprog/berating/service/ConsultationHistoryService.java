package id.ac.ui.cs.advprog.berating.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import id.ac.ui.cs.advprog.berating.dto.ConsultationHistoryDTO;
import id.ac.ui.cs.advprog.berating.exception.ConsultationHistoryNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationHistoryService {
    private final WebClient webClient;

    public ConsultationHistoryDTO getConsultationHistoryById(String id) {
        var response = webClient.get()
                .uri("/api/consultiation/" + id)
                .retrieve()
                .bodyToMono(ConsultationHistoryDTO.class)
                .block();

        if (response == null) {
            throw new ConsultationHistoryNotFoundException(id);
        }

        return response;
    }
}
