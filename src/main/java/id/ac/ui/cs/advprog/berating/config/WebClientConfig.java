package id.ac.ui.cs.advprog.berating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class WebClientConfig {
    
    private String loadProfileUrl() {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            return dotenv.get("PROFILE_SERVICE_URL");
        } catch (Exception e) {
            return System.getenv("PROFILE_SERVICE_URL");
        }
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(loadProfileUrl())
                .build();
    }
} 