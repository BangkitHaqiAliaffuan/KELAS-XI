package com.kelasxi.simpleweatherapp.data.repository

import com.kelasxi.simpleweatherapp.data.api.ApiClient
import com.kelasxi.simpleweatherapp.data.api.WeatherApiService
import com.kelasxi.simpleweatherapp.data.model.ForecastResponse
import com.kelasxi.simpleweatherapp.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository class untuk mengelola data cuaca
 * Mengabstraksi sumber data dari ViewModel
 */
class WeatherRepository {
    
    private val apiService: WeatherApiService = ApiClient.weatherApiService
    
    /**
     * Mendapatkan data cuaca berdasarkan nama kota
     * @param cityName nama kota yang dicari
     * @return Result yang berisi WeatherResponse atau error message
     */
    suspend fun getWeatherData(cityName: String): Result<WeatherResponse> {
        return getWeatherByQuery(cityName)
    }
    
    /**
     * Mendapatkan data cuaca berdasarkan koordinat latitude,longitude
     * @param coordinates koordinat dalam format "latitude,longitude"
     * @return Result yang berisi WeatherResponse atau error message
     */
    suspend fun getWeatherByCoordinates(coordinates: String): Result<WeatherResponse> {
        return getWeatherByQuery(coordinates)
    }
    
    /**
     * Mendapatkan data forecast cuaca 3 hari berdasarkan nama kota
     * @param cityName nama kota yang dicari
     * @param days jumlah hari forecast (default 3)
     * @return Result yang berisi ForecastResponse atau error message
     */
    suspend fun getForecastData(cityName: String, days: Int = 3): Result<ForecastResponse> {
        return getForecastByQuery(cityName, days)
    }
    
    /**
     * Mendapatkan data forecast cuaca berdasarkan koordinat
     * @param coordinates koordinat dalam format "latitude,longitude"
     * @param days jumlah hari forecast (default 3)
     * @return Result yang berisi ForecastResponse atau error message
     */
    suspend fun getForecastByCoordinates(coordinates: String, days: Int = 3): Result<ForecastResponse> {
        return getForecastByQuery(coordinates, days)
    }
    
    /**
     * Private method untuk melakukan query ke API
     * @param query bisa berupa nama kota atau koordinat
     * @return Result yang berisi WeatherResponse atau error message
     */
    private suspend fun getWeatherByQuery(query: String): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<WeatherResponse> = apiService.getCurrentWeather(
                    apiKey = WeatherApiService.API_KEY,
                    query = query
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        Result.success(weatherResponse)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request. Please check the city name."
                        401 -> "Unauthorized. Please check your API key."
                        403 -> "Forbidden. API key might be invalid."
                        404 -> "City not found. Please try a different city name."
                        429 -> "Too many requests. Please try again later."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to load weather data. Error code: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No internet connection. Please check your network."
                    e.message?.contains("timeout") == true -> 
                        "Request timeout. Please try again."
                    else -> 
                        "Failed to load data: ${e.message ?: "Unknown error"}"
                }
                Result.failure(Exception(errorMessage))
            }
        }
    }
    
    /**
     * Private method untuk melakukan forecast query ke API
     * @param query bisa berupa nama kota atau koordinat
     * @param days jumlah hari forecast
     * @return Result yang berisi ForecastResponse atau error message
     */
    private suspend fun getForecastByQuery(query: String, days: Int): Result<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ForecastResponse> = apiService.getForecastWeather(
                    apiKey = WeatherApiService.API_KEY,
                    query = query,
                    days = days
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { forecastResponse ->
                        Result.success(forecastResponse)
                    } ?: Result.failure(Exception("Forecast response body is null"))
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request. Please check the location."
                        401 -> "Unauthorized. Please check your API key."
                        403 -> "Forbidden. API key might be invalid."
                        404 -> "Location not found. Please try a different location."
                        429 -> "Too many requests. Please try again later."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to load forecast data. Error code: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No internet connection. Please check your network."
                    e.message?.contains("timeout") == true -> 
                        "Request timeout. Please try again."
                    else -> 
                        "Failed to load forecast data: ${e.message ?: "Unknown error"}"
                }
                Result.failure(Exception(errorMessage))
            }
        }
    }
}