package com.example.tripplanpractice.notification.repository;

import com.example.tripplanpractice.notification.domain.UserFcmToken;
import com.example.tripplanpractice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {
    Optional<UserFcmToken> findByUser(User user);
}
