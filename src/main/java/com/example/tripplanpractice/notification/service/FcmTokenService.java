package com.example.tripplanpractice.notification.service;

import com.example.tripplanpractice.global.enums.ErrorCode;
import com.example.tripplanpractice.global.exception.BusinessException;
import com.example.tripplanpractice.notification.domain.UserFcmToken;
import com.example.tripplanpractice.notification.dto.request.FcmTokenRequestDto;
import com.example.tripplanpractice.notification.repository.UserFcmTokenRepository;
import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmTokenService {

    private final UserFcmTokenRepository userFcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * FCM 토큰 저장
     *
     * * 로그인한 사용자의 FCM 토큰을 저장합니다.
     * * 이미 저장된 토큰이 존재하면 기존 토큰을 최신 토큰으로 수정하고,
     * 저장된 토큰이 없다면 새 토큰 정보를 생성합니다.
     *
     * @param loginId 로그인한 사용자 ID
     * @param dto FCM 토큰 요청 정보
     * @throws BusinessException 존재하지 않는 사용자일 경우
     */
    @Transactional
    public void saveToken(String loginId, FcmTokenRequestDto dto) {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        userFcmTokenRepository.findByUser(user)
                .ifPresentOrElse(fcmToken -> fcmToken.updateFcmToken(dto.getToken()),
                        () -> userFcmTokenRepository.save(
                                UserFcmToken.builder()
                                        .user(user)
                                        .fcmToken(dto.getToken())
                                        .build()));
    };
}
