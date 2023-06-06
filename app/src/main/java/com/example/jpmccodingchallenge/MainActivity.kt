package com.example.jpmccodingchallenge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.jpmccodingchallenge.composeComponents.WeatherWidget
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather")

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<WeatherViewModel>()
    private val LAST_SEARCH = "LAST_SEARCH"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                checkAndFetchLocation()
            }
        }

        checkAndFetchLocation()

        getString(LAST_SEARCH)

        setContent {
            val searchLocation by viewModel.searchLocationLiveData.observeAsState()
            val currentLocation by viewModel.currentLocationLiveData.observeAsState()
            val locationEnabled by viewModel.isLocationEnabledLiveData.observeAsState(false)
            val currentSearch = rememberSaveable {
                mutableStateOf("")
            }
            val scrollState = rememberScrollState()

            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .verticalScroll(scrollState)
            ) {
                TextField(
                    value = currentSearch.value,
                    onValueChange = {
                        currentSearch.value = it
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )

                Button(onClick = {
                    putString(LAST_SEARCH, currentSearch.value)
                    viewModel.fetchWeatherData(currentSearch.value)
                },
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Search",
                        fontSize = 24.sp
                    )
                }

                searchLocation?.let { WeatherWidget(weatherData = it) }

                //If location is enabled, show location weather data. Else show button to get location weather.
                if (locationEnabled) {
                    currentLocation?.let { WeatherWidget(weatherData = it) }
                } else {
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(50.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Get Location Weather",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }

    /**
     * Originally I was going to put this method and the "getString" method
     * in its own repository and the have that repository injected into the
     * WeatherViewModel, but in the interest of time I'm putting it here in
     * the activity.
     */
    private fun putString(key: String, value: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val preferencesKey = stringPreferencesKey(key)
            this@MainActivity.dataStore.edit { preferences ->
                preferences[preferencesKey] = value
            }
        }
    }

    private fun getString(key: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val preferencesKey = stringPreferencesKey(key)
            val flow: Flow<String> = this@MainActivity.dataStore.data.map { preferences ->
                preferences[preferencesKey] ?: ""
            }
            viewModel.fetchWeatherData(flow.first())
        }
    }

    private fun checkAndFetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                viewModel.fetchWeatherDataLatLong(it.latitude, it.longitude)
            }
            viewModel.setLocationEnabled(true)
            return
        }
        viewModel.setLocationEnabled(false)
    }
}