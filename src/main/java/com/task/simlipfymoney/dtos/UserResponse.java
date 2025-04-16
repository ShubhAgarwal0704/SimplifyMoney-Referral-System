package com.task.simlipfymoney.dtos;

import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String referralCode;
    private String referrerCode;
    private boolean profileCompleted;
}
