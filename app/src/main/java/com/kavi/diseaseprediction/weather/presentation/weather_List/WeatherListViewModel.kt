package com.kavi.diseaseprediction.weather.presentation.weather_List

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kavi.diseaseprediction.core.domain.util.NetworkError
import com.kavi.diseaseprediction.core.domain.util.onError
import com.kavi.diseaseprediction.core.domain.util.onSuccess
import com.kavi.diseaseprediction.weather.domain.WeatherData
import com.kavi.diseaseprediction.weather.domain.WeatherDataSource
import com.kavi.diseaseprediction.weather.presentation.models.toWeatherDataUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

class WeatherListViewModel(
    private val weatherDataSource: WeatherDataSource,
    private val context: Context // Add Context to get access to Geocoder
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherDataState())
    val state = _state
        .onStart { loadCurrentCityWeather() } // Initially load the weather data for the current city
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            WeatherDataState()
        )

    private val _events = Channel<WeatherListEvent>()
    val events = _events.receiveAsFlow()

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Handle user actions
    fun onAction(action: WeatherListAction) {
        when (action) {
            is WeatherListAction.OnSearch -> {
                if (action.location.isEmpty()) {
                    loadCurrentCityWeather() // Fetch weather for current location if the search query is empty
                } else {
                    loadData(action.location) // Otherwise, fetch weather for the specified location
                }
            }
            is WeatherListAction.OnCoinClick -> {
                // Handle weather item click
            }
        }
    }

    // Load data for a specific location (city)
    private fun loadData(location: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val today = LocalDate.now() // Get today's date
            val last7Days = (0..6).map {
                today.minusDays(it.toLong()).toString()
            } // Generate last 7 days' dates

            val weatherDataList = mutableListOf<WeatherData>()
            var errorOccurred = false

            try {
                last7Days.forEach { date ->
                    weatherDataSource
                        .getDailyWeather(location = location, date = date)
                        .onSuccess { weatherData ->
                            weatherDataList.addAll(weatherData) // Combine data for each day
                        }
                        .onError { error ->
                            if (!errorOccurred) {
                                errorOccurred = true
                                _events.send(WeatherListEvent.Error(error))
                            }
                        }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        weatherData = if (weatherDataList.isNotEmpty()) {
                            weatherDataList.map { it.toWeatherDataUi() }
                        } else {
                            emptyList()
                        }
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    // Function to get the current city using Geocoder and Location Services
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentCity(): String? {
        return try {
            val location = fusedLocationProviderClient.lastLocation.await() // Fetch current location
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                // Wrap geocoder call in withContext to avoid blocking the main thread
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)
                }
                if (addresses!!.isNotEmpty()) {
                    return addresses[0].locality // Return the city name
                }
            }
            null
        } catch (e: Exception) {
            // Handle any errors that occur during fetching location
            //_events.trySend(WeatherListEvent.Error("Unable to fetch location: ${e.message}"))
            null
        }
    }

    // Call this function to load weather for the current city
    private val _currentCity = MutableStateFlow("Fetching city...")
    val currentCity = _currentCity.asStateFlow()

    private fun loadCurrentCityWeather() {
        viewModelScope.launch {
            val currentCityName = getCurrentCity()
            if (currentCityName != null) {
                _currentCity.value = currentCityName // Update the city name
                loadData(currentCityName) // Fetch data for the current city
            } else {
                _currentCity.value = "Unknown"
            }
        }
    }
}
