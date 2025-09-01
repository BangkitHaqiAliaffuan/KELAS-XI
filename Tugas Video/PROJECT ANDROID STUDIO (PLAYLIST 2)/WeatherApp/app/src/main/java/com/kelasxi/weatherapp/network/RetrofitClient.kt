package com.kelasxi.weatherapp.network

import android.util.Log
import com.kelasxi.weatherapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val TAG = "RetrofitClient"
    
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, "HTTP: $message")
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "Making request to: ${request.url}")
            
            val response = chain.proceed(request)
            Log.d(TAG, "Response: ${response.code} ${response.message}")
            
            response
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}
