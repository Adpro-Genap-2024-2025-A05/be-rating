package id.ac.ui.cs.advprog.berating.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import id.ac.ui.cs.advprog.berating.dto.UserDTO;
import id.ac.ui.cs.advprog.berating.dto.DoctorDTO;
import id.ac.ui.cs.advprog.berating.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.berating.exception.DoctorNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;

    public UserDTO getUserLogin(String token, String userId) {
        var response = webClient.get()
                .uri("/api/users/" + userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();

        if (response == null) {
            throw new UserNotFoundException(userId);
        }

        return response;
    }

    public DoctorDTO getDoctorById(String token, String doctorId) {
        var response = webClient.get()
                .uri("/api/doctors/" + doctorId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(DoctorDTO.class)
                .block();

        if (response == null) {
            throw new DoctorNotFoundException(doctorId);
        }

        return response;
    }
}