package com.kavi.diseaseprediction.weather.presentation.weather_List

import com.kavi.diseaseprediction.core.domain.util.NetworkError

sealed interface WeatherListEvent {
    data class Error(val error: NetworkError): WeatherListEvent
}