package com.shrish.skysnap

import androidx.annotation.DrawableRes

data class WeatherStyle(
    @DrawableRes val backgroundDrawable: Int,
    val cardColorHex: String,
    val textColorHex: String,
    @DrawableRes val iconDrawable: Int
)

object WeatherStyleHelper {

    fun getStyle(weatherType: WeatherConditionParser.WeatherType, isNight: Boolean): WeatherStyle {
        return when {
            isNight -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_night,
                cardColorHex = "#9090AC",
                textColorHex = "#484A82",
                iconDrawable = R.drawable.night
            )

            weatherType == WeatherConditionParser.WeatherType.RAIN -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_rainy,
                cardColorHex = "#40666A",
                textColorHex = "#C9E8E0",
                iconDrawable = R.drawable.rainy
            )

            weatherType == WeatherConditionParser.WeatherType.SNOW -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_snowy,
                cardColorHex = "#99B8CC",
                textColorHex = "#E4F1F9",
                iconDrawable = R.drawable.cloud
            )

            weatherType == WeatherConditionParser.WeatherType.CLOUD -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardColorHex = "#637178",
                textColorHex = "#AED5E4",
                iconDrawable = R.drawable.cloud
            )

            weatherType == WeatherConditionParser.WeatherType.SUNNY -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_sunny,
                cardColorHex = "#EDC368",
                textColorHex = "#F6C8A4",
                iconDrawable = R.drawable.sunny
            )

            else -> WeatherStyle(
                backgroundDrawable = R.drawable.bg_cloudy,
                cardColorHex = "#637178",
                textColorHex = "#AED5E4",
                iconDrawable = R.drawable.cloud
            )
        }
    }
}