package com.fintech.usermanagement.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationRequest {
    private String number;
}
