package com.kavi.diseaseprediction.weather.presentation.weather_List.components


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.kavi.diseaseprediction.ui.theme.DiseasePredictionTheme
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.presentation.models.WeatherDataUi
import com.kavi.diseaseprediction.weather.presentation.models.toWeatherDataUi


@Composable
fun DailyWeather(
    weatherDataUi: WeatherDataUi,
    modifier: Modifier = Modifier
) {
    val contentColor = if(isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }
    Row(
        modifier = modifier
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Column {
            Text(
                text = "Location: ${weatherDataUi.location}",
                fontSize = 10.sp,
                color = contentColor
            )
            Text(
                text = "Rainfall: ${weatherDataUi.rainfall.formatted} mm",
                fontSize = 10.sp,
                color = contentColor
            )
        }
        Column {
            Text(
                text = "Avg Temp: ${weatherDataUi.avgTemp.formatted}°C",
                fontSize = 10.sp,
                color = contentColor
            )
            Text(
                text = "Humidity: ${weatherDataUi.humidity.formatted}%",
                fontSize = 10.sp,
                color = contentColor
            )
        }
        Column {
            Text(
                text = "Date: ${weatherDataUi.date} ",
                fontSize = 10.sp,
                color = contentColor
            )
            Text(
                text = "Wind Speed: ${weatherDataUi.windSpeed.formatted} kph",
                fontSize = 10.sp,
                color = contentColor
            )
        }

    }

}

@PreviewLightDark
@Composable
fun DailyWeatherPreview(){
    DiseasePredictionTheme {
        DailyWeather(
            weatherDataUi = previewData,
        )
    }
}

internal val previewData = WeatherData(
    location = "salem",
    maxTemp = 36.2,
    minTemp = 30.1,
    avgTemp = 33.5,
    humidity = 69,
    windSpeed = 28.4,
    rainfall = 0.22,
    date = "11-12-2024"
).toWeatherDataUi()