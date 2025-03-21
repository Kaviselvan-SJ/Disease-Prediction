package com.kavi.diseaseprediction.weather.presentation.weather_List

import com.kavi.diseaseprediction.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDataScreen(
    state: WeatherDataState,
    currentCity: String,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onPredictDisease: () -> Unit = {} // Callback for prediction
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val updatedCurrentCity = if (currentCity == "Unknown") {
        "Can't fetch location. Enter manually..."
    } else {
        currentCity
    }
    val alertMinThreshold = 20
    val alertMaxThreshold = 25
    val warningThreshold = 25

    var showAlertForBlast by remember { mutableStateOf(false) }
    var showAlertForSmut by remember { mutableStateOf(false) }
    var showWarningForBlast by remember { mutableStateOf(false) }
    var showWarningForSmut by remember { mutableStateOf(false) }
    var alertMessageForBlast by remember { mutableStateOf("") }
    var alertMessageForSmut by remember { mutableStateOf("") }
    var warningMessageForBlast by remember { mutableStateOf("") }
    var warningMessageForSmut by remember { mutableStateOf("") }
    var searchedCity by remember { mutableStateOf("") }
    val displayedCity = searchedCity.ifEmpty { updatedCurrentCity }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 5.dp,start = 12.dp,end = 12.dp, bottom = 12.dp), // Ensures proper padding
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    /*
                    Image(
                        painter = painterResource(id = R.drawable.logo_cit), // Replace with your image
                        contentDescription = "CIT logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                     */
                    Image(
                        painter = painterResource(id = R.drawable.logo_tnau), // Replace with your image
                        contentDescription = "TNAU logo",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
        // Current Location
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Current Location: $updatedCurrentCity",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(start = 7.dp)
                )
            }
        }

        // Search Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { value -> searchText = value },
                    label = { Text("Search by location") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 5.dp, bottom = 5.dp, end = 8.dp)
                )
                IconButton(

                    onClick = {
                        searchedCity = searchText.text
                        onSearch(searchText.text.ifEmpty { "" }) },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = "Perform Search"
                    )
                }
            }
        }


        // Weather Data or Loading/Error State
        when {
            state.isLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.weatherData.isEmpty() -> {
                item{
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
                                    "No data available..."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { onSearch("") },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(text = "Use Current Location")
                            }
                        }
                    }
                }
            }


            else -> {
                item{
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Disease Prediction UI
                        state.diseasePrediction?.let { prediction ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Result for $displayedCity ( ${prediction.fromDate} to ${prediction.toDate} )",
                                    fontSize = 17.sp,
                                    modifier = Modifier.padding(start = 7.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp), // Add padding to the entire column
                            ) {
                                val blastRisk = categorizeRisk(prediction.blastDiseaseRisk)
                                val smutRisk = categorizeRisk(prediction.smutDiseaseRisk)



                                // Use LaunchedEffect to trigger alert only once per change
                                LaunchedEffect(prediction.blastDiseaseRisk, prediction.smutDiseaseRisk) {

                                    if (prediction.blastDiseaseRisk > alertMinThreshold.toString() && prediction.blastDiseaseRisk <= alertMaxThreshold.toString()) {
                                        showAlertForBlast = true
                                        alertMessageForBlast =
                                            "Alert: Disease Incidence Index (Blast): ${prediction.blastDiseaseRisk}%. Take preventive measures to stop further spread."
                                    }
                                    if (prediction.smutDiseaseRisk > alertMinThreshold.toString() && prediction.smutDiseaseRisk <= alertMaxThreshold.toString()) {
                                        showAlertForSmut = true
                                        alertMessageForSmut =
                                            "Alert: Disease Incidence Index (False Smut) : ${prediction.smutDiseaseRisk}%. Take preventive measures to stop further spread."
                                    }

                                    if(prediction.blastDiseaseRisk > warningThreshold.toString()){
                                        showWarningForBlast = true
                                        warningMessageForBlast = "Warning: Rice Blast incidence exceeds threshold. Immediate action is recommended to prevent further spread."
                                    }
                                    if(prediction.smutDiseaseRisk > warningThreshold.toString()){
                                        showWarningForSmut = true
                                        warningMessageForSmut = " Warning: False Smut incidence exceeds threshold. Immediate action is recommended to prevent further spread of False Smut."
                                    }
                                }

                                // Show alert dialog if needed
                                if (showAlertForBlast) {
                                    AlertDialog(
                                        onDismissRequest = { showAlertForBlast = false },
                                        title = { Text(text = "Alert", fontSize = 20.sp) },
                                        text = { Text(text = alertMessageForBlast, fontSize = 16.sp) },
                                        confirmButton = {
                                            Button(onClick = { showAlertForBlast = false }) {
                                                Text("OK")
                                            }
                                        }
                                    )
                                }

                                if (showAlertForSmut) {
                                    AlertDialog(
                                        onDismissRequest = { showAlertForSmut = false },
                                        title = { Text(text = "Alert", fontSize = 20.sp) },
                                        text = { Text(text = alertMessageForSmut, fontSize = 16.sp) },
                                        confirmButton = {
                                            Button(onClick = { showAlertForSmut = false }) {
                                                Text("OK")
                                            }
                                        }
                                    )
                                }

                                if (showWarningForBlast) {
                                    AlertDialog(
                                        onDismissRequest = { showWarningForBlast = false },
                                        title = { Text(text = "Warning", color = Color.Black,fontSize = 20.sp) },
                                        text = {
                                            Text(
                                                text = warningMessageForBlast,
                                                fontSize = 16.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = { showWarningForBlast = false },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                            ) {
                                                Text("OK")
                                            }
                                        },
                                        containerColor = Color(0xFFFFE0E0) // Light Red Background
                                    )
                                }

                                if (showWarningForSmut) {
                                    AlertDialog(
                                        onDismissRequest = { showWarningForSmut = false },
                                        title = { Text(text = "Warning", color = Color.Black,fontSize = 20.sp) },
                                        text = {
                                            Text(
                                                text = warningMessageForSmut,
                                                fontSize = 16.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = { showWarningForSmut = false },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                            ) {
                                                Text("OK")
                                            }
                                        },
                                        containerColor = Color(0xFFFFE0E0) // Light Red Background
                                    )
                                }

                                // Blast Disease
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 7.dp),
                                    horizontalAlignment = Alignment.Start // Center alignment for both lines
                                ) {
                                    Text(
                                        text = "Blast Disease Risk:",
                                        fontSize = 20.sp,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(
                                            start = 5.dp,
                                            top = 10.dp,
                                            bottom = 4.dp
                                        )
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = " $blastRisk (${prediction.blastDiseaseRisk})",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                                    )
                                }

                                // Space between rows
                                Spacer(modifier = Modifier.height(7.dp))

                                // Smut Disease
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp),
                                    horizontalAlignment = Alignment.Start // Center alignment for both lines
                                ) {
                                    Text(
                                        text = "False Smut Disease Risk:",
                                        fontSize = 20.sp,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 5.dp,top = 10.dp, bottom =4.dp )
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = " $smutRisk (${prediction.smutDiseaseRisk})",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp)) // Space before buttons

                                // Precaution Buttons
                                val context = LocalContext.current

                                Button(
                                    onClick = {
                                        openLink(context, "http://www.agritech.tnau.ac.in/expert_system/paddy/cpdisblast.html")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(text = "Blast Precautions and Remedies")
                                }

                                Button(
                                    onClick = {
                                        openLink(context, "http://www.agritech.tnau.ac.in/expert_system/paddy/cpdisfalsegraindis.html")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(text = "Smut Precautions and Remedies")
                                }
                            }


                            val blastDiseases = listOf(
                                R.drawable.blast_image1 to "Blast Disease Sample 1"
                            )

                            val smutDiseases = listOf(
                                R.drawable.smut_image1 to "Smut Disease Sample 1"
                            )


                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Title for Blast Disease Images
                                Text(
                                    text = "Rice Blast Disease Samples",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Blast Disease Images
                                blastDiseases.forEach { (imageRes, description) ->
                                    DiseaseImage(
                                        painter = painterResource(id = imageRes),
                                        description = description
                                    )
                                }


                                // Title for Smut Disease Images
                                Text(
                                    text = "False Smut Disease Samples",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Smut Disease Images
                                smutDiseases.forEach { (imageRes, description) ->
                                    DiseaseImage(
                                        painter = painterResource(id = imageRes),
                                        description = description
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseImage(painter: Painter, description: String) {
    Image(
        painter = painter,
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp) // Uniform image size
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
            .background(Color.Gray)
    )
}


// Function to categorize risk levels
private fun categorizeRisk(riskPercentage: String): String {
    // Remove the '%' sign and trim spaces
    val cleanedPercentage = riskPercentage.replace("%", "").trim()

    // Parse the cleaned string to a Double
    val risk = cleanedPercentage.toDoubleOrNull() ?: return "Invalid Input"

    return when {
        risk < 20 -> "Very Low Chance"
        risk < 40 -> "Low Chance"
        risk < 60 -> "Moderate Chance"
        risk < 80 -> "High Chance"
        else -> "Very High Chance"
    }
}



private fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
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
