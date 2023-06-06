package com.example.jpmccodingchallenge.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherDetails(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial",
    ): Response<WeatherData>

    @GET("weather")
    suspend fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial",
    ): Response<WeatherData>
}