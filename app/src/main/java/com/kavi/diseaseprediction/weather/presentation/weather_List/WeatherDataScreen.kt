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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDataScreen(
    state: WeatherDataState,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar with Icon
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
                onClick = { onSearch(searchText.text) },
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
                    Text(
                        text = if (searchText.text.isNotEmpty()) {
                            "No results found for '${searchText.text}'"
                        } else {
                            "Fetching weather data for your location..."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
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
            state = WeatherDataState(
                weatherData = List(10) { previewData } // Generates a list of 10 `WeatherDataUi` items
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}
