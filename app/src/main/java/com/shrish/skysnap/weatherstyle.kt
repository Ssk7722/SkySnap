package com.shrish.skysnap

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

object WeatherStyleProvider {

    data class WeatherStyle(
        @DrawableRes val backgroundDrawable: Int,
        @ColorRes val cardColor: Int,
        @ColorRes val textColor: Int
    )

    fun getStyle(condition: String): WeatherStyle {
        val lower = condition.lowercase()

        return when {
            containsAny(lower, listOf("rain", "shower", "drizzle", "thunderstorm", "storm")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_rainy,
                cardColor = R.color.card_rainy,
                textColor = R.color.text_light
            )

            containsAny(lower, listOf("cloud", "overcast", "mist", "fog", "haze")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardColor = R.color.card_cloudy,
                textColor = R.color.text_dark
            )

            containsAny(lower, listOf("sun", "clear", "bright")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_sunny,
                cardColor = R.color.card_sunny,
                textColor = R.color.text_dark
            )

            containsAny(lower, listOf("snow", "blizzard", "sleet", "ice", "flurry")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_snowy,
                cardColor = R.color.card_snowy,
                textColor = R.color.text_dark
            )

            else -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardColor = R.color.card_cloudy,
                textColor = R.color.text_dark
            )
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { it in text }
    }
}
