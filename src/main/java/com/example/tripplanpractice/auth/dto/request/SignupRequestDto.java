package com.example.tripplanpractice.auth.dto.request;

import com.example.tripplanpractice.user.domain.User;
import com.example.tripplanpractice.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private LocalDate birthDate;
    private Gender gender;
    private String countryCode;
    private boolean privacyAgree;
    private boolean marketingAgree;
    private boolean nightMarketingAgree;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(this.loginId)
                .passwordHash(encodedPassword)
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .countryCode(this.countryCode)
                .privacyAgree(this.privacyAgree)
                .marketingAgree(this.marketingAgree)
                .nightMarketingAgree(this.nightMarketingAgree)
                .build();
    }
}
