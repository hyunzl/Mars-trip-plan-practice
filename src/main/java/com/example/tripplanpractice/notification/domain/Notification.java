package com.example.tripplanpractice.notification.domain;

import com.example.tripplanpractice.global.entity.BaseEntity;
import com.example.tripplanpractice.global.enums.UseYnEnum;
import com.example.tripplanpractice.notification.enums.NotificationType;
import com.example.tripplanpractice.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", length = 60,nullable = false)
    private String title;

    @Column(name = "content", length = 400, nullable = false)
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "is_read", nullable = false)
    private UseYnEnum isRead = UseYnEnum.N;

    @Builder.Default
    @Column(name = "send_at", nullable = false)
    private LocalDateTime sendAt = LocalDateTime.now();

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false)
    private UseYnEnum isDeleted = UseYnEnum.N;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
