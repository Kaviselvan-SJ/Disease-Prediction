package com.kavi.diseaseprediction.weather.presentation.weather_List

import com.kavi.diseaseprediction.weather.presentation.models.DiseasePredictionUi
import com.kavi.diseaseprediction.weather.presentation.models.WeatherDataUi

sealed interface WeatherListAction {
    data class OnWeatherDataClick(val weatherDataUi: WeatherDataUi): WeatherListAction
    data class OnSearch(val location: String) : WeatherListAction
    data class OnDiseasePredictionClick(val diseasePredictionUi: DiseasePredictionUi): WeatherListAction
}