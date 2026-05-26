package com.example.tripplanpractice.notification.repository;

import com.example.tripplanpractice.global.enums.UseYnEnum;
import com.example.tripplanpractice.notification.domain.Notification;
import com.example.tripplanpractice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsDeletedFalseOrderBySendAtDesc(User user);
    boolean existsByUserAndIsReadAndIsDeletedFalse(User user, UseYnEnum isRead);
}
