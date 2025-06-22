package com.shrish.skysnap

data class HourlyWeather(
    val time: Long,            // UNIX timestamp
    val temperature: Int,      // Temperature in Celsius
    val condition: String      // Weather description (e.g., Sunny, Cloudy)
)
