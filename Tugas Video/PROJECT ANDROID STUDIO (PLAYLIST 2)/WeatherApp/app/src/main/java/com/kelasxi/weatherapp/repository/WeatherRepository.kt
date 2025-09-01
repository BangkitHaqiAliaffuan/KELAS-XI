package com.kelasxi.weatherapp.repository

import android.util.Log
import com.kelasxi.weatherapp.model.WeatherResponse
import com.kelasxi.weatherapp.network.RetrofitClient
import com.kelasxi.weatherapp.utils.Constants
import retrofit2.Response

class WeatherRepository {
    
    companion object {
        private const val TAG = "WeatherRepository"
    }
    
    private val weatherApiService = RetrofitClient.weatherApiService
    
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Response<WeatherResponse> {
        Log.d(TAG, "Making API call to get weather for coordinates: $latitude, $longitude")
        Log.d(TAG, "Using API key: ${if (Constants.isApiKeySet()) Constants.WEATHER_API_KEY.take(8) + "..." else "NOT_SET"}")
        Log.d(TAG, "API URL: ${Constants.BASE_URL}weather?lat=$latitude&lon=$longitude&appid=${Constants.WEATHER_API_KEY}&units=metric&lang=id")
        
        return try {
            val response = weatherApiService.getCurrentWeather(latitude, longitude)
            Log.d(TAG, "Repository: API call completed - Success: ${response.isSuccessful}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Repository: Exception during API call", e)
            throw e
        }
    }
    
    suspend fun getCurrentWeatherByCity(cityName: String): Response<WeatherResponse> {
        Log.d(TAG, "Making API call to get weather for city: $cityName")
        Log.d(TAG, "Using API key: ${if (Constants.isApiKeySet()) Constants.WEATHER_API_KEY.take(8) + "..." else "NOT_SET"}")
        Log.d(TAG, "API URL: ${Constants.BASE_URL}weather?q=$cityName&appid=${Constants.WEATHER_API_KEY}&units=metric&lang=id")
        
        return try {
            val response = weatherApiService.getCurrentWeatherByCity(cityName)
            Log.d(TAG, "Repository: City API call completed - Success: ${response.isSuccessful}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Repository: Exception during city API call", e)
            throw e
        }
    }
}
