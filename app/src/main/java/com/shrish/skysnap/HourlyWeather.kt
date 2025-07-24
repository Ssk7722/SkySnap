package com.shrish.skysnap

data class HourlyWeather(
    val time: Long,
    val temperature: Int,
    val condition: String,
    val isNight: Boolean // ✅ NEW FIELD
)