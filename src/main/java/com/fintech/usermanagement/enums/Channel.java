package com.fintech.usermanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Channel {
    USSD, MOBILE, WEB;

    @JsonCreator
    public static Channel fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
