package com.kavi.diseaseprediction.weather.domain

import com.kavi.diseaseprediction.core.domain.util.NetworkError
import com.kavi.diseaseprediction.core.domain.util.Result

interface DiseaseDataSource {
    suspend fun classifyDisease(
        temperature: Double,
        rainfall: Double,
        humidity: Double,
        windSpeed: Double
    ): Result<String, NetworkError>
}
