package com.kavi.diseaseprediction.weather.presentation.weather_List

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
                var diseasePredictionUi: DiseasePredictionUi? = null
                if (weatherDataList.isNotEmpty()) {
                    val latestWeather = weatherDataList.first() // Use the latest weather for prediction
                    diseasePredictionUi = classifyDisease(latestWeather)
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
    private suspend fun classifyDisease(weatherData: WeatherData): DiseasePredictionUi? {
        return try {
            var predictionUi: DiseasePredictionUi? = null
            diseaseDataSource
                .classifyDisease(
                    temperature = weatherData.avgTemp,
                    rainfall = weatherData.rainfall,
                    humidity = weatherData.humidity.toDouble(),
                    windSpeed = weatherData.windSpeed
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
            val latestWeather = _state.value.weatherData.firstOrNull()
            if (latestWeather != null) {
                classifyDisease(latestWeather.toWeatherData())
            } else {
                //_events.send(WeatherListEvent.Error(NetworkError.LocationError("No weather data available for prediction.")))
            }
        }
    }


}
