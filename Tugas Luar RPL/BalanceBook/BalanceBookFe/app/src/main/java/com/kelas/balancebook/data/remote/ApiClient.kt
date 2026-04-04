package com.kelas.balancebook.data.remote

import android.content.Context
import com.kelas.balancebook.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://192.168.1.9:8000/api/"

    fun service(context: Context): BalanceBookApi {
        val authInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
            SessionManager.getToken(context)?.let { token ->
                if (token.isNotBlank()) {
                    builder.addHeader("Authorization", "Bearer $token")
                }
            }
            chain.proceed(builder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BalanceBookApi::class.java)
    }
}
