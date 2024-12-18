package com.kavi.diseaseprediction.weather.data.networking


import com.kavi.diseaseprediction.core.data.networking.constructUrlForPrediction
import com.kavi.diseaseprediction.core.data.networking.safeCall
import com.kavi.diseaseprediction.core.domain.util.NetworkError
import com.kavi.diseaseprediction.core.domain.util.Result
import com.kavi.diseaseprediction.core.domain.util.map
import com.kavi.diseaseprediction.weather.data.networking.dto.DiseaseResponseDto
import com.kavi.diseaseprediction.weather.domain.DiseaseDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class RemoteDiseaseDataSource(
    private val httpClient: HttpClient
) : DiseaseDataSource {

    override suspend fun classifyDisease(
        temperature: Double,
        rainfall: Double,
        humidity: Double,
        windSpeed: Double
    ): Result<String, NetworkError> {
        return safeCall<DiseaseResponseDto> {
            httpClient.get {
                url(constructUrlForPrediction("/classify_disease"))
                parameter("temperature", temperature)
                parameter("rainfall", rainfall)
                parameter("humidity", humidity)
                parameter("wind_speed", windSpeed)
            }
        }.map { response ->
            response.disease.firstOrNull() ?: "No disease detected.." // Extract disease from the response DTO
        }
    }
}
