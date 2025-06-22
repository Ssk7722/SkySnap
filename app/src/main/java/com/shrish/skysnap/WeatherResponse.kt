package com.shrish.skysnap

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

// Location details
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime: String
)

// Current weather info
data class Current(
    val temp_c: Double,
    val feelslike_c: Double,
    val condition: Condition,
    val wind_kph: Double,
    val humidity: Int,
    val precip_mm: Double,
    val cloud: Int

)

// Weather condition description and icon
data class Condition(
    val text: String,
    val icon: String
)

// Forecast for multiple days
data class Forecast(
    val forecastday: List<ForecastDay>
)

// Forecast for a single day
data class ForecastDay(
    val date: String,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

// Daily summary
data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

// Sunrise/Sunset times
data class Astro(
    val sunrise: String,
    val sunset: String
)

// Hourly forecast
data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)
