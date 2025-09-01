package com.kelasxi.weatherapp.network

import com.kelasxi.weatherapp.model.WeatherResponse
import com.kelasxi.weatherapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = Constants.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "id"
    ): Response<WeatherResponse>
    
    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = Constants.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "id"
    ): Response<WeatherResponse>
}
