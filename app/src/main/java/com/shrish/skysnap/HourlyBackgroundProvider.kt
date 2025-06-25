package com.shrish.skysnap

object HourlyBackgroundProvider {
    fun getHourlyCardBackground(condition: String): Int {
        val lower = condition.lowercase()
        return when {
            "rain" in lower -> R.drawable.bg_hourly_rainy
            "cloud" in lower || "overcast" in lower -> R.drawable.bg_hourly_cloudy
            "sun" in lower || "clear" in lower -> R.drawable.bg_hourly_sunny
            else -> R.drawable.bg_hourly_cloudy

        }
    }
}