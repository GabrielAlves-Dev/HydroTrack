package com.gabriel.hydrotrack.data.weather

import com.google.gson.annotations.SerializedName

data class WeatherData(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String,
    val timezone: Long,
    val id: Long,
    val dt: Long
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double,
    val deg: Int
)
