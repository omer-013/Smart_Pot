package com.example.smartpotapp.repository

import com.example.smartpotapp.api.SensorApi
import com.example.smartpotapp.api.SensorData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SensorRepository {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.137.101/") // ESP8266'nın IP adresi güncellendi
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(SensorApi::class.java)

    suspend fun getSensorData(): SensorData {
        return try {
            api.getSensorData()
        } catch (e: Exception) {
            throw Exception("Sensör verileri alınamadı: ${e.message}")
        }
    }
}