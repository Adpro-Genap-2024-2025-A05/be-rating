package id.ac.ui.cs.advprog.berating.service;

import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.reactive.function.client.WebClient;

import id.ac.ui.cs.advprog.berating.dto.DoctorDTO;
import id.ac.ui.cs.advprog.berating.dto.UserDTO;


@Service
public class UserService {
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

    public UserService(WebClient.Builder webClientBuilder) {
        this.profileClient = webClientBuilder.baseUrl(profileUrl).build();
    }

    public UserDTO getUserLogin(String token, String id) {
        var response = profileClient.get()
                .uri("/api/users/" + id )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to fetch user data");
        }
        return response;
    }

    public DoctorDTO getDoctorById(String token, String id) {
        var response = profileClient.get()
                .uri("/api/doctors/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(DoctorDTO.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to fetch doctor data");
        }

        return response;
    }
}
