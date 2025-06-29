# User Management Service API

📌 Overview
A RESTful API for customer onboarding, authentication, and account management with integrated BVN/NIN verification. Provides complete customer lifecycle management for financial applications.

🚀 Key Features
- Customer onboarding with identity verification (BVN/NIN)
- Multi-channel registration (WEB, MOBILE, USSD)
- JWT-based authentication
- Account creation and management
- Secure API endpoints

🔐 Authentication
- Onboarding/Login: API key authentication
- Post-Login: JWT token authentication

Required Headers:
X-API-KEY: your-secret-key
Authorization: Bearer your-jwt-token

🌐 API Endpoints

1. Customer Onboarding
Endpoint: POST /customer/onboard/{channel}

Example Request:

POST /customer/onboard/MOBILE
X-API-KEY: secure-key-12345
Content-Type: application/json

{
  "email": "user@example.com",
  "firstName": "John",
  "middleName": "Middle",
  "lastName": "Doe",
  "address": "123 Main St",
  "phoneNumber": "08012345678",
  "nin": "42937562241",
  "bvn": "59220822994",
  "password": "securePassword123"
}

Success Response:
{
  "flag": true,
  "code": "00",
  "message": "Account created successfully",
  "result": {
    "accountNumber": "1234567890",
    "accountName": "John Doe",
    "accountTier": "3"
  }
}

Error Response (Already Exists):
{
  "flag": false,
  "code": "02",
  "message": "Customer already exists"
}

Error Response (BVN Mismatch):
{
  "flag": false,
  "code": "02",
  "message": "Phone number does not match BVN record"
}

2. Customer Login
Endpoint: POST /customer/login

Example Request:

POST /customer/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}

Success Response:
{
  "flag": true,
  "code": "00",
  "message": "Customer Logged In",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}

Error Response:
{
  "flag": false,
  "code": "02",
  "message": "Customer does not exist"
}

---

🧪 RAW CURL TEST CASES

# ✅ Customer Onboarding (Successful)
curl -X POST http://localhost:8090/customer/onboard/MOBILE \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: secure-key-12345" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "middleName": "Middle",
    "lastName": "Doe",
    "address": "123 Main St",
    "phoneNumber": "08012345678",
    "nin": "42937562241",
    "bvn": "59220822994",
    "password": "securePassword123"
}'

# ❌ Customer Onboarding (Already Exists)
curl -X POST http://localhost:8090/customer/onboard/MOBILE \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: secure-key-12345" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "middleName": "Middle",
    "lastName": "Doe",
    "address": "123 Main St",
    "phoneNumber": "08012345678",
    "nin": "42937562241",
    "bvn": "59220822994",
    "password": "securePassword123"
}'

# ❌ Customer Onboarding (Phone and BVN Mismatch)
curl -X POST http://localhost:8090/customer/onboard/MOBILE \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: secure-key-12345" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "middleName": "Middle",
    "lastName": "Doe",
    "address": "123 Main St",
    "phoneNumber": "08100000000",
    "nin": "42937562241",
    "bvn": "59220822994",
    "password": "securePassword123"
}'

# ✅ Customer Login (Successful)
curl -X POST http://localhost:8090/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
}'

# ❌ Customer Login (Invalid Email)
curl -X POST http://localhost:8090/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "wrongPassword"
}'

---

🛠️ Technical Implementation

Request Models

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

public class CustomerLoginRequest {
    private String email;
    private String password;
}

Response Models

public class BaseResponse<T> {
    private Boolean flag;
    private String code;
    private String message;
    private T result;
}

public class CustomerOnboardingResponse {
    private String accountNumber;
    private String accountName;
    private String accountTier;
}

---

🔧 Configuration

application.properties

spring.application.name=usermanagement  
server.port=8090  
app.secret-key=secure-key-12345  
jwt.secret=your-jwt-secret  
jwt.expiration=86400000  # 24 hours  

---

🧪 Testing Valid Credentials

Field       Test Values  
BVN         59220822994, 59220822888  
NIN         42937562241, 42937562333  
Email       Any valid email format  
Password    Minimum 8 characters  

---

📊 Response Codes

Code  Meaning  
00    Success  
02    Validation failure  
03    Unauthorized  
06    System error  
