# Simplify Money: Referral Tracking System

A Java-based backend system for managing user signups with referral code tracking to incentivize successful referrals. The referral system ensures that a referral is only considered successful when the referred user completes their profile.

---

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [API Endpoints](#api-endpoints)
    - [User API](#user-api)
    - [CSV Report API](#csv-report-api)
- [Database Schema](#database-schema)
- [Setup & Deployment](#setup--deployment)
- [Sample Curl Requests](#sample-curl-requests)
- [Author](#author)

---

## Introduction

This project implements a **Referral Tracking System** that allows users to sign up, optionally using a referral code. Users are rewarded for successful referrals only after the referred user completes their profile. It also includes a feature to generate CSV reports for all users and their referral details.

---

## Features

1. **User Signup**:
    - Users can sign up with or without a referral code.
    - Generates a unique referral code for each user.
    - Validates the referral code during signup.

2. **Referral Tracking**:
    - Tracks and links referrer and referred users.
    - Marks referrals as successful upon profile completion.

3. **CSV Report**:
    - Generates a CSV file containing all users and their referral details.

4. **Swagger Integration**:
    - API documentation is accessible via Swagger UI.

5. **Database Support**:
    - Uses MongoDB for database operations.

---

## Technologies Used

- **Java**: Spring Boot Framework.
- **Database**: MongoDB.
- **Validation**: Java Bean Validation (Jakarta).
- **Dependency Management**: Maven.
- **Documentation**: Swagger (OpenAPI).
- **CSV Generation**: Apache Commons CSV.
- **Testing**: JUnit.
- **Deployment**: AWS Elastic Beanstalk.

---

## API Endpoints

### Base URL:
[http://smtask.ap-south-1.elasticbeanstalk.com/](http://smtask.ap-south-1.elasticbeanstalk.com/)

### User API

#### 1. **Signup API**
- **Endpoint**: `/api/user/signup`
- **Method**: `POST`
- **Description**: Allows user signup with or without a referral code.
- **Request Body**:
  ```json
  {
      "name": "string",
      "email": "string",
      "password": "string",
      "referrerCode": "string (optional)"
  }
  ```
- **Response**:
  ```json
  {
      "name": "string",
      "email": "string",
      "phoneNumber": "string",
      "address": "string",
      "referralCode": "string",
      "referrerCode": "string",
      "profileCompleted": false
  }
  ```

#### 2. **Profile Completion API**
- **Endpoint**: `/api/user/complete-profile`
- **Method**: `POST`
- **Description**: Marks a user’s profile as complete and updates referral status if applicable.
- **Request Body**:
  ```json
  {
      "name": "string",
      "email": "string",
      "password": "string",
      "phoneNumber": "string",
      "address": "string"
  }
  ```
- **Response**:
  ```json
  {
      "name": "string",
      "email": "string",
      "phoneNumber": "string",
      "address": "string",
      "referralCode": "string",
      "referrerCode": "string",
      "profileCompleted": true
  }
  ```

#### 3. **Referred Users API**
- **Endpoint**: `/api/user/referred/{referralCode}`
- **Method**: `GET`
- **Description**: Fetches the list of users referred by the provided referral code.
- **Response**:
  ```json
  [
      {
          "name": "string",
          "email": "string",
          "phoneNumber": "string",
          "address": "string",
          "referralCode": "string",
          "referrerCode": "string",
          "profileCompleted": false
      }
  ]
  ```

### CSV Report API

#### 1. **Generate Referral Report**
- **Endpoint**: `/api/report/referrals`
- **Method**: `GET`
- **Description**: Generates a CSV report of all users and their referrals.
- **Response**:
    - CSV file (`Referral_Report.csv`) is downloaded.

---

## Database Schema

### **Users Collection**
| Field            | Type    | Description                              |
|------------------|---------|------------------------------------------|
| `id`             | String  | Unique identifier for a user.            |
| `name`           | String  | Full name of the user.                   |
| `email`          | String  | Email address of the user.               |
| `password`       | String  | Password for user authentication.        |
| `referralCode`   | String  | Unique referral code for the user.       |
| `referrerCode`   | String  | Referral code of the referring user.     |
| `profileCompleted`| Boolean | Indicates if the user has completed their profile. |
| `phoneNumber`    | String  | User's phone number.                     |
| `address`        | String  | User's address.                          |
| `referredUsers`  | List    | List of users referred by this user.     |

---

## Setup & Deployment

### Prerequisites
- Java 17+
- Maven
- MongoDB
- AWS Elastic Beanstalk Account

### Steps to Run Locally
1. Clone the repository:
   ```bash
   git clone https://github.com/ShubhAgarwal0704/SimplifyMoney-Referral-System
   ```
2. Navigate to the project directory:
   ```bash
   cd simplify-money
   ```
3. Set up MongoDB:
    - Create a MongoDB database named `TaskDB`.
    - Update the `application.properties` file with your MongoDB URI.
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```
6. Access the APIs at `http://localhost:8081`.

### Deployment on AWS Elastic Beanstalk
1. Package the application:
   ```bash
   mvn clean package
   ```
2. Deploy the JAR file to AWS Elastic Beanstalk.

---

## Sample Curl Requests

### 1. **Signup API**
```bash
curl -X POST "http://smtask.ap-south-1.elasticbeanstalk.com/api/user/signup" -H "Content-Type: application/json" -d "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"securePassword\"}"
```

### 2. **Profile Completion API**
```bash
curl -X POST "http://smtask.ap-south-1.elasticbeanstalk.com/api/user/complete-profile" -H "Content-Type: application/json" -d "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"securePassword\", \"phoneNumber\": \"9876543210\", \"address\": \"123 Main St, Cityville\"}"
```

### 3. **Referred Users API**
```bash
curl -X GET http://smtask.ap-south-1.elasticbeanstalk.com/api/user/referred/ABC123
```

### 4. **Generate Referral Report**
```bash
curl -X GET http://smtask.ap-south-1.elasticbeanstalk.com/api/report/referrals -o Referral_Report.csv
```

---

## Author

**Shubh Agarwal**  
GitHub: [ShubhAgarwal0704](https://github.com/ShubhAgarwal0704)
