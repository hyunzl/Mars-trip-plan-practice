package com.example.tripplanpractice.notification.repository;

import com.example.tripplanpractice.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
