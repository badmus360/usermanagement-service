package com.fintech.usermanagement.request;

import lombok.Data;

@Data
public class CustomerLoginRequest {
    private String email;
    private String password;
}
