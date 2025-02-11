package com.kavi.diseaseprediction

import android.content.Context
import android.util.Log
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.presentation.models.DiseasePredictionUi
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


data class WeeklyWeatherData(
    val minTemp: Float,
    val maxTemp: Float,
    val avgHumidity: Float,
    val totalRainfall: Float,
    val avgWindSpeed: Float,
    val weekNumber: Int,
    val fromDate: String,  // Add this field
    val toDate: String     // Add this field
)

/*
fun extractWeatherDataFromExcel(inputStream: InputStream, targetWeek: Int): List<WeatherData> {
    val workbook = WorkbookFactory.create(inputStream)
    val sheet = workbook.getSheetAt(0) // Assuming data is in the first sheet
    val weatherDataList = mutableListOf<WeatherData>()

    for (row in sheet.drop(1)) { // Skipping the header row
        val weekNum = row.getCell(0)?.numericCellValue?.toInt() ?: continue
        if (weekNum == targetWeek) {
            val minT = row.getCell(2)?.numericCellValue ?: 0.0
            val maxT = row.getCell(1)?.numericCellValue ?: 0.0
            val RHm = row.getCell(3)?.numericCellValue ?: 0.0
            val RHe = row.getCell(4)?.numericCellValue ?: 0.0
            val windSpeed = row.getCell(5)?.numericCellValue ?: 0.0
            val rainfall = row.getCell(8)?.numericCellValue ?: 0.0

            weatherDataList.add(
                WeatherData(
                    date = "",  // Adjust based on your actual date column
                    minTemp = minT,
                    maxTemp = maxT,
                    humidity = ((RHm + RHe) / 2).toInt(),
                    rainfall = rainfall,
                    windSpeed = windSpeed,
                    location = "",
                    avgTemp = ((minT + maxT) / 2)
                )
            )
        }
    }

    workbook.close()
    return weatherDataList
}




// Convert week number to date range
fun getDateRangeForWeek(weekNumber: Int): Pair<String, String> {
    val year = LocalDate.now().year
    val firstDayOfYear = LocalDate.of(year, 1, 1)
    val weekFields = WeekFields.of(Locale.getDefault())

    val fromDate = firstDayOfYear.with(weekFields.weekOfYear(), weekNumber.toLong())
    val toDate = fromDate.plusDays(6)

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    return fromDate.format(formatter) to toDate.format(formatter)
}

// Function to aggregate weekly weather data
fun aggregateWeatherData(weeklyData: List<WeatherData>, targetWeek: Int): WeeklyWeatherData? {
    if (weeklyData.isEmpty()) return null

    val avgMinTemp = weeklyData.map { it.minTemp }.average().toFloat()
    val avgMaxTemp = weeklyData.map { it.maxTemp }.average().toFloat()
    val avgHumidity = weeklyData.map { it.humidity.toDouble() }.average().toFloat()
    val totalRainfall = weeklyData.sumOf { it.rainfall }.toFloat()
    val avgWindSpeed = weeklyData.map { it.windSpeed }.average().toFloat()

    val (fromDate, toDate) = getDateRangeForWeek(targetWeek)

    return WeeklyWeatherData(
        minTemp = avgMinTemp,
        maxTemp = avgMaxTemp,
        avgHumidity = avgHumidity,
        totalRainfall = totalRainfall,
        avgWindSpeed = avgWindSpeed,
        fromDate = fromDate,
        toDate = toDate,
        weekNumber = targetWeek,
    )
}

 */

