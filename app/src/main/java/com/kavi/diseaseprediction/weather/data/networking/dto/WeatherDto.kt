package com.kavi.diseaseprediction.weather.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val location: String,
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val totalprecip_mm: Double,
    val humidity: Double,
    val wind_kph: Double
)


