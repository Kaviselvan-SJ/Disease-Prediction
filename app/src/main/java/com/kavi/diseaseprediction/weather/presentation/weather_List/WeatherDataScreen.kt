package com.kavi.diseaseprediction.weather.presentation.weather_List

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kavi.diseaseprediction.ui.theme.DiseasePredictionTheme
import com.kavi.diseaseprediction.weather.presentation.weather_List.components.DailyWeather
import com.kavi.diseaseprediction.weather.presentation.weather_List.components.previewData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDataScreen(
    state: WeatherDataState,
    currentCity: String,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val updatedCurrentCity = if(currentCity == "Unknown"){
        "Can't fetch location enter manually.."
    }else{
        currentCity
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar with Icon
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Current Location : $updatedCurrentCity"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { value -> searchText = value },
                label = { Text("Search by location") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                )
            )
            IconButton(
                onClick = {
                    // If the search field is empty, fetch the current location
                    onSearch(searchText.text.ifEmpty { "" })
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = "Perform Search"
                )
            }
        }

        // Loading or Weather Data
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.weatherData.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchText.text.isNotEmpty()) {
                                "No results found for '${searchText.text}'"
                            } else {
                                "Fetching weather data for your location..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        // Add a button to trigger fetching weather data for current location
                        if (searchText.text.isEmpty()) {
                            Button(
                                onClick = { onSearch("") },
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally) // Explicitly align the button
                            ) {
                                Text(text = "Use Current Location")
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.weatherData) { weatherDataUi ->
                        DailyWeather(
                            weatherDataUi = weatherDataUi,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun WeatherDataScreenPreview() {
    DiseasePredictionTheme {
        WeatherDataScreen(
            currentCity = "Unknown",
            state = WeatherDataState(
                weatherData = List(10) { previewData } // Generates a list of 10 `WeatherDataUi` items
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}
