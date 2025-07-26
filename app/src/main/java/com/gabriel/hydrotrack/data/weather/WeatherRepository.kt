package com.gabriel.hydrotrack.data.weather

import com.gabiel.hydrotrack.data.weather.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class WeatherRepository {

    private val weatherApiService: WeatherApiService

    private val OPEN_WEATHER_API_KEY = "bd3ad01103aa27a599691ff0626a2f89" // Essa seria minha chave, como o rep é para fins didáticos, eu vou optar por deixar diretamente no código

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData? {
        return try {
            val response = weatherApiService.getCurrentWeather(lat, lon, OPEN_WEATHER_API_KEY)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}