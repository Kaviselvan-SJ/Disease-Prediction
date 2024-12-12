package com.kavi.diseaseprediction.weather.data.networking

import com.kavi.diseaseprediction.BuildConfig
import com.kavi.diseaseprediction.core.data.networking.constructUrl
import com.kavi.diseaseprediction.core.data.networking.safeCall
import com.kavi.diseaseprediction.core.domain.util.NetworkError
import com.kavi.diseaseprediction.core.domain.util.Result
import com.kavi.diseaseprediction.core.domain.util.map
import com.kavi.diseaseprediction.weather.data.mappers.toWeatherData
import com.kavi.diseaseprediction.weather.data.networking.dto.WeatherResponseDto
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.domain.WeatherDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class RemoteWeatherDataSource(
    private val httpClient: HttpClient
): WeatherDataSource {

    override suspend fun getDailyWeather(location: String,date: String): Result<List<WeatherData>, NetworkError> {
        return safeCall<WeatherResponseDto> {
            httpClient.get {
                url(constructUrl("/history.json"))
                parameter("key", BuildConfig.API_KEY) // API key from BuildConfig
                parameter("q", location) // Location parameter
                parameter("dt", date) // Date parameter (format: YYYY-MM-DD)
            }
        }.map { response ->
            response.forecast.forecastday.map { it.toWeatherData(response.location.name) }
        }
    }
}