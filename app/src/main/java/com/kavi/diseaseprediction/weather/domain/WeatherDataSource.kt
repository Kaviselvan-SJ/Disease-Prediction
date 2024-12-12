package com.kavi.diseaseprediction.weather.domain

import com.kavi.diseaseprediction.core.domain.util.NetworkError
import com.kavi.diseaseprediction.core.domain.util.Result

interface WeatherDataSource{
    suspend fun getDailyWeather(location:String, date:String): Result<List<WeatherData>, NetworkError>

}