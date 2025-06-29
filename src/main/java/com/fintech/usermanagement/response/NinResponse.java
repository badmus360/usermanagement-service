package com.fintech.usermanagement.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NinResponse {
    private String statusCode;
    private String status;
    private String message;
    private Result result;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Result {
        private String requestReference;
        private String ninNumber;
        private String documentNo;
        private String trackingId;
        private String centralId;
        private String verificationStatus;
        private String serviceType;
        private PersonalInfo personalInfo;
        private NextOfKin nextOfKin;
        private ResidentialInfo residentialInfo;
        private IndigeneInfo indigeneInfo;
        private String documentUrl;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalInfo {
        private String title;
        private String firstName;
        private String middleName;
        private String lastName;
        private String fullName;
        private String maidenName;
        private String gender;
        private String email;
        private String phoneNumber;
        private String dateOfBirth;
        private String height;
        private String maritalStatus;
        private String religion;
        private String signature;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NextOfKin {
        private String firstname;
        private String surname;
        private String address;
        private String lga;
        private String town;
        private String state;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResidentialInfo {
        private String address;
        private String lgaOfResidence;
        private String stateOfResidence;
        private String residenceStatus;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndigeneInfo {
        private String lgaOfOrigin;
        private String placeOfOrigin;
        private String stateOfOrigin;
    }
}
