package com.example.tripplanpractice.auth.service;

import com.example.tripplanpractice.auth.dto.request.PasswordEmailRequestDto;
import com.example.tripplanpractice.auth.dto.request.LoginRequestDto;
import com.example.tripplanpractice.auth.dto.request.SignupRequestDto;
import com.example.tripplanpractice.auth.dto.request.TokenReissueRequestDto;
import com.example.tripplanpractice.auth.dto.response.PasswordEmailResponseDto;
import com.example.tripplanpractice.auth.dto.response.LoginResponseDto;
import com.example.tripplanpractice.auth.dto.response.SignupResponseDto;
import com.example.tripplanpractice.auth.dto.response.TokenReissueResponseDto;
import com.example.tripplanpractice.global.exception.BusinessException;
import com.example.tripplanpractice.global.enums.ErrorCode;
import com.example.tripplanpractice.global.security.JwtTokenProvider;
import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;
    // 동시에 여러 사용자가 접근할 수 있기 때문에 ConcurrentHashMap 사용
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * 회원가입
     *
     * * 비밀번호는 보안을 위해 해시 처리 후 저장한다.
     *
     * @param dto 회원가입 요청 DTO
     * @return 회원가입 응답 DTO
     */
    @Transactional
    public SignupResponseDto save(SignupRequestDto dto) {
        // 비밀번호 해시처리(암호화)
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = dto.toEntity(encodedPassword);

        User savedUser = userRepository.save(user);

        return SignupResponseDto.from(savedUser);
    }

    /**
     * 로그인
     *
     * * LoginId 기반으로 유저 조회 후,
     * 입력한 비밀번호와 암호화된 비밀번호를 비교 검증한다.
     *
     * * 인증 성공 시 Access Token / Refresh Token을 발급한다.
     *
     * @param dto 로그인 요청 DTO
     * @return 로그인 응답 DTO
     * @throws BusinessException 유저가 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {

        // 유저 조회(존재하는지 검증)
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        // 객체에 Refresh Token + 만료시간 저장
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(14));

        return LoginResponseDto.from(user, accessToken, refreshToken);
    }

    /**
     * Access Token 재발급
     *
     * * Refresh Token 유효성 검증 후,
     * 새로운 Access Token / Refresh Token을 재발급한다.
     *
     * @param dto 토큰 재발급 요청 DTO
     * @return 토큰 재발급 응답 DTO
     * @throws BusinessException 토큰이 유효하지 않거나 만료된 경우
     */
    @Transactional
    public TokenReissueResponseDto reissue(TokenReissueRequestDto dto) {

        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // Refresh Token 기반 유저 조회
        User user = userRepository.findByRefreshToken(dto.getRefreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Refresh Token 만료 여부 확인
        if (user.getRefreshTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        // 새로운 Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        // 객체에 Refresh Token + 만료시간 저장
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(14));

        return TokenReissueResponseDto.from(accessToken, refreshToken);
    }

    /**
     * 비밀번호 재설정용 인증메일 발송
     *
     * * 사용자가 입력한 loginId와 email이 실제 회원 정보와 일치하는지 검증한 뒤,
     * 6자리 인증번호를 생성하여 메모리에 저장하고 이메일로 전송합니다.
     *
     * * 이메일 발송 실패 시 EMAIL_SEND_FAIL 처리
     *
     * @param dto 비밀번호 재설정 요청 정보 (loginId, email)
     * @return 인증 메일 발송 대상자 로그인 아이디, 이메일
     * @throws BusinessException 회원 정보가 존재하지 않거나 메일 발송에 실패한 경우
     */
    @Transactional(readOnly = true)
    public PasswordEmailResponseDto sendPasswordResetEmail(PasswordEmailRequestDto dto) {

        userRepository.findByLoginIdAndEmail(dto.getLoginId(), dto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmail());
        message.setSubject("인증번호 발송");
        message.setText("인증번호: " + code);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAIL);
        }

        verificationCodes.put(dto.getEmail(), code);

        return new PasswordEmailResponseDto(dto.getLoginId(), dto.getEmail());
    }
}
