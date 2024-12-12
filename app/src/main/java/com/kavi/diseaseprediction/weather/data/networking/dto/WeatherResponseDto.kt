package com.kavi.diseaseprediction.weather.data.networking.dto

import com.kavi.diseaseprediction.weather.domain.WeatherData
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val location: LocationDto,
    val forecast: ForecastDto
)

@Serializable
data class LocationDto(
    val name: String
)

@Serializable
data class ForecastDto(
    val forecastday: List<ForecastDayDto>
)

@Serializable
data class ForecastDayDto(
    val date: String,
    val day: DayDto
)
@Serializable
data class DayDto(
    val maxtemp_c: Double,           // Maximum temperature (Celsius)
    val mintemp_c: Double,           // Minimum temperature (Celsius)
    val avgtemp_c: Double,           // Average temperature (Celsius)
    val totalprecip_mm: Double,      // Total precipitation (Millimeters)
    val avghumidity: Int,            // Humidity (%)
    val maxwind_kph: Double
)