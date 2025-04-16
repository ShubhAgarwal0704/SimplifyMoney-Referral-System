package com.task.simlipfymoney.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.simlipfymoney.dtos.ProfileCompletionRequest;
import com.task.simlipfymoney.dtos.SignupRequest;
import com.task.simlipfymoney.dtos.UserResponse;
import com.task.simlipfymoney.exceptions.DuplicateEmailException;
import com.task.simlipfymoney.exceptions.InvalidReferralCodeException;
import com.task.simlipfymoney.exceptions.UserNotFoundException;
import com.task.simlipfymoney.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private SignupRequest signupRequest;
    private ProfileCompletionRequest profileCompletionRequest;
    private UserResponse userResponse;

    @BeforeEach
    public void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setName("User 1");
        signupRequest.setEmail("user1@example.com");
        signupRequest.setPassword("password");

        profileCompletionRequest = new ProfileCompletionRequest();
        profileCompletionRequest.setName("User 1");
        profileCompletionRequest.setEmail("user1@example.com");
        profileCompletionRequest.setPassword("password");
        profileCompletionRequest.setPhoneNumber("1234567890");
        profileCompletionRequest.setAddress("Lko");

        userResponse = new UserResponse();
        userResponse.setName("User 1");
        userResponse.setEmail("user1@example.com");
        userResponse.setPhoneNumber("1234567890");
        userResponse.setAddress("Lko");
        userResponse.setReferralCode("ABC123");
        userResponse.setProfileCompleted(true);
    }

    @Test
    public void testSignup() throws Exception {
        when(userService.signup(Mockito.any(SignupRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User 1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    public void testCompleteProfile() throws Exception {
        when(userService.completeProfile(Mockito.any(ProfileCompletionRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/user/complete-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileCompletionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User 1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    public void testGetReferredUsers() throws Exception {
        when(userService.getReferredUsers(Mockito.anyString())).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/user/referred/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"));
    }

    @Test
    public void testSignupDuplicateEmailException() throws Exception {
        when(userService.signup(Mockito.any(SignupRequest.class)))
                .thenThrow(new DuplicateEmailException("Email is already in use. Please use a different email."));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("User exists for this mail id. Please try again."));
    }

    @Test
    public void testCompleteProfileUserNotFoundException() throws Exception {
        when(userService.completeProfile(Mockito.any(ProfileCompletionRequest.class)))
                .thenThrow(new UserNotFoundException("Invalid email or password."));

        mockMvc.perform(post("/api/user/complete-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileCompletionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("User not found."));
    }

    @Test
    public void testGetReferredUsersInvalidReferralCodeException() throws Exception {
        when(userService.getReferredUsers(Mockito.anyString()))
                .thenThrow(new InvalidReferralCodeException("Invalid referral code provided."));

        mockMvc.perform(get("/api/user/referred/INVALID123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid referral code."));
    }
}
