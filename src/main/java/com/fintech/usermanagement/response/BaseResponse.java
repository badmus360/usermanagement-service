package com.fintech.usermanagement.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {
    private Boolean flag;
    private String code;
    private String message;
    private T result;
}
