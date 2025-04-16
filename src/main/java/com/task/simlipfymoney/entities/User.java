package com.task.simlipfymoney.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotNull
    @NotEmpty(message = "username must be present")
    private String name;

    @NotNull
    @NotEmpty
    @Email(message = "enter a valid email")
    private String email;

    @NotNull
    @NotEmpty(message = "password must be present")
    private String password;

    private String referralCode;

    private String referrerCode;

    private boolean profileCompleted;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotEmpty
    private String address;

    private List<String> referredUsers;
}