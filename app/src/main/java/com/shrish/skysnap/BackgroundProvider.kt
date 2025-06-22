package com.shrish.skysnap

object BackgroundProvider {
    fun getBackground(condition: String): Int {
        val lc = condition.lowercase()

        return when {
            "rain" in lc -> R.drawable.bg_rainy
            "cloud" in lc || "overcast" in lc || "partly" in lc -> R.drawable.bg_cloudy
            "snow" in lc -> R.drawable.bg_snowy
            "sun" in lc || "clear" in lc -> R.drawable.bg_sunny
            else -> R.drawable.bg_cloudy
        }
    }
}