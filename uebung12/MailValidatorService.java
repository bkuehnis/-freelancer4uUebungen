package ch.zhaw.freelancer4u.service;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import ch.zhaw.freelancer4u.model.MailInformation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailValidatorService {
    private final WebClient disifyClient;

    public boolean isValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        MailInformation result;
        try {
            result = disifyClient.post()
                    .uri("/api/email")
                    .body(BodyInserters.fromFormData("email", email))
                    .retrieve()
                    .bodyToMono(MailInformation.class)
                    .block(Duration.ofSeconds(5));
        } catch (RuntimeException failure) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email validation is unavailable.");
        }

        if (result == null || result.format() == null || result.isDnsIndeterminate()
                || (result.format() && (result.disposable() == null || result.dns() == null))) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email validation is unavailable.");
        }
        return result.isValid();
    }
}
