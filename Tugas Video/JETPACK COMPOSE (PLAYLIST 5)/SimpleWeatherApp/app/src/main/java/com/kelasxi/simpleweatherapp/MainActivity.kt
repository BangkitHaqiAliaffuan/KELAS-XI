package com.kelasxi.simpleweatherapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.simpleweatherapp.data.model.ForecastUiState
import com.kelasxi.simpleweatherapp.data.model.WeatherUiState
import com.kelasxi.simpleweatherapp.presentation.components.*
import com.kelasxi.simpleweatherapp.presentation.viewmodel.WeatherViewModel
import com.kelasxi.simpleweatherapp.ui.theme.SimpleWeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleWeatherAppTheme {
                WeatherApp()
            }
        }
    }
}

/**
 * ViewModelFactory untuk menyediakan Context ke ViewModel
 */
class WeatherViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Main Composable function untuk aplikasi cuaca
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    // Permission launcher untuk location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        viewModel.updateLocationPermission(fineLocationGranted || coarseLocationGranted)
    }
    
    // Effect untuk meminta permission saat pertama kali load
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    // Gradient background yang lebih kontras dan readable
    val gradientColors = listOf(
        Color(0xFF1E3A8A), // Deep blue
        Color(0xFF3B82F6), // Medium blue
        Color(0xFF93C5FD)  // Light blue
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Simple Weather App",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Search Field
            SearchField(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = viewModel::searchWeather,
                modifier = Modifier.padding(16.dp)
            )
            
            // Content berdasarkan UI State
            when (val currentState = uiState) {
                is WeatherUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier.padding(top = 64.dp)
                    )
                }
                
                is WeatherUiState.Success -> {
                    WeatherDisplay(
                        weatherData = currentState.weather,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    
                    WeatherDetailsCard(
                        weatherData = currentState.weather,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    // Forecast Content
                    when (val currentForecastState = forecastState) {
                        is ForecastUiState.Loading -> {
                            ForecastLoadingIndicator(
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        
                        is ForecastUiState.Success -> {
                            ForecastCard(
                                forecastData = currentForecastState.forecast,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        
                        is ForecastUiState.Error -> {
                            ForecastErrorState(
                                errorMessage = currentForecastState.message,
                                onRetry = {
                                    val query = searchQuery.ifBlank { "Jakarta" }
                                    viewModel.loadForecast(query)
                                },
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                    
                    // Bottom spacing
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                is WeatherUiState.Error -> {
                    ErrorState(
                        errorMessage = currentState.message,
                        onRetry = viewModel::retry,
                        modifier = Modifier.padding(top = 64.dp)
                    )
                }
            }
        }
    }
}

/**
 * Preview untuk WeatherApp
 */
@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    SimpleWeatherAppTheme {
        WeatherApp()
    }
}