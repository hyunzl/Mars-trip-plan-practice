package com.example.tripplanpractice.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEmailResponseDto {
    private String loginId;
    private String email;
}
