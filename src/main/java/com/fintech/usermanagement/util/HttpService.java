package com.fintech.usermanagement.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HttpService {

    private final WebClient webClient;

    public ResponseEntity<String> post(Object requestBody, HttpHeaders httpHeaders, String url) {
        return webClient.post()
                .uri(url)
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .toEntity(String.class).timeout(Duration.ofSeconds(120), Mono.error(() -> {
                    try {
                        throw new Exception("Request Timeout, please try again");
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })).block();
    }

    public ResponseEntity<String> get(HttpHeaders httpHeaders, String url) {
        return webClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .toEntity(String.class).timeout(Duration.ofSeconds(120), Mono.error(() -> {
                    try {
                        throw new Exception("Request Timeout, please try again");
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })).block();
    }

//    public ResponseEntity<String> put(Object requestBody, HttpHeaders httpHeaders, String url) {
//        return webClient.put()
//                .uri(url)
//                .body(BodyInserters.fromValue(requestBody))
//                .headers(headers -> headers.addAll(httpHeaders))
//                .retrieve()
//                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
//                .toEntity(String.class).timeout(Duration.ofSeconds(120), Mono.error(() -> {
//                    try {
//                        throw new Exception("Request Timeout, please try again");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e.getMessage());
//                    }
//                })).block();
//    }
}
