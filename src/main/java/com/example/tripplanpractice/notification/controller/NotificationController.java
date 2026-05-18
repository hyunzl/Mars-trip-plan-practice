package com.example.tripplanpractice.notification.controller;

import com.example.tripplanpractice.notification.dto.request.FcmTokenRequestDto;
import com.example.tripplanpractice.notification.dto.request.WeatherNotificationRequestDto;
import com.example.tripplanpractice.notification.dto.response.NotificationResponseDto;
import com.example.tripplanpractice.notification.service.FcmTokenService;
import com.example.tripplanpractice.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveToken(@Valid @RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        fcmTokenService.saveToken(loginId, fcmTokenRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/weather")
    public ResponseEntity<Void> weather(@Valid @RequestBody WeatherNotificationRequestDto weatherNotificationRequestDto) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.sendWeatherNotification(loginId, weatherNotificationRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(notificationService.getNotifications(loginId));
    }

    @GetMapping("/unread")
    public ResponseEntity<Boolean> getUnreadNotifications() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(notificationService.hasUnreadNotification(loginId));
    }
}
