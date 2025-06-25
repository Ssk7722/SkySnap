package com.shrish.skysnap

object BackgroundProvider {
    fun getBackground(condition: String): Int {
        val lower = condition.lowercase()
        return when {
            "rain" in lower -> R.drawable.bg_rainy
            "cloud" in lower || "overcast" in lower -> R.drawable.bg_cloudy
            "sun" in lower || "clear" in lower -> R.drawable.bg_sunny
            else -> R.drawable.bg_cloudy
        }
    }
}