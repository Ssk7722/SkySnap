package com.shrish.skysnap

object TextBackgroundProvider {

    fun getBackground(conditionText: String): Int {
        val condition = conditionText.lowercase()

        return when {
            "rain" in condition -> R.drawable.bg_rainy
            "cloud" in condition || "overcast" in condition -> R.drawable.bg_cloudy
            "snow" in condition -> R.drawable.bg_snowy
            "sun" in condition || "clear" in condition -> R.drawable.bg_sunny
            else -> R.drawable.bg_cloudy // default fallback
        }
    }
}
