package com.fintech.usermanagement.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOnboardingResponse {
    private String accountNumber;
    private String accountName;
    private String accountTier;
}
