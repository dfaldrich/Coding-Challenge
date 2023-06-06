package com.example.jpmccodingchallenge.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApiHelper {
    private const val baseUrl = "https://api.openweathermap.org/data/2.5/"
    const val iconBaseUrl = "https://openweathermap.org/img/wn/%s@2x.png"
    const val openWeatherApiKey = "5f33ffb54db5754d4555bc1e376273b4"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}