package com.shrish.skysnap

data class ForecastDay(
    val date: String,
    val day: DayForecast,
    val astro: AstroData,
    val hour: List<HourData>
)

data class DayForecast(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: WeatherCondition
)

data class AstroData(
    val sunset: String
)

data class HourData(
    val time: String,
    val temp_c: Double,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)