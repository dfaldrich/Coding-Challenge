package com.example.jpmccodingchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jpmccodingchallenge.retrofit.WeatherApi
import com.example.jpmccodingchallenge.retrofit.WeatherApiHelper
import com.example.jpmccodingchallenge.retrofit.WeatherData
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherApi: WeatherApi = WeatherApiHelper.getInstance().create(WeatherApi::class.java)
) : ViewModel() {

    private val _searchLocationLiveData = MutableLiveData<WeatherData>()
    val searchLocationLiveData: LiveData<WeatherData> = _searchLocationLiveData

    private val _currentLocationLiveData = MutableLiveData<WeatherData>()
    val currentLocationLiveData: LiveData<WeatherData> = _currentLocationLiveData

    private val _isLocationEnabledLiveData = MutableLiveData<Boolean>()
    val isLocationEnabledLiveData: LiveData<Boolean> = _isLocationEnabledLiveData

    fun setLocationEnabled(enabled: Boolean) {
        _isLocationEnabledLiveData.value = enabled
    }

    /**
     * Fetch weather data using a location name
     */
    fun fetchWeatherData(location: String) {
        viewModelScope.launch {
            val result = weatherApi.getWeatherDetails(location, WeatherApiHelper.openWeatherApiKey)
            if (result.code() == 200) {
                _searchLocationLiveData.postValue(result.body())
            }
        }
    }

    /**
     * Fetch weather data using latitude and longitude
     */
    fun fetchWeatherDataLatLong(lat: Double, long: Double) {
        viewModelScope.launch {
            val result = weatherApi.getWeatherDetails(lat, long, WeatherApiHelper.openWeatherApiKey)
            if (result.code() == 200) {
                _currentLocationLiveData.postValue(result.body())
            }
        }
    }
}