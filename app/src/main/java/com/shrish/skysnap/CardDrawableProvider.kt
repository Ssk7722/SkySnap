package com.shrish.skysnap

object CardDrawableProvider {
    fun getCardDrawable(condition: String): Int {
        val lc = condition.lowercase()

        return when {
            "rain" in lc -> R.drawable.card_rainy
            "cloud" in lc || "overcast" in lc || "partly" in lc -> R.drawable.card_cloudy
            "snow" in lc -> R.drawable.card_snowy
            "sun" in lc || "clear" in lc -> R.drawable.card_sunny
            else -> R.drawable.card_default
        }
    }
}