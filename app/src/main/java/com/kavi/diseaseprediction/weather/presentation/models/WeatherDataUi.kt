package com.kavi.diseaseprediction.weather.presentation.models

import com.kavi.diseaseprediction.weather.domain.WeatherData
import java.text.NumberFormat
import java.util.Locale

data class WeatherDataUi (
    val location: String,
    val date:String,
    val avgTemp: DisplayableNumber,
    val humidity: DisplayableNumber,
    val windSpeed: DisplayableNumber,
    val rainfall: DisplayableNumber
)

data class DisplayableNumber(
    val value: Double,
    val formatted: String
)

fun WeatherData.toWeatherDataUi(): WeatherDataUi {
    return WeatherDataUi(
        location = location,
        avgTemp = avgTemp.toDisplayableNumber(),
        humidity = humidity.toDisplayableNumber(),
        windSpeed = windSpeed.toDisplayableNumber(),
        rainfall = rainfall.toDisplayableNumber(),
        date = date
    )
}

fun Double.toDisplayableNumber(): DisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}
fun Int.toDisplayableNumber(): DisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return DisplayableNumber(
        value = this.toDouble(),
        formatted = formatter.format(this)
    )
}