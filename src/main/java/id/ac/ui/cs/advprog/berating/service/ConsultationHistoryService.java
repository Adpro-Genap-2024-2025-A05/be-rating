package id.ac.ui.cs.advprog.berating.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import id.ac.ui.cs.advprog.berating.dto.ConsultationHistoryDTO;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ConsultationHistoryService {
    private String loadProfileUrl() {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            return dotenv.get("PROFILE_SERVICE_URL");
        } catch (Exception e) {
            return System.getenv("PROFILE_SERVICE_URL");
        }
    }

    private final String profileUrl = loadProfileUrl();
    private final WebClient profileClient;

    public ConsultationHistoryService(WebClient.Builder webClientBuilder) {
        this.profileClient = webClientBuilder.baseUrl(profileUrl).build();
    }

    public ConsultationHistoryDTO getConsultationHistoryById(String id) {
        var response = profileClient.get()
                .uri("/api/consultiation/" + id)
                .retrieve()
                .bodyToMono(ConsultationHistoryDTO.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to fetch user data");
        }

        return response;
    }
}
