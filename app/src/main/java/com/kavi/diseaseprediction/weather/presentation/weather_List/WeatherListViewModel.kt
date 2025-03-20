package com.kavi.diseaseprediction.weather.presentation.weather_List

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kavi.diseaseprediction.TFLiteModelInterpreter
import com.kavi.diseaseprediction.core.domain.util.onError
import com.kavi.diseaseprediction.core.domain.util.onSuccess
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.domain.WeatherDataSource
import com.kavi.diseaseprediction.weather.domain.DiseaseDataSource
import com.kavi.diseaseprediction.weather.presentation.models.DiseasePredictionUi
import com.kavi.diseaseprediction.weather.presentation.models.toWeatherData
import com.kavi.diseaseprediction.weather.presentation.models.toWeatherDataUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeatherListViewModel(
    private val weatherDataSource: WeatherDataSource,
    private val diseaseDataSource: DiseaseDataSource, // Added for disease prediction
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherDataState())
    val state = _state
        .onStart { loadCurrentCityWeather() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            WeatherDataState()
        )

    private val _events = Channel<WeatherListEvent>()
    val events = _events.receiveAsFlow()

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentCity = MutableStateFlow("Fetching city...")
    val currentCity = _currentCity.asStateFlow()

    // Handles user actions
    fun onAction(action: WeatherListAction) {
        when (action) {
            is WeatherListAction.OnSearch -> {
                if (action.location.isEmpty()) {
                    loadCurrentCityWeather()
                } else {
                    loadWeatherData(action.location)
                }
            }
            is WeatherListAction.OnDiseasePredictionClick -> {
                onPredictDisease()
            }
            is WeatherListAction.OnWeatherDataClick -> {
                // Handle specific item click events
            }
        }
    }

    // Loads weather data for the given location and checks for disease prediction
    private fun loadWeatherData(location: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val today = LocalDate.now()
            val last7Days = (0..6).map { today.minusDays(it.toLong()).toString() }

            val weatherDataList = mutableListOf<WeatherData>()
            var errorOccurred = false

            try {
                last7Days.forEach { date ->
                    weatherDataSource
                        .getDailyWeather(location, date)
                        .onSuccess { weatherData ->
                            weatherDataList.addAll(weatherData)
                        }
                        .onError { error ->
                            if (!errorOccurred) {
                                errorOccurred = true
                                _events.send(WeatherListEvent.Error(error))
                            }
                        }
                }

                val weatherUiData = weatherDataList.map { it.toWeatherDataUi() }

                // If weather data is fetched successfully, call disease prediction API
                val diseasePredictionUi = if (weatherDataList.isNotEmpty()) {
                    classifyDisease(weatherDataList)
                } else {
                    null
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        weatherData = weatherUiData,
                        diseasePrediction = diseasePredictionUi
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                // _events.send(WeatherListEvent.Error(NetworkError.UnknownError(e.message)))
            }
        }
    }

    // Predicts disease based on the latest weather data
    // Predicts disease based on 7 days of weather data

    private val modelInterpreter1 by lazy { TFLiteModelInterpreter(context, "rice_blast_model.tflite") }
    private val modelInterpreter2 by lazy { TFLiteModelInterpreter(context, "false_smut_model.tflite") }

    // Predict disease using the local model
    private suspend fun classifyDisease(weatherDataList: List<WeatherData>): DiseasePredictionUi? {
        return try {
            // Aggregate the weather data metrics for 7 days
            val minTemp = weatherDataList.map { it.minTemp }.average()
            val maxTemp = weatherDataList.map { it.maxTemp }.average()
            val totalRainfall = weatherDataList.sumOf { it.rainfall }
            val avgHumidity = weatherDataList.map { it.humidity.toDouble() }.average()
            val avgWindSpeed = weatherDataList.map { it.windSpeed }.average()

            // Prepare input data
            val inputData = floatArrayOf(
                minTemp.toFloat(),
                maxTemp.toFloat(),  // Use avgTemp for minTemp (example)
                avgHumidity.toFloat(),
                totalRainfall.toFloat(),
                avgWindSpeed.toFloat()
            )

            // Scale input data
            val scaledInput = scaleInput(inputData)

            // Get prediction from model
            val result1 = modelInterpreter1.predict(scaledInput)
            val result2 = modelInterpreter2.predict(scaledInput)
            val percentage1 = result1[0] * 100
            val percentage2 = result2[0] * 100


            // Determine date range
            // Define date formatter for "DD-MM-YYYY"
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            // Convert dates to "DD-MM-YYYY" format
            val fromDate = weatherDataList.minByOrNull { it.date }?.date
                ?.let { LocalDate.parse(it).format(dateFormatter) } ?: "Unknown"

            val toDate = weatherDataList.maxByOrNull { it.date }?.date
                ?.let { LocalDate.parse(it).format(dateFormatter) } ?: "Unknown"

            // Return disease prediction
            DiseasePredictionUi(
                blastDiseaseRisk = "%.2f%%".format(percentage1),
                smutDiseaseRisk = "%.2f%%".format(percentage2),
                fromDate = fromDate,
                toDate = toDate
            )
        } catch (e: Exception) {
            Log.e("DiseasePrediction", "Error during prediction: $e")
            null
        }
    }

    // Scale input data (same logic as in TFLiteModelInterpreter)
    private fun scaleInput(inputArray: FloatArray): FloatArray {
        val maxTempRange = 15f
        val minTempRange = 10f
        val humidityRange = 30f
        val rainfallRange = 200f
        val windSpeedRange = 25f

        return floatArrayOf(
            (inputArray[0] - 25) / maxTempRange,  // maxtemp scaled
            (inputArray[1] - 18) / minTempRange,  // mintemp scaled
            (inputArray[2] - 65) / humidityRange, // humidity scaled
            inputArray[3] / rainfallRange,        // rainfall scaled
            inputArray[4] / windSpeedRange        // windspeed scaled
        )
    }

    /*private suspend fun classifyDisease(weatherDataList: List<WeatherData>): DiseasePredictionUi? {
            return try {
                var predictionUi: DiseasePredictionUi? = null

                // Aggregate the weather data metrics for 7 days
                val avgTemp = weatherDataList.map { it.avgTemp }.average()
                val totalRainfall = weatherDataList.sumOf { it.rainfall }
                val avgHumidity = weatherDataList.map { it.humidity.toDouble() }.average()
                val avgWindSpeed = weatherDataList.map { it.windSpeed }.average()

                // Call the disease prediction API using aggregated data
                diseaseDataSource
                    .classifyDisease(

                        temperature = avgTemp,
                        rainfall = totalRainfall,
                        humidity = avgHumidity,
                        windSpeed = avgWindSpeed
                    )
                    .onSuccess { diseaseName ->
                        predictionUi = DiseasePredictionUi(
                            diseaseName = diseaseName
                        )
                    }
                    .onError { error ->
                        _events.send(WeatherListEvent.Error(error))
                    }

                predictionUi
            } catch (e: Exception) {
                Log.d("DiseasePrediction123", "Exception: $e")
                null
            }
        }

    */

    // Gets the current city name using Geocoder and Location Services
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentCity(): String? {
        return try {
            val location = fusedLocationProviderClient.lastLocation.await()
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)
                }
                addresses?.firstOrNull()?.locality
            }
        } catch (e: Exception) {
            null
        }
    }

    // Loads weather data for the current city
    private fun loadCurrentCityWeather() {
        viewModelScope.launch {
            val currentCityName = getCurrentCity()
            if (currentCityName != null) {
                _currentCity.value = currentCityName
                loadWeatherData(currentCityName)
            } else {
                _currentCity.value = "Unknown"
                //_events.send(WeatherListEvent.Error(NetworkError.LocationError("Unable to fetch location")))
            }
        }
    }
    fun onPredictDisease() {
        viewModelScope.launch {
            val weatherDataList = _state.value.weatherData.map { it.toWeatherData() }
            if (weatherDataList.isNotEmpty()) {
                classifyDisease(weatherDataList)
            } else {
                //_events.send(WeatherListEvent.Error(NetworkError.LocationError("No weather data available for prediction.")))
            }
        }
    }


}