package com.example.tripplanpractice.notification.dto.response;

import com.example.tripplanpractice.global.enums.UseYnEnum;
import com.example.tripplanpractice.notification.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private String title;
    private String content;
    private LocalDateTime sendAt;
    private UseYnEnum isRead;
    private LocalDateTime readAt;

    public NotificationResponseDto(Notification notification) {
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.sendAt = notification.getSendAt();
        this.isRead = notification.getIsRead();
        this.readAt = notification.getReadAt();
    }
}
