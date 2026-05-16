package com.example.tripplanpractice.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherNotificationRequestDto {
    @NotNull(message = "필수로 입력해야 합니다.")
    private Double latitude;
    @NotNull(message = "필수로 입력해야 합니다.")
    private Double longitude;
}
