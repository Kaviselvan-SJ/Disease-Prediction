package com.kavi.diseaseprediction

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Rice Blast/False Smut Predict") },
                            colors = TopAppBarDefaults.topAppBarColors()
                        )
                    }
                ) { innerPadding ->
                    CheckLocationPermission()
                    val viewModel = koinViewModel<WeatherListViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val context = LocalContext.current
                    ObserveAsEvents(events = viewModel.events) { event ->
                        when (event) {
                            is WeatherListEvent.Error -> {
                                Toast.makeText(
                                    context,
                                    event.error.toString(context),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()

                    WeatherDataScreen(
                        state = state,
                        currentCity = currentCity,
                        onSearch = { query ->
                            viewModel.onAction(WeatherListAction.OnSearch(query))
                        },
                        onPredictDisease = { viewModel.onPredictDisease() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun CheckLocationPermission() {
        val context = LocalContext.current
        val locationManager = context.getSystemService(LocationManager::class.java)
        val isLocationEnabled =
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        // State to control dialog visibility
        val showPermissionDialog = remember { mutableStateOf(false) }
        val showLocationDialog = remember { mutableStateOf(false) }
        val showCameraDialog = remember { mutableStateOf(false) }
        val showFileAccessDialog = remember { mutableStateOf(false) }

        // Ensure the checks are done only once
        val checkedOnce = remember { mutableStateOf(false) }

        if (!checkedOnce.value) {
            checkedOnce.value = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showPermissionDialog.value = true // Show permission dialog only once
            }
            if (!isLocationEnabled) {
                showLocationDialog.value = true // Show location services dialog only once
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showCameraDialog.value = true
            }
            // Check file access permissions
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showFileAccessDialog.value = true
            }
        }

        // Location Permission Dialog
        if (showPermissionDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showPermissionDialog.value = false // Hide dialog on dismiss
                },
                confirmButton = {
                    TextButton(onClick = {
                        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        showPermissionDialog.value = false // Hide dialog after requesting permission
                    }) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPermissionDialog.value = false // Hide dialog on cancel
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Location Permission Required") },
                text = { Text("This app requires location permission to work.") }
            )
        }

        // Location Services Dialog
        if (showLocationDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showLocationDialog.value = false // Hide dialog on dismiss
                },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                        showLocationDialog.value = false // Hide dialog after navigating
                    }) {
                        Text("Enable Location")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showLocationDialog.value = false // Hide dialog on cancel
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Enable Location Services") },
                text = { Text("This app requires location services to be enabled.") }
            )
        }
    }
}