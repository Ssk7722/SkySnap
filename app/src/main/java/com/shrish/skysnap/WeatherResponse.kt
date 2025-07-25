package com.shrish.skysnap

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime: String
)

data class Current(
    val temp_c: Double,
    val condition: Condition,
    val feelslike_c: Double,
    val precip_mm: Double,
    val humidity: Int,
    val uv: Double,
    val wind_kph: Double,
    val is_day: Int
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: Condition
)

data class Astro(
    val sunrise: String,
    val sunset: String
)

data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition,
    val is_day: Int,
    val precip_mm: Double,
    val humidity: Int,
    val uv: Double,
    val wind_kph: Double
)