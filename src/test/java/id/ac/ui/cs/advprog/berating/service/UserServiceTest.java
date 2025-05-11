package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.DoctorDTO;
import id.ac.ui.cs.advprog.berating.dto.UserDTO;
import id.ac.ui.cs.advprog.berating.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UserService userService;

    private String token;
    private String userId;
    private UserDTO userDTO;
    private DoctorDTO doctorDTO;

    @BeforeEach
    void setUp() {
        token = "valid.jwt.token";
        userId = "123";
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setRole(Role.PACILLIANS);
        doctorDTO = new DoctorDTO();
        doctorDTO.setId(userId);
        doctorDTO.setName("Dr. Test");
        doctorDTO.setPracticeAddress("123 Test St");
        doctorDTO.setWorkSchedule("Mon-Fri 9-5");
        doctorDTO.setEmail("dr.test@example.com");
        doctorDTO.setPhoneNumber("1234567890");
        doctorDTO.setRating(4.5);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq("Authorization"), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // POSITIVE CASES

    @Test
    void getUserLogin_ShouldReturnUserDTO() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

        UserDTO result = userService.getUserLogin(token, userId);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getRole(), result.getRole());
        verify(webClient.get()).uri("/api/users/" + userId);
        verify(requestHeadersSpec).header("Authorization", "Bearer " + token);
    }

    @Test
    void getDoctorById_ShouldReturnDoctorDTO() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(doctorDTO));

        DoctorDTO result = userService.getDoctorById(token, userId);

        assertNotNull(result);
        assertEquals(doctorDTO.getId(), result.getId());
        assertEquals(doctorDTO.getName(), result.getName());
        assertEquals(doctorDTO.getPracticeAddress(), result.getPracticeAddress());
        assertEquals(doctorDTO.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(doctorDTO.getEmail(), result.getEmail());
        assertEquals(doctorDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(doctorDTO.getRating(), result.getRating());
        verify(webClient.get()).uri("/api/doctors/" + userId);
        verify(requestHeadersSpec).header("Authorization", "Bearer " + token);
    }

    // NEGATIVE CASES

    @Test
    void getUserLogin_WhenResponseIsNull_ShouldThrowException() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserLogin(token, userId));
    }

    @Test
    void getDoctorById_WhenResponseIsNull_ShouldThrowException() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> userService.getDoctorById(token, userId));
    }

    // CORNER CASES

    @Test
    void getUserLogin_WithEmptyToken_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

        UserDTO result = userService.getUserLogin("", userId);

        assertNotNull(result);
        verify(requestHeadersSpec).header("Authorization", "Bearer ");
    }

    @Test
    void getDoctorById_WithEmptyUserId_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(doctorDTO));

        DoctorDTO result = userService.getDoctorById(token, "");

        assertNotNull(result);
        verify(webClient.get()).uri("/api/doctors/");
    }
} 