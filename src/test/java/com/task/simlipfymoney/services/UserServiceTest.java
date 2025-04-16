package com.task.simlipfymoney.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.task.simlipfymoney.dtos.ProfileCompletionRequest;
import com.task.simlipfymoney.dtos.SignupRequest;
import com.task.simlipfymoney.dtos.UserResponse;
import com.task.simlipfymoney.entities.User;
import com.task.simlipfymoney.exceptions.DuplicateEmailException;
import com.task.simlipfymoney.exceptions.InvalidReferralCodeException;
import com.task.simlipfymoney.exceptions.UserNotFoundException;
import com.task.simlipfymoney.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private SignupRequest signupRequest;
    private ProfileCompletionRequest profileCompletionRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("user1@example.com");
        signupRequest.setPassword("password123");

        profileCompletionRequest = new ProfileCompletionRequest();
        profileCompletionRequest.setEmail("user1@example.com");
        profileCompletionRequest.setPassword("password123");
        profileCompletionRequest.setPhoneNumber("1234567890");
        profileCompletionRequest.setName("User 1");

        user = new User();
        user.setEmail("user1@example.com");
        user.setName("User 1");
        user.setPassword("password123");
        user.setReferralCode("REF123");
        user.setProfileCompleted(false);
    }

    //Test for sign up successful
    @Test
    public void testSignupSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.signup(signupRequest);

        assertNotNull(response);
        assertEquals("user1@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    //Test for DuplicateEmailException during sign up
    @Test
    public void testSignupDuplicateEmailException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(DuplicateEmailException.class, () -> userService.signup(signupRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    //Test for complete profile successful
    @Test
    public void testCompleteProfileSuccess() {
        when(userRepository.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setProfileCompleted(true);
            return savedUser;
        });

        UserResponse response = userService.completeProfile(profileCompletionRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertTrue(savedUser.isProfileCompleted(), "User profile should be marked as completed");
        assertEquals("User 1", response.getName());
        assertEquals("1234567890", response.getPhoneNumber());
    }

    //Test for UserNotFoundException during complete profile
    @Test
    public void testCompleteProfileUserNotFoundException() {
        when(userRepository.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.completeProfile(profileCompletionRequest));
    }

    //Test for get referred users successfully
    @Test
    public void testGetReferredUsersSuccess() {
        List<String> referredEmails = Arrays.asList("user2@example.com", "user3@example.com");
        User referrer = new User();
        referrer.setReferralCode("REF123");
        referrer.setReferredUsers(referredEmails);

        when(userRepository.findByReferralCode("REF123")).thenReturn(Optional.of(referrer));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setProfileCompleted(true);
        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setProfileCompleted(true);

        List<User> referredUsers = Arrays.asList(user2, user3);
        when(userRepository.findByEmailInAndProfileCompletedTrue(referredEmails)).thenReturn(Optional.of(referredUsers));

        List<UserResponse> responses = userService.getReferredUsers("REF123");

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("user2@example.com", responses.get(0).getEmail());
        assertEquals("user3@example.com", responses.get(1).getEmail());
    }

    //Test for InvalidReferralCodeException during get referred users
    @Test
    public void testGetReferredUserInvalidReferralCodeException() {
        when(userRepository.findByReferralCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(InvalidReferralCodeException.class, () -> userService.getReferredUsers("INVALID"));
    }
}
