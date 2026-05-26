package com.example.tripplanpractice.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    // OpenWeather API 인증 키
    @Value("${openweather.api.key}")
    private String apiKey;

    /**
     * 현재 날씨 정보 조회
     *
     * * 사용자로부터 요청으로 전달받은 위도, 경도를 기반으로
     * OpenWeather API에 요청을 보내 날씨 정보를 조회합니다.
     *
     * @param latitude 사용자 위도
     * @param longitude 사용자 경도
     * @return 최저기온, 최고기온, 날씨 상태 정보
     */
    public WeatherInfo getWeatherInfo(double latitude, double longitude) {
        try {
            WebClient webClient = WebClient.create("https://api.openweathermap.org");

            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            int minTemp = (int) Math.round(((Number) main.get("temp_min")).doubleValue());
            int maxTemp = (int) Math.round(((Number) main.get("temp_max")).doubleValue());

            List<Map<String, Object>> weather = (List<Map<String, Object>>) response.get("weather");
            String weatherMain = (String) weather.get(0).get("main");

            return new WeatherInfo(minTemp, maxTemp, convertWeather(weatherMain));
        } catch (Exception e) {
            log.error("날씨 API 호출 실패: {}", e.getMessage());
            return new WeatherInfo(0, 0, "알 수 없음");
        }

    }

    /**
     * OpenWeather 영문 날씨 상태를 한글로 변환
     */
    private String convertWeather(String weatherMain) {
        return switch (weatherMain) {
            case "Clear" -> "맑은";
            case "Rain", "Drizzle" -> "비오는";
            case "Thunderstorm" -> "천둥번개";
            case "Snow" -> "눈오는";
            default -> "흐린";
        };
    }

    public record WeatherInfo(int minTemp, int maxTemp, String weatherStatus) {}
}
