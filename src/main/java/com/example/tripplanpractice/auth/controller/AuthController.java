package com.example.tripplanpractice.auth.controller;

import com.example.tripplanpractice.auth.dto.request.*;
import com.example.tripplanpractice.auth.dto.response.*;
import com.example.tripplanpractice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> save(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.ok(authService.save(signupRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponseDto> reissue(@Valid @RequestBody TokenReissueRequestDto tokenReissueRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenReissueRequestDto));
    }

    @PostMapping("/password/send-verify")
    public ResponseEntity<PasswordEmailResponseDto> sendVerificationCode(@Valid @RequestBody PasswordEmailRequestDto passwordEmailRequestDto) {
        return ResponseEntity.ok(authService.sendPasswordResetCode(passwordEmailRequestDto));
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<PasswordResetResponseDto> verifyPasswordResetCode(@Valid @RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        return ResponseEntity.ok(authService.verifyPasswordResetCode(passwordResetRequestDto));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<PasswordResetResponseDto> resetPassword(@Valid @RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        return ResponseEntity.ok(authService.issueTempPassword(passwordResetRequestDto));
    }
}
