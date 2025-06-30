package com.fintech.usermanagement.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOnboardingRequest {
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String nin;
    private String bvn;
    private String password;
}
