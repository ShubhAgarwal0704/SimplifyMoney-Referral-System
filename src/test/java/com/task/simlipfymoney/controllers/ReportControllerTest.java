package com.task.simlipfymoney.controllers;

import com.task.simlipfymoney.services.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ReportController reportController;

    public ReportControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateReferralReport_CallsService() {
        reportController.generateReferralReport(response);
        verify(reportService, times(1)).generateCsvReport(response);
    }
}
