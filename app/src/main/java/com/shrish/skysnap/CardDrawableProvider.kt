package com.shrish.skysnap

object CardDrawableProvider {
    fun getCardDrawable(condition: String): Int {
        val lower = condition.lowercase()
        return when {
            "rain" in lower -> R.drawable.card_rainy
            "cloud" in lower || "overcast" in lower -> R.drawable.card_cloudy
            "sun" in lower || "clear" in lower -> R.drawable.card_sunny
            else -> R.drawable.card_default
        }
    }
}