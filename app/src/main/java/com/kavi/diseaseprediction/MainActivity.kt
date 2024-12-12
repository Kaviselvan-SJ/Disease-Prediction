package com.kavi.diseaseprediction

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kavi.diseaseprediction.core.presentation.util.ObserveAsEvents
import com.kavi.diseaseprediction.core.presentation.util.toString
import com.kavi.diseaseprediction.ui.theme.DiseasePredictionTheme
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherDataScreen
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherListAction
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherListEvent
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherListViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiseasePredictionTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Plant Doctor") },
                            actions = {
                                IconButton(onClick = { /* Handle menu action here */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "More options"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors()
                        )
                    }
                ) { innerPadding ->
                    val viewModel = koinViewModel<WeatherListViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val context = LocalContext.current
                    ObserveAsEvents(events = viewModel.events) { event ->
                        when(event) {
                            is WeatherListEvent.Error -> {
                                Toast.makeText(
                                    context,
                                    event.error.toString(context),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    CheckLocationPermission()
                    WeatherDataScreen(
                        state = state,
                        onSearch = { query ->
                            viewModel.onAction(WeatherListAction.OnSearch(query))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )


                }
            }
        }
    }


    @Composable
    private fun CheckLocationPermission() {
        val context = LocalContext.current
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = { requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    Button(onClick = { }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Location Permission Required") },
                text = { Text("This app requires location permission to work.") }
            )
        }
    }

}

