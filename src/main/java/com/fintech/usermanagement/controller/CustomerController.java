package com.fintech.usermanagement.controller;


import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.jwt.JwtUtils;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    @PostMapping("/onboard/{channel}")
    public BaseResponse<CustomerOnboardingResponse> customerOnboarding(@RequestBody CustomerOnboardingRequest request, @PathVariable Channel channel) {
        return customerService.customerOnboarding(request, channel);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody CustomerLoginRequest loginRequest) {
//
//    }

}
