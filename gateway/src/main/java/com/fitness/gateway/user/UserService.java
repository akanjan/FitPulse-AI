package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userUID) {
        log.info("Calling User Validation API for userId: {}", userUID);
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userUID)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.just(false)) // user not found
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("Unexpected error during validation: {}", e.getMessage());
                        return Mono.error(new RuntimeException("User validation failed"));
                    });
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling User Registration API for email: {}", request.getEmail());

        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        e -> Mono.error(new RuntimeException("Bad Request: " + e.getResponseBodyAsString())))
                .onErrorResume(WebClientResponseException.InternalServerError.class,
                        e -> Mono.error(new RuntimeException("Internal Server Error: " + e.getResponseBodyAsString())))
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.error(new RuntimeException("Unexpected error: " + e.getResponseBodyAsString())));
    }

}
