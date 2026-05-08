package com.example.tripplanpractice.auth.controller;

import com.example.tripplanpractice.auth.dto.request.LoginRequestDto;
import com.example.tripplanpractice.auth.dto.request.SignupRequestDto;
import com.example.tripplanpractice.auth.dto.request.TokenReissueRequestDto;
import com.example.tripplanpractice.auth.dto.response.LoginResponseDto;
import com.example.tripplanpractice.auth.dto.response.SignupResponseDto;
import com.example.tripplanpractice.auth.dto.response.TokenReissueResponseDto;
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
}
