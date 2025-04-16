package com.task.simlipfymoney.services;

import com.task.simlipfymoney.entities.User;
import com.task.simlipfymoney.exceptions.CsvGenerationException;
import com.task.simlipfymoney.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testGenerateCsvReportSuccess() {
        User user = new User();
        user.setName("User 1");
        user.setEmail("user1@example.com");
        user.setReferralCode("REF123");
        user.setReferrerCode("REF456");
        user.setProfileCompleted(true);
        user.setPhoneNumber("1234567890");
        user.setAddress("Lko");
        user.setReferredUsers(List.of("user3", "user4"));

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        assertDoesNotThrow(() -> reportService.generateCsvReport(response));

        String output = stringWriter.toString();

        assertTrue(output.contains("Name,Email,Referral Code,Referrer Code,Profile Completed,Phone Number,Address,Referred Users"));
        assertTrue(output.contains("User 1,user1@example.com,REF123,REF456,true,1234567890,Lko,user3;user4"));
    }

    @Test
    void testGenerateCsvReportCsvGenerationException() {
        when(userRepository.findAll()).thenThrow(new CsvGenerationException("Failed to generate CSV report"));

        CsvGenerationException exception = assertThrows(
                CsvGenerationException.class,
                () -> reportService.generateCsvReport(response)
        );

        assertEquals("Failed to generate CSV report", exception.getMessage());
    }
}