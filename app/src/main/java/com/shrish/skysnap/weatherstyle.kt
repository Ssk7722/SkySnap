package com.shrish.skysnap

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class WeatherStyle(
    @DrawableRes val backgroundDrawable: Int,
    @DrawableRes val cardDrawable: Int,
    @ColorRes val textColor: Int,
    @DrawableRes val iconDrawable: Int
)

object WeatherStyleHelper {

    fun getStyle(condition: String): WeatherStyle {
        val lower = condition.lowercase()

        return when {
            containsAny(lower, listOf("rain", "shower", "drizzle", "thunderstorm", "storm")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_rainy,
                cardDrawable = R.drawable.card_rainy,
                textColor = R.color.rainy_text,
                iconDrawable = R.drawable.rainy
            )

            containsAny(lower, listOf("cloud", "overcast", "mist", "fog", "haze")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardDrawable = R.drawable.card_cloudy,
                textColor = R.color.cloudy_text,
                iconDrawable = R.drawable.cloud
            )

            containsAny(lower, listOf("sun", "clear", "bright")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_sunny,
                cardDrawable = R.drawable.card_sunny,
                textColor = R.color.sunny_text,
                iconDrawable = R.drawable.sunny
            )

            containsAny(lower, listOf("snow", "blizzard", "sleet", "ice", "flurry")) -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_snowy,
                cardDrawable = R.drawable.card_snowy,
                textColor = R.color.snowy_text,
                iconDrawable = R.drawable.cloud
            )

            else -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardDrawable = R.drawable.card_cloudy,
                textColor = R.color.cloudy_text,
                iconDrawable = R.drawable.cloud
            )
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { it in text }
    }
}