package com.fintech.usermanagement.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BvnResponse {
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
        private String bvnNumber;
        private String nameOnCard;
        private String enrolmentBranch;
        private String enrolmentBank;
        private String formattedRegistrationDate;
        private String levelOfAccount;
        private String nin;
        private String watchListed;
        private String verificationStatus;
        private String serviceType;
        private PersonalInfo personalInfo;
        private ResidentialInfo residentialInfo;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class PersonalInfo {
            private String firstName;
            private String middleName;
            private String lastName;
            private String fullName;
            private String email;
            private String gender;
            private String phoneNumber;
            private String phoneNumber2;
            private String dateOfBirth;
            private String lgaOfOrigin;
            private String stateOfOrigin;
            private String nationality;
            private String maritalStatus;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class ResidentialInfo {
            private String stateOfResidence;
            private String lgaOfResidence;
            private String residentialAddress;
        }
    }
}

