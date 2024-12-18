package com.kavi.diseaseprediction.weather.presentation.weather_List

import androidx.compose.runtime.Immutable
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.presentation.models.DiseasePredictionUi
import com.kavi.diseaseprediction.weather.presentation.models.WeatherDataUi


@Immutable
data class WeatherDataState(
    val isLoading: Boolean = false,
    val weatherData: List<WeatherDataUi> = emptyList(),
    val originalWeatherData: List<WeatherData> = emptyList(),
    val diseasePrediction: DiseasePredictionUi? = null
)
