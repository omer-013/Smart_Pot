package com.example.smartpotapp.api

import retrofit2.http.GET

interface SensorApi {
    @GET("data")
    suspend fun getSensorData(): SensorData
}

data class SensorData(
    val toprak_nem: Int,
    val sicaklik: Int,
    val nem: Int,
    val sulama_durumu: Boolean
) 