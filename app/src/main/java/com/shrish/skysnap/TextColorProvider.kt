package com.shrish.skysnap

import com.shrish.skysnap.R

object TextColorProvider {
    fun getTextColor(condition: String): Int {
        val lowered = condition.lowercase()
        return when {
            "snow" in lowered || "blizzard" in lowered -> R.color.snowy_text
            "rain" in lowered || "drizzle" in lowered || "thunder" in lowered -> R.color.rainy_text
            "sun" in lowered || "clear" in lowered -> R.color.sunny_text
            "cloud" in lowered || "overcast" in lowered -> R.color.cloudy_text
            else -> R.color.cloudy_text
        }
    }
}