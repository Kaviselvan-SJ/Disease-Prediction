package com.kavi.diseaseprediction.weather.data.networking.dto


import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class DiseaseResponseDto(val disease : List<String>)