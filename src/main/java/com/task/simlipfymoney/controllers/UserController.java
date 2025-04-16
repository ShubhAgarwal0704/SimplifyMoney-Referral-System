package com.task.simlipfymoney.controllers;

import com.task.simlipfymoney.dtos.ProfileCompletionRequest;
import com.task.simlipfymoney.dtos.SignupRequest;
import com.task.simlipfymoney.dtos.UserResponse;
import com.task.simlipfymoney.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse user = userService.signup(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<UserResponse> completeProfile(@Valid @RequestBody ProfileCompletionRequest request) {
        UserResponse user = userService.completeProfile(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/referred/{referralCode}")
    public ResponseEntity<List<UserResponse>> getReferredUsers(@PathVariable String referralCode) {
        List<UserResponse> referredUsers = userService.getReferredUsers(referralCode);
        return ResponseEntity.ok(referredUsers);
    }
}