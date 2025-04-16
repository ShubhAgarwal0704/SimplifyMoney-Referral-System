package com.task.simlipfymoney.repositories;

import com.task.simlipfymoney.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // Fetch user using referral code
    Optional<User> findByReferralCode(String referralCode);

    // Fetch list of referred users
    Optional<List<User>> findByEmailInAndProfileCompletedTrue(List<String> emails);

    // Find user using email
    Optional<User> findByEmail(String email);

    // Find use using email and password
    Optional<User> findByEmailAndPassword(String email, String password);
}