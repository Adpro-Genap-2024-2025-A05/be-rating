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
import static org.mockito.ArgumentMatchers.anyString;
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

    private UserDTO userDTO;
    private DoctorDTO doctorDTO;
    private String token;
    private String doctorId;

    @BeforeEach
    void setUp() {
        token = "valid.jwt.token";
        doctorId = "doctor123";

        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setRole(Role.PACILLIANS);

        doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctorId);
        doctorDTO.setName("Dr. Test");
        doctorDTO.setPracticeAddress("123 Test St");
        doctorDTO.setWorkSchedule("Mon-Fri 9-5");
        doctorDTO.setEmail("dr.test@example.com");
        doctorDTO.setPhoneNumber("1234567890");
        doctorDTO.setRating(4.5);

        // Setup WebClient mock chain
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // POSITIVE CASES

    @Test
    void getUserLogin_WithValidToken_ShouldReturnUserDTO() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

        UserDTO result = userService.getUserLogin(token);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getRole(), result.getRole());
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/users/");
        verify(requestHeadersSpec).header("Authorization", "Bearer " + token);
    }

    @Test
    void getDoctorById_WithValidId_ShouldReturnDoctorDTO() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(doctorDTO));

        DoctorDTO result = userService.getDoctorById(doctorId);

        assertNotNull(result);
        assertEquals(doctorDTO.getId(), result.getId());
        assertEquals(doctorDTO.getName(), result.getName());
        assertEquals(doctorDTO.getPracticeAddress(), result.getPracticeAddress());
        assertEquals(doctorDTO.getWorkSchedule(), result.getWorkSchedule());
        assertEquals(doctorDTO.getEmail(), result.getEmail());
        assertEquals(doctorDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(doctorDTO.getRating(), result.getRating());
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/doctors/" + doctorId);
    }

    // NEGATIVE CASES

    @Test
    void getUserLogin_WhenResponseIsNull_ShouldThrowException() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserLogin(token));
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/users/");
    }

    @Test
    void getDoctorById_WhenResponseIsNull_ShouldThrowException() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> userService.getDoctorById(doctorId));
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/api/doctors/" + doctorId);
    }

    // CORNER CASES

    @Test
    void getUserLogin_WithEmptyToken_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

        UserDTO result = userService.getUserLogin("");

        assertNotNull(result);
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(requestHeadersSpec).header("Authorization", "Bearer ");
    }

    @Test
    void getDoctorById_WithEmptyId_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(doctorDTO));

        DoctorDTO result = userService.getDoctorById("");

        assertNotNull(result);
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(requestHeadersUriSpec).uri("/api/doctors/");
    }

    @Test
    void getUserLogin_WithNullToken_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));

        UserDTO result = userService.getUserLogin(null);

        assertNotNull(result);
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(requestHeadersSpec).header("Authorization", "Bearer null");
    }

    @Test
    void getDoctorById_WithNullId_ShouldMakeRequest() {
        when(responseSpec.bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(doctorDTO));

        DoctorDTO result = userService.getDoctorById(null);

        assertNotNull(result);
        verify(webClientBuilder).baseUrl(anyString());
        verify(webClientBuilder).build();
        verify(requestHeadersUriSpec).uri("/api/doctors/null");
    }
} 