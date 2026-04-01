package com.kelasxi.myapplication.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Production        → https://trashcarebackend-production.up.railway.app/api/
    // Emulator        → http://10.0.2.2:8000/api/
    // ── Physical device (USB debug): PC WiFi IP on the same network ──
    // Physical device → http://192.168.30.212:8000/api/  (php artisan serve --host=0.0.0.0 --port=8000)
    const val BASE_URL = "https://giving-rebirth-production-b635.up.railway.app/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    /** Forces every request to carry Accept: application/json so Laravel
     *  always returns JSON errors instead of HTML redirects. */
    private val jsonAcceptInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(jsonAcceptInterceptor)   // applied before logging so headers appear in logs
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
