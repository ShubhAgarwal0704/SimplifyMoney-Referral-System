package com.task.simlipfymoney.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReferralReportResponse {
    private String name;
    private String email;
    private String referralCode;
    private String referrerCode;
    private boolean profileCompleted;
    private String phoneNumber;
    private String address;
    private List<String> referredUsers;
}
