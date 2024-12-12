package com.kavi.diseaseprediction.weather.data.mappers


import com.kavi.diseaseprediction.weather.data.networking.dto.ForecastDayDto
import com.kavi.diseaseprediction.weather.domain.WeatherData

fun ForecastDayDto.toWeatherData(location: String): WeatherData {
    return WeatherData(
        location = location,
        date = date,
        minTemp = day.mintemp_c,
        maxTemp = day.maxtemp_c,
        avgTemp = day.avgtemp_c,
        rainfall = day.totalprecip_mm,
        humidity = day.avghumidity,
        windSpeed = day.maxwind_kph
    )
}