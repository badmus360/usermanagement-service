package com.fintech.usermanagement.controller;


import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.jwt.JwtUtils;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    @PostMapping("/onboard/{channel}")
    public BaseResponse<CustomerOnboardingResponse> customerOnboarding(@RequestBody @Valid CustomerOnboardingRequest request, @PathVariable Channel channel) {
        try {
            // Your existing onboarding logic
            return customerService.customerOnboarding(request, channel);
        } catch (Exception e) {
            return BaseResponse.<CustomerOnboardingResponse>builder()
                    .code("05")
                    .flag(false)
                    .message("Onboarding failed: " + e.getMessage())
                    .build();
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody CustomerLoginRequest loginRequest) {
//
//    }

}
