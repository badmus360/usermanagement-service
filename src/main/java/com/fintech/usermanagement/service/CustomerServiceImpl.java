package com.fintech.usermanagement.service;

import com.fintech.usermanagement.entity.Account;
import com.fintech.usermanagement.entity.Customer;
import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.jwt.JwtUtils;
import com.fintech.usermanagement.repository.AccountRepository;
import com.fintech.usermanagement.repository.CustomerRepository;
import com.fintech.usermanagement.request.CustomerLoginRequest;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.response.LoginResponse;
import com.fintech.usermanagement.util.HttpService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;
    private final HttpService httpService;
    private final Gson gson;

    @Override
    public BaseResponse<CustomerOnboardingResponse> customerOnboarding(CustomerOnboardingRequest onboardingRequest, Channel channel) {
        // Check if customer exists
        Customer customer = customerRepository.getCustomerByEmail(onboardingRequest.getEmail()).orElse(null);
        if (customer != null) {
            return BaseResponse.<CustomerOnboardingResponse>builder()
                    .code("02")
                    .message("Customer already Exist")
                    .flag(false)
                    .build();
        }

        // Create and save the new customer first
        customer = new Customer();
        customer.setEmail(onboardingRequest.getEmail());
        customer.setBvn(onboardingRequest.getBvn());
        customer.setNin(onboardingRequest.getNin());
        customer.setAddress(onboardingRequest.getAddress());
        customer.setPassword(onboardingRequest.getPassword());

        // Set channel flags
        switch (channel) {
            case MOBILE -> customer.setIsMobileUser(true);
            case USSD -> customer.setIsUssdUser(true);
            case WEB -> customer.setIsWebUser(true);
        }

        // Save the customer first!
        customer = customerRepository.save(customer);

        log.info("Initiate Account Creation");
        String accountCreationUrl = "http://localhost:8091/api/account/create";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "secure-key-12345");

        try {
            String response = httpService.get(headers, accountCreationUrl).getBody();
            BaseResponse<?> baseResponse = gson.fromJson(response, BaseResponse.class);

            if (baseResponse.getResult() != null) {
                // Create and save the account
                Account account = new Account();
                account.setAccountName(onboardingRequest.getFirstName() + " " + onboardingRequest.getLastName());
                account.setAccountNo(baseResponse.getResult().toString());
                account.setCustomer(customer);  // Now customer is persisted
                account.setNuban(baseResponse.getResult().toString());
                account.setProductCode("101");  // Fixed typo from ProductName to ProductCode
                account.setProductName("INDIVIDUAL");
                account.setAvailableBalanceStr("3000000");
                account.setTier(3);

                accountRepository.save(account);

                return BaseResponse.<CustomerOnboardingResponse>builder()
                        .code("00")
                        .flag(true)
                        .message("Account Created Successfully")
                        .result(CustomerOnboardingResponse.builder()
                                .accountName(account.getAccountName())
                                .accountNumber(account.getAccountNo())
                                .accountTier("3")
                                .build())
                        .build();
            }
        } catch (Exception e) {
            log.error("Account creation failed", e);
        }

        return BaseResponse.<CustomerOnboardingResponse>builder()
                .code("02")
                .flag(false)  // Changed from true to false for error case
                .message("Account Can't Be Created at the moment")
                .build();
    }

    @Override
    public BaseResponse<LoginResponse> customerLogin(CustomerLoginRequest loginRequest) {
        log.info("Fetch Customer by Email");
        Customer customer = customerRepository.getCustomerByEmail(loginRequest.getEmail()).orElse(null);
        if (Objects.equals(customer, null)) {
            return BaseResponse.<LoginResponse>builder()
                    .code("02")
                    .message("Customer does not Exist")
                    .flag(false)
                    .build();
        }
        String token = jwtUtils.generateToken(loginRequest.getEmail());
        return BaseResponse.<LoginResponse>builder()
                    .code("00")
                    .flag(true)
                    .message("Customer Logged In")
                    .result(LoginResponse.builder()
                            .token(token)
                            .build())
                    .build();
    }
}
