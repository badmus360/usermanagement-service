package com.fintech.usermanagement.service;

import com.fintech.usermanagement.entity.Account;
import com.fintech.usermanagement.entity.Customer;
import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.jwt.JwtUtils;
import com.fintech.usermanagement.repository.AccountRepository;
import com.fintech.usermanagement.repository.CustomerRepository;
import com.fintech.usermanagement.request.CustomerLoginRequest;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.request.VerificationRequest;
import com.fintech.usermanagement.response.*;
import com.fintech.usermanagement.util.HttpService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        try {

            if (onboardingRequest == null) {
                return buildErrorResponse("01", "Request payload cannot be null");
            }

            List<String> missingFields = new ArrayList<>();
            if (onboardingRequest.getEmail() == null || onboardingRequest.getEmail().isBlank()) missingFields.add("email");
            if (onboardingRequest.getFirstName() == null || onboardingRequest.getFirstName().isBlank()) missingFields.add("firstName");
            if (onboardingRequest.getMiddleName() == null || onboardingRequest.getMiddleName().isBlank()) missingFields.add("middleName");
            if (onboardingRequest.getLastName() == null || onboardingRequest.getLastName().isBlank()) missingFields.add("lastName");
            if (onboardingRequest.getAddress() == null || onboardingRequest.getAddress().isBlank()) missingFields.add("address");
            if (onboardingRequest.getPhoneNumber() == null || onboardingRequest.getPhoneNumber().isBlank()) missingFields.add("phoneNumber");
            if (onboardingRequest.getNin() == null || onboardingRequest.getNin().isBlank()) missingFields.add("NIN");
            if (onboardingRequest.getBvn() == null || onboardingRequest.getBvn().isBlank()) missingFields.add("BVN");
            if (onboardingRequest.getPassword() == null || onboardingRequest.getPassword().isBlank()) missingFields.add("password");

            if (!missingFields.isEmpty()) {
                return buildErrorResponse("01", "Missing required fields: " + String.join(", ", missingFields));
            }

            // Validate field formats
            if (!onboardingRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return buildErrorResponse("01", "Invalid email format");
            }

            if (!onboardingRequest.getPhoneNumber().matches("^[0-9]{11}$")) {
                return buildErrorResponse("01", "Phone number must be 11 digits");
            }

            if (!onboardingRequest.getNin().matches("^[0-9]{11}$")) {
                return buildErrorResponse("01", "NIN must be 11 digits");
            }

            if (!onboardingRequest.getBvn().matches("^[0-9]{11}$")) {
                return buildErrorResponse("01", "BVN must be 11 digits");
            }

            Customer customerResponse = customerRepository.getCustomerByEmail(onboardingRequest.getEmail()).orElse(null);
            if (customerResponse != null) {
                return BaseResponse.<CustomerOnboardingResponse>builder()
                        .code("02")
                        .message("Customer already Exist")
                        .flag(false)
                        .build();
            }

            BvnResponse bvnResponse = verifyBvn(onboardingRequest);
            if (bvnResponse == null || !"200".equals(bvnResponse.getStatusCode())) {
                return buildErrorResponse("02", bvnResponse != null ? bvnResponse.getMessage() : "BVN verification failed");
            }

            if (!onboardingRequest.getPhoneNumber().equals(bvnResponse.getResult().getPersonalInfo().getPhoneNumber())) {
                return buildErrorResponse("02", "Phone number does not match BVN record");
            }

            NinResponse ninResponse = verifyNin(onboardingRequest);
            log.info("NinResponse :: {}", gson.toJson(ninResponse));
            if (ninResponse == null || !"200".equals(ninResponse.getStatusCode())) {
                return buildErrorResponse("02", ninResponse != null ? ninResponse.getMessage() : "NIN verification failed");
            }

            if (!onboardingRequest.getPhoneNumber().equals(ninResponse.getResult().getPersonalInfo().getPhoneNumber())) {
                return buildErrorResponse("02", "Phone number does not match NIN record");
            }

            BaseResponse<?> accountResponse = createAccount();
            if (accountResponse == null || !"00".equals(accountResponse.getCode())) {
                return buildErrorResponse("02", "Account creation failed");
            }

            Customer customer = createCustomer(onboardingRequest, channel);
            customer = customerRepository.save(customer);

            Account account = createAccountEntity(onboardingRequest, accountResponse, customer);
            accountRepository.save(account);

            return buildSuccessResponse(account);

        } catch (Exception e) {
            log.error("Customer onboarding failed: {}", e.getMessage(), e);
            return buildErrorResponse("06", "System error during onboarding");
        }
    }

    private BvnResponse verifyBvn(CustomerOnboardingRequest request) {
        HttpHeaders headers = createHeaders();
        String bvnUrl = "http://localhost:8096/api/identity/bvn";
        log.info("Initiating BVN verification");
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setNumber(request.getBvn());

        try {
            String response = httpService.post(verificationRequest, headers, bvnUrl).getBody();
            return gson.fromJson(response, BvnResponse.class);
        } catch (Exception e) {
            log.error("BVN verification failed: {}", e.getMessage());
            return null;
        }
    }

    private NinResponse verifyNin(CustomerOnboardingRequest request) {
        HttpHeaders headers = createHeaders();
        String ninUrl = "http://localhost:8096/api/identity/nin";
        log.info("Initiating NIN verification");
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setNumber(request.getNin());

        try {
            String response = httpService.post(verificationRequest, headers, ninUrl).getBody();
            return gson.fromJson(response, NinResponse.class);
        } catch (Exception e) {
            log.error("NIN verification failed: {}", e.getMessage());
            return null;
        }
    }

    private BaseResponse<?> createAccount() {
        HttpHeaders headers = createHeaders();
        String accountUrl = "http://localhost:8091/api/account/create";
        log.info("Initiating account creation");

        try {
            String response = httpService.get(headers, accountUrl).getBody();
            return gson.fromJson(response, BaseResponse.class);
        } catch (Exception e) {
            log.error("Account creation failed: {}", e.getMessage());
            return null;
        }
    }

    private Customer createCustomer(CustomerOnboardingRequest request, Channel channel) {
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setBvn(request.getBvn());
        customer.setNin(request.getNin());
        customer.setAddress(request.getAddress());
        customer.setPassword(request.getPassword());

        switch (channel) {
            case MOBILE -> customer.setIsMobileUser(true);
            case USSD -> customer.setIsUssdUser(true);
            case WEB -> customer.setIsWebUser(true);
        }

        return customer;
    }

    private Account createAccountEntity(CustomerOnboardingRequest request, BaseResponse<?> response, Customer customer) {
        Account account = new Account();
        account.setAccountName(request.getFirstName() + " " + request.getLastName());
        account.setAccountNo(response.getResult().toString());
        account.setCustomer(customer);
        account.setNuban(response.getResult().toString());
        account.setProductCode("101");
        account.setProductName("INDIVIDUAL");
        account.setAvailableBalanceStr("3000000");
        account.setTier(3);
        return account;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "secure-key-12345");
        return headers;
    }

    private <T> BaseResponse<T> buildErrorResponse(String code, String message) {
        return BaseResponse.<T>builder()
                .code(code)
                .message(message)
                .flag(false)
                .build();
    }

    private BaseResponse<CustomerOnboardingResponse> buildSuccessResponse(Account account) {
        return BaseResponse.<CustomerOnboardingResponse>builder()
                .code("00")
                .flag(true)
                .message("Account created successfully")
                .result(CustomerOnboardingResponse.builder()
                        .accountName(account.getAccountName())
                        .accountNumber(account.getAccountNo())
                        .accountTier("3")
                        .build())
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
        if (!Objects.equals(loginRequest.getPassword(), customer.getPassword())) {
            return BaseResponse.<LoginResponse>builder()
                    .code("02")
                    .message("Incorrect Password")
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
