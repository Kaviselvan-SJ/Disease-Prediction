package com.kavi.diseaseprediction.weather.domain

data class WeatherData(
    val location: String,
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rainfall: Double
)
