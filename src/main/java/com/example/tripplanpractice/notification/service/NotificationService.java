package com.example.tripplanpractice.notification.service;

import com.example.tripplanpractice.global.enums.ErrorCode;
import com.example.tripplanpractice.global.enums.UseYnEnum;
import com.example.tripplanpractice.global.exception.BusinessException;
import com.example.tripplanpractice.notification.domain.Notification;
import com.example.tripplanpractice.notification.domain.UserFcmToken;
import com.example.tripplanpractice.notification.dto.request.WeatherNotificationRequestDto;
import com.example.tripplanpractice.notification.enums.NotificationType;
import com.example.tripplanpractice.notification.repository.NotificationRepository;
import com.example.tripplanpractice.notification.repository.UserFcmTokenRepository;
import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final FcmService fcmService;
    private final WeatherService weatherService;

    /**
     * 알림 전송
     *
     * * 사용자의 마케팅 수신 여부와 야간 알림 허용 여부를 검사한 뒤,
     * 알림 데이터를 DB에 저장하고 FCM 알림을 전송합니다.
     *
     * @param loginId 로그인한 사용자 ID
     * @param notificationType 알림 타입
     * @param title 알림 제목
     * @param content 알림 내용
     * @throws BusinessException 사용자가 존재하지 않거나 FCM 토큰이 없는 경우
     */
    @Transactional
    public void sendNotification(String loginId, NotificationType notificationType, String title, String content) {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getMarketingAgree() == UseYnEnum.N)
            return;

        if (isNightTime() && user.getNightMarketingAgree() == UseYnEnum.N)
            return;

        // FCM 토큰 조회
        UserFcmToken userFcmToken = userFcmTokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.FCM_TOKEN_NOT_FOUND));

        // 알림 저장
        Notification notification = Notification.builder()
                .user(user)
                .type(notificationType)
                .title(title)
                .content(content)
                .build();
        notificationRepository.save(notification);

        // 알림 전송
        try {
            fcmService.sendPushNotification(userFcmToken.getFcmToken(), title, content);
        } catch (Exception e) {
            log.error("FCM 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 날씨 알림 전송
     *
     * * 요청으로 전달받은 위치 정보를 기반으로 날씨 데이터를 조회한 뒤,
     * 날씨 정보를 포함한 알림 메시지를 생성하여 전송합니다.
     *
     * @param loginId 로그인한 사용자 ID
     * @param dto 날씨 알림 요청 정보
     */
    @Transactional
    public void sendWeatherNotification(String loginId, WeatherNotificationRequestDto dto) {

        // OpenWeather API 기반 날씨 정보 조회
        WeatherService.WeatherInfo weatherInfo = weatherService.getWeatherInfo(dto.getLatitude(), dto.getLongitude());

        String title = "날씨 안내";
        String content = String.format("오늘 날씨는 최저온도 %d도, 최고온도 %d도의 %s 날씨입니다.",
                weatherInfo.minTemp(), weatherInfo.maxTemp(), weatherInfo.weatherStatus());

        sendNotification(loginId, NotificationType.WEATHER, title, content);
    }

    /**
     * 현재 시간이 야간 시간대인지 확인
     *
     * @return 야간 시간 여부
     */
    private boolean isNightTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(LocalTime.of(21, 0)) || now.isBefore(LocalTime.of(8, 0));
    }
}
