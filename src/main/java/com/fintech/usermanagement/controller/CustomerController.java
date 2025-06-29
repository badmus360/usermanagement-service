package com.fintech.usermanagement.controller;

import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.request.CustomerLoginRequest;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.response.LoginResponse;
import com.fintech.usermanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(path = "/onboard/{channel}", consumes = "application/json")
    public ResponseEntity<BaseResponse<CustomerOnboardingResponse>> customerOnboarding(
            @RequestBody @Valid CustomerOnboardingRequest request,
            @PathVariable Channel channel
    ) {
        log.info("Customer onboarding request for channel: {}", channel);
        BaseResponse<CustomerOnboardingResponse> response = customerService.customerOnboarding(request, channel);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @RequestBody @Valid CustomerLoginRequest loginRequest
    ) {
        log.info("Login request for email: {}", loginRequest.getEmail());
        BaseResponse<LoginResponse> response = customerService.customerLogin(loginRequest);
        return ResponseEntity.ok(response);
    }
}