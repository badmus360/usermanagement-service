package com.fintech.usermanagement.request;

import lombok.Data;

@Data
public class CustomerOnboardingRequest {
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String nin;
    private String bvn;
}
