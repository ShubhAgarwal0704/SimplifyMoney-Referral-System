package com.task.simlipfymoney.services;

import com.task.simlipfymoney.dtos.ProfileCompletionRequest;
import com.task.simlipfymoney.dtos.SignupRequest;
import com.task.simlipfymoney.dtos.UserResponse;
import com.task.simlipfymoney.entities.User;
import com.task.simlipfymoney.exceptions.DuplicateEmailException;
import com.task.simlipfymoney.exceptions.InvalidReferralCodeException;
import com.task.simlipfymoney.exceptions.UserNotFoundException;
import com.task.simlipfymoney.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UserResponse signup(SignupRequest request) {
        logger.info("Attempting to sign up user with email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.error("Signup failed: Email {} is already in use", request.getEmail());
            throw new DuplicateEmailException("Email is already in use. Please use a different email.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setReferralCode(generateReferralCode());

        if (request.getReferrerCode() != null) {
            Optional<User> referrer = userRepository.findByReferralCode(request.getReferrerCode());
            if (referrer.isPresent()) {
                user.setReferrerCode(request.getReferrerCode());
                logger.info("Referrer code {} validated for user {}", request.getReferrerCode(), request.getEmail());
            } else {
                logger.error("Invalid referral code: {}", request.getReferrerCode());
                throw new InvalidReferralCodeException("Invalid referral code provided.");
            }
        }

        User savedUser = userRepository.save(user);
        logger.info("User signed up successfully with email: {}", savedUser.getEmail());
        return mapToUserResponse(savedUser);
    }

    public UserResponse completeProfile(ProfileCompletionRequest request) {
        logger.info("Attempting to complete profile for user with email: {}", request.getEmail());

        User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(() -> {
                    logger.error("Profile completion failed: Invalid email or password");
                    return new UserNotFoundException("Invalid email or password.");
                });

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        if (user.getName() != null && !user.getName().isEmpty() &&
                user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() &&
                user.getAddress() != null && !user.getAddress().isEmpty()) {

            if (!user.isProfileCompleted()) {
                user.setProfileCompleted(true);
                logger.info("Profile completed successfully for user with email: {}", user.getEmail());

                if (user.getReferrerCode() != null) {
                    Optional<User> referrer = userRepository.findByReferralCode(user.getReferrerCode());
                    // Adding referral logic
                    if (referrer.isPresent()) {
                        User referrerUser = referrer.get();
                        if (referrerUser.getReferredUsers() == null) {
                            referrerUser.setReferredUsers(new ArrayList<>());
                        }
                        referrerUser.getReferredUsers().add(user.getEmail());
                        userRepository.save(referrerUser);
                        logger.info("Referral applied: {} referred by {}", user.getEmail(), user.getReferrerCode());
                    }
                }
            }
        }

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public List<UserResponse> getReferredUsers(String referralCode) {
        logger.info("Fetching referred users for referral code: {}", referralCode);

        Optional<User> referrerUser = userRepository.findByReferralCode(referralCode);

        if (referrerUser.isEmpty()) {
            logger.error("No user found with referral code: {}", referralCode);
            throw new InvalidReferralCodeException("No user found with referral code: " + referralCode);
        }

        User referrer = referrerUser.get();

        List<String> referredEmails = referrer.getReferredUsers();

        if (referredEmails == null || referredEmails.isEmpty()) {
            logger.warn("No referred user emails found for referral code: {}", referralCode);
            return List.of();
        }

        Optional<List<User>> referredUsers = userRepository.findByEmailInAndProfileCompletedTrue(referredEmails);

        if (referredUsers.isPresent() && !referredUsers.get().isEmpty()) {
            logger.info("{} referred users fetched for referral code: {}", referredUsers.get().size(), referralCode);

            return referredUsers
                    .get().stream()
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());
        }else{
            logger.warn("No referred users found for referral code: {}", referralCode);
            return List.of();
        }
    }

    // 6 character alphanumeric referral code generator
    String generateReferralCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder referralCode = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            referralCode.append(ALPHANUMERIC_CHARACTERS.charAt(random.nextInt(ALPHANUMERIC_CHARACTERS.length())));
        }
        return referralCode.toString();
    }

    // Mapping User to User Response
    public UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setReferralCode(user.getReferralCode());
        response.setReferrerCode(user.getReferrerCode());
        response.setProfileCompleted(user.isProfileCompleted());
        return response;
    }
}