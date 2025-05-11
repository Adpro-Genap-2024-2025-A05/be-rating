package id.ac.ui.cs.advprog.berating.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import id.ac.ui.cs.advprog.berating.dto.DoctorDTO;
import id.ac.ui.cs.advprog.berating.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;

    public UserDTO getUserLogin(String token, String id) {
        var response = webClient.get()
                .uri("/api/users/" + id)
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
        var response = webClient.get()
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