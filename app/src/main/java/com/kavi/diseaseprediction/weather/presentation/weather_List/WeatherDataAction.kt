package com.kavi.diseaseprediction.weather.presentation.weather_List

import com.kavi.diseaseprediction.weather.presentation.models.WeatherDataUi

sealed interface WeatherListAction {
    data class OnCoinClick(val weatherDataUi: WeatherDataUi): WeatherListAction
    data class OnSearch(val location: String) : WeatherListAction
}