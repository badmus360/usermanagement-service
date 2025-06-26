package com.fintech.usermanagement.service;

import com.fintech.usermanagement.repository.CustomerRepository;
import com.fintech.usermanagement.entity.Account;
import com.fintech.usermanagement.entity.Customer;
import com.fintech.usermanagement.enums.Channel;
import com.fintech.usermanagement.jwt.JwtUtils;
import com.fintech.usermanagement.repository.AccountRepository;
import com.fintech.usermanagement.request.CustomerLoginRequest;
import com.fintech.usermanagement.request.CustomerOnboardingRequest;
import com.fintech.usermanagement.response.BaseResponse;
import com.fintech.usermanagement.response.CustomerOnboardingResponse;
import com.fintech.usermanagement.response.LoginResponse;
import com.fintech.usermanagement.util.HttpService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.AbstractCollection;
import java.util.Objects;

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
        BaseResponse<?> baseResponse = new BaseResponse<>();
        Customer customer = customerRepository.getCustomerByEmail(onboardingRequest.getEmail()).orElse(null);
        if (!Objects.equals(customer, null)) {
            return BaseResponse.<CustomerOnboardingResponse>builder()
                    .code("02")
                    .message("Customer already Exist")
                    .flag(false)
                    .build();
        }
        customer = new Customer();
        customer.setEmail(onboardingRequest.getEmail());
        customer.setBvn(onboardingRequest.getBvn());
        customer.setNin(onboardingRequest.getNin());
        customer.setAddress(onboardingRequest.getAddress());
        customer.setNin(onboardingRequest.getNin());
        customer.setBvn(onboardingRequest.getBvn());
        switch (channel) {
            case MOBILE ->
                customer.setIsMobileUser(true);
            case USSD ->
                customer.setIsUssdUser(true);
            case WEB ->
                customer.setIsWebUser(true);
        }

        String accountCreationUrl = "";
        String response = httpService.get(new HttpHeaders(), accountCreationUrl).getBody();
        baseResponse = gson.fromJson(response, BaseResponse.class);
        if (!Objects.equals(baseResponse.getResult(), null)) {
            Account account = accountRepository.findByEmail(onboardingRequest.getEmail()).orElse(null);
            if (Objects.equals(account, null)) {
                account = new Account();
                account.setAccountName(onboardingRequest.getFirstName()+" "+onboardingRequest.getLastName());
                account.setAccountNo(baseResponse.getResult().toString());
                account.setCustomer(customer);
                account.setNuban(baseResponse.getResult().toString());
                account.setProductName("101");
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
        }
        return BaseResponse.<CustomerOnboardingResponse>builder()
                .code("02")
                .flag(true)
                .message("Account Can't Be Created at the moment")
                .result(null)
                .build();
    }

    @Override
    public BaseResponse<LoginResponse> customerLogin(CustomerLoginRequest loginRequest) {
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
