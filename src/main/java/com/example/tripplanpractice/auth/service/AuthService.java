package com.example.tripplanpractice.auth.service;

import com.example.tripplanpractice.auth.dto.request.LoginRequestDto;
import com.example.tripplanpractice.auth.dto.request.SignupRequestDto;
import com.example.tripplanpractice.auth.dto.request.TokenReissueRequestDto;
import com.example.tripplanpractice.auth.dto.response.LoginResponseDto;
import com.example.tripplanpractice.auth.dto.response.SignupResponseDto;
import com.example.tripplanpractice.auth.dto.response.TokenReissueResponseDto;
import com.example.tripplanpractice.global.security.JwtTokenProvider;
import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponseDto save(SignupRequestDto dto) {
        // 비밀번호 해시처리(암호화)
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = dto.toEntity(encodedPassword);

        User savedUser = userRepository.save(user);

        return SignupResponseDto.from(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        // 유저 조회(존재하는지 검증)
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(14));

        return LoginResponseDto.from(user, accessToken, refreshToken);
    }

    @Transactional
    public TokenReissueResponseDto reissue(TokenReissueRequestDto dto) {
        User user = userRepository.findByRefreshToken(dto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 만료 여부 확인
        if (user.getRefreshTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        // 객체에 토큰 + 만료시간 저장
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(14));

        return TokenReissueResponseDto.from(accessToken, refreshToken);
    }
}
