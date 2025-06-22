package com.shrish.skysnap

object HourlyBackgroundProvider {
    fun getHourlyCardBackground(condition: String): Int {
        val lc = condition.lowercase()

        return when {
            "rain" in lc -> R.drawable.bg_hourly_rainy
            "cloud" in lc || "overcast" in lc || "partly" in lc -> R.drawable.bg_hourly_cloudy
            "snow" in lc -> R.drawable.bg_hourly_snowy
            "sun" in lc || "clear" in lc -> R.drawable.bg_hourly_sunny
            else -> R.drawable.bg_hourly_default
        }
    }
}