package com.task.simlipfymoney.services;


import com.task.simlipfymoney.dtos.ReferralReportResponse;
import com.task.simlipfymoney.entities.User;
import com.task.simlipfymoney.exceptions.CsvGenerationException;
import com.task.simlipfymoney.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private UserRepository userRepository;

    public void generateCsvReport(HttpServletResponse response) {
        logger.info("Starting CSV report generation...");

        try {
            String filename = "referral_report.csv";
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));

            PrintWriter writer = response.getWriter();

            // Write CSV header
            writer.println("Name,Email,Referral Code,Referrer Code,Profile Completed,Phone Number,Address,Referred Users");

            List<User> allUsers = userRepository.findAll();
            logger.info("Fetched {} users from database for CSV generation.", allUsers.size());

            for (User user : allUsers) {
                ReferralReportResponse dto = ReferralReportResponse.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .referralCode(user.getReferralCode())
                        .referrerCode(user.getReferrerCode())
                        .profileCompleted(user.isProfileCompleted())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .referredUsers(user.getReferredUsers())
                        .build();

                String referred = dto.getReferredUsers() != null ? String.join(";", dto.getReferredUsers()) : "";
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        sanitize(dto.getName()),
                        sanitize(dto.getEmail()),
                        sanitize(dto.getReferralCode()),
                        sanitize(dto.getReferrerCode()),
                        dto.isProfileCompleted(),
                        sanitize(dto.getPhoneNumber()),
                        sanitize(dto.getAddress()),
                        sanitize(referred));
            }

            writer.flush();
            writer.close();
            logger.info("CSV report generated and sent successfully.");

        } catch (Exception e) {
            logger.error("Error generating CSV report: {}", e.getMessage(), e);
            throw new CsvGenerationException("Failed to generate CSV report");
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}