package com.example.jpmccodingchallenge.composeComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jpmccodingchallenge.retrofit.WeatherApiHelper
import com.example.jpmccodingchallenge.retrofit.WeatherData

@Composable
fun WeatherWidget(
    weatherData: WeatherData
) {
    Column(modifier = Modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.LightGray)
    ) {
        Text(
            text = weatherData.name,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
        ) {
            //Note: This isn't working on my emulator, but it works on device
            AsyncImage(
                model = WeatherApiHelper.iconBaseUrl.format(weatherData.weather[0].icon),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = weatherData.weather[0].main,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "Temperature: " + weatherData.main.temp + "F",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Max: " + weatherData.main.temp_max + "F | " + "Min: " + weatherData.main.temp_min + "F | " + "Feels Like: " + weatherData.main.feels_like + "F",
                fontSize = 16.sp,
            )
        }

        Text(
            text = "Wind Speed: " + weatherData.wind.speed + " mph",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
        )
    }
}