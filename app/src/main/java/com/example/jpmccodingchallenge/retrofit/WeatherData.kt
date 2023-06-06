package com.example.jpmccodingchallenge.retrofit

data class WeatherData(
    val weather: List<WeatherDetails>,
    val main: TemperatureDetails,
    val visibility: Int,
    val wind: WindDetails,
    val name: String,
)

data class WeatherDetails(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

data class TemperatureDetails(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Float,
    val humidity: Float,
)

data class WindDetails(
    val speed: Float,
)