package com.example.jpmccodingchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jpmccodingchallenge.retrofit.TemperatureDetails
import com.example.jpmccodingchallenge.retrofit.WeatherApi
import com.example.jpmccodingchallenge.retrofit.WeatherApiHelper
import com.example.jpmccodingchallenge.retrofit.WeatherData
import com.example.jpmccodingchallenge.retrofit.WindDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response

private const val LOCATION = "Atlanta"
private const val LATITUDE = 44.34
private const val LONGITUDE = 10.99

private val response: Response<WeatherData> = Response.success(200, WeatherData(
    weather = emptyList(),
    main = TemperatureDetails(
        temp = 80.0f,
        feels_like = 82.0f,
        temp_min = 78.0f,
        temp_max = 85.0f,
        pressure = 20.0f,
        humidity = 35.0f
    ),
    visibility = 0,
    wind = WindDetails(
        speed = 14.43f
    ),
    name = "Atlanta"
))

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {
    private lateinit var viewModel: WeatherViewModel

    @Mock private lateinit var weatherApi: WeatherApi
    @Mock private lateinit var weatherApiError: WeatherApi
    @Mock private val responseBody: ResponseBody = mock()

    private val errorResponse: Response<WeatherData> = Response.error(404, responseBody)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun initMocks() {
        Dispatchers.setMain(StandardTestDispatcher())
        weatherApi = mock {
            onBlocking { getWeatherDetails(LOCATION, WeatherApiHelper.openWeatherApiKey) } doReturn response
            onBlocking { getWeatherDetails(LATITUDE, LONGITUDE, WeatherApiHelper.openWeatherApiKey) } doReturn response
        }
        weatherApiError = mock {
            onBlocking { getWeatherDetails(LOCATION, WeatherApiHelper.openWeatherApiKey) } doReturn errorResponse
            onBlocking { getWeatherDetails(LATITUDE, LONGITUDE, WeatherApiHelper.openWeatherApiKey) } doReturn errorResponse
        }
    }

    @Test
    fun fetchWeatherData_Success() = runTest {
        viewModel = WeatherViewModel(weatherApi)
        viewModel.fetchWeatherData(LOCATION)
        advanceUntilIdle()
        assertEquals(response.body(), viewModel.searchLocationLiveData.value)
    }

    @Test
    fun fetchWeatherData_Error() = runTest {
        viewModel = WeatherViewModel(weatherApiError)
        viewModel.fetchWeatherData(LOCATION)
        advanceUntilIdle()
        assertNull(viewModel.searchLocationLiveData.value)
    }

    @Test
    fun fetchWeatherDataLatLong_Success() = runTest {
        viewModel = WeatherViewModel(weatherApi)
        viewModel.fetchWeatherDataLatLong(LATITUDE, LONGITUDE)
        advanceUntilIdle()
        assertEquals(response.body(), viewModel.currentLocationLiveData.value)
    }

    @Test
    fun fetchWeatherDataLatLong_Error() = runTest {
        viewModel = WeatherViewModel(weatherApiError)
        viewModel.fetchWeatherDataLatLong(LATITUDE, LONGITUDE)
        advanceUntilIdle()
        assertNull(viewModel.currentLocationLiveData.value)
    }

    @Test
    fun setSearchToLiveData() {
        viewModel = WeatherViewModel(weatherApi)
        viewModel.setLocationEnabled(true)
        assert(viewModel.isLocationEnabledLiveData.value == true)
    }
}