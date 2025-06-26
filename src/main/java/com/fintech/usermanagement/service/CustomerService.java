package com.fintech.usermanagement.service;


import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.request.CustomerLoginRequest;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.response.LoginResponse;

public interface CustomerService {
    BaseResponse<CustomerOnboardingResponse> customerOnboarding(CustomerOnboardingRequest request, Channel channel);
    BaseResponse<LoginResponse> customerLogin(CustomerLoginRequest loginRequest);
}
