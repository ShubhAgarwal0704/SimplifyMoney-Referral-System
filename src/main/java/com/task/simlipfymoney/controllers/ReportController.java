package com.task.simlipfymoney.controllers;


import com.task.simlipfymoney.services.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@Tag(name = "CSV Report API")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/referrals")
    public void generateReferralReport(HttpServletResponse response) {
        reportService.generateCsvReport(response);
    }
}

