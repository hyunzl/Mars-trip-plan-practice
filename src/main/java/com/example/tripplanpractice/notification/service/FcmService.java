package com.example.tripplanpractice.notification.service;

import com.example.tripplanpractice.global.enums.ErrorCode;
import com.example.tripplanpractice.global.exception.BusinessException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    /**
     * FCM 알림 발송
     *
     * * 사용자의 FCM 토큰을 기반으로 제목과 내용을 포함한 알림을 전송합니다.
     *
     * @param token 알림을 수신할 디바이스의 FCM 토큰
     * @param title 푸시 알림 제목
     * @param body 푸시 알림 내용
     * @throws BusinessException FCM 알림 전송에 실패한 경우
     */
    public void sendPushNotification(String token, String title, String body) {

        // FCM 알림 생성
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FCM_SEND_FAIL);
        }
    }
}
