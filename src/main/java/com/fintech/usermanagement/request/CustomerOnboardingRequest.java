package com.fintech.usermanagement.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOnboardingRequest {
    @NotBlank(message = "input a valid email")
    private String email;
    @NotBlank(message = "input a valid First Name")
    private String firstName;
    @NotBlank(message = "input a valid Middle Name")
    private String middleName;
    @NotBlank(message = "input a valid Last Name")
    private String lastName;
    @NotBlank(message = "input a valid Address")
    private String address;
    @NotBlank(message = "input a valid Phone Number")
    private String phoneNumber;
    @NotBlank(message = "input a valid NIN")
    private String nin;
    @NotBlank(message = "input a valid BVN")
    private String bvn;
}
