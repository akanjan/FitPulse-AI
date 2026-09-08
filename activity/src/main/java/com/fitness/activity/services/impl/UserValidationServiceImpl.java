package com.fitness.activity.services.impl;

import com.fitness.activity.services.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final WebClient userServiceWebClient;
    @Override
    public boolean validateUser(String userUID) {
        log.info("Calling User Validation API for userId: {}", userUID);
        try{
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userUID)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RuntimeException("User Not Found: " + userUID);
            else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Invalid Request: " + userUID);
        }
        return false;
    }
}
