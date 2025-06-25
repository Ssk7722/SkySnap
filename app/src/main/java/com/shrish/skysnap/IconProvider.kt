package com.shrish.skysnap

object IconProvider {
    fun getIcon(condition: String): Int {
        val lower = condition.lowercase()
        return when {
            "rain" in lower -> R.drawable.rainy
            "cloud" in lower || "overcast" in lower || "snow" in lower -> R.drawable.cloud
            "sun" in lower || "clear" in lower -> R.drawable.sunny
            else -> R.drawable.cloud
        }
    }
}