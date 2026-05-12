package com.example.tripplanpractice.auth.dto.request;

import com.example.tripplanpractice.global.enums.Role;
import com.example.tripplanpractice.global.enums.UseYnEnum;
import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @NotNull(message = "필수로 입력해야 합니다.")
    @Size(min = 1, max = 40)
    private String loginId;

    @NotNull(message = "필수로 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자이며, 영문, 숫자, 특수문자를 포함해야 합니다.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "필수로 입력해야 합니다.")
    @Email
    private String email;

    @NotNull(message = "필수로 입력해야 합니다.")
    @Size(max = 10)
    private String name;

    @NotNull(message = "필수로 입력해야 합니다.")
    @Size(max = 20)
    private String nickname;

    @NotNull(message = "필수로 입력해야 합니다.")
    private LocalDate birthDate;

    @NotNull(message = "필수로 입력해야 합니다.")
    private Gender gender;

    @NotNull(message = "필수로 입력해야 합니다.")
    private String countryCode;

    @NotNull(message = "필수로 입력해야 합니다.")
    private UseYnEnum privacyAgree;

    private UseYnEnum marketingAgree;

    private UseYnEnum nightMarketingAgree;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(this.loginId)
                .passwordHash(encodedPassword)
                .email(this.email)
                .role(Role.USER)
                .name(this.name)
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .countryCode(this.countryCode)
                .privacyAgree(this.privacyAgree)
                .marketingAgree(this.marketingAgree == null ? UseYnEnum.N : this.marketingAgree)
                .nightMarketingAgree(this.nightMarketingAgree == null ? UseYnEnum.N : this.nightMarketingAgree)
                .build();
    }
}
