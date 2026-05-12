package com.example.tripplanpractice.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDto {

    @NotNull(message = "필수로 입력해야 합니다.")
    @Size(min = 1, max = 40)
    private String loginId;

    @NotNull(message = "필수로 입력해야 합니다.")
    @Email
    private String email;

    @NotNull(message = "필수로 입력해야 합니다.")
    private String verifyCode;
}
