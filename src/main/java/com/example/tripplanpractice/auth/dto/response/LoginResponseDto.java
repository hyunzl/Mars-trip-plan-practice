package com.example.tripplanpractice.auth.dto.response;

import com.example.tripplanpractice.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private SignupResponseDto userDetails;

    public static LoginResponseDto from(User user, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userDetails(SignupResponseDto.from(user))
                .build();
    }
}
