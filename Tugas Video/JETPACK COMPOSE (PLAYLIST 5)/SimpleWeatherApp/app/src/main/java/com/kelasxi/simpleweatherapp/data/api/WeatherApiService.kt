package com.kelasxi.simpleweatherapp.data.api

import com.kelasxi.simpleweatherapp.data.model.ForecastResponse
import com.kelasxi.simpleweatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface untuk API service cuaca
 * Menggunakan WeatherAPI.com (bisa diganti dengan OpenWeatherMap atau API lainnya)
 */
interface WeatherApiService {
    
    /**
     * Mendapatkan data cuaca berdasarkan nama kota
     * @param apiKey API key untuk WeatherAPI.com
     * @param query Nama kota yang dicari
     * @param aqi Apakah ingin mendapatkan data air quality index (default: no)
     */
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("aqi") aqi: String = "no"
    ): Response<WeatherResponse>
    
    /**
     * Mendapatkan data forecast cuaca 3 hari ke depan
     * @param apiKey API key untuk WeatherAPI.com
     * @param query Nama kota atau koordinat (lat,lon)
     * @param days Jumlah hari forecast (1-3 untuk gratis, 1-10 untuk premium)
     * @param aqi Apakah ingin mendapatkan data air quality index (default: no)
     * @param alerts Apakah ingin mendapatkan weather alerts (default: no)
     */
    @GET("forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Response<ForecastResponse>
    
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        // API Key - Ini harus didapatkan dari WeatherAPI.com
        // User perlu mendaftar di https://www.weatherapi.com/ untuk mendapatkan API key gratis
        const val API_KEY = "tes min"
    }
}