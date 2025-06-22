package com.shrish.skysnap

import com.shrish.skysnap.R

object WeatherBackgroundSelector {

    data class BackgroundStyle(
        val cardColor: Int,
        val gradientStart: Int,
        val gradientEnd: Int
    )

    fun getStyle(conditionText: String, precipMm: Double? = null, cloud: Int? = null): BackgroundStyle {
        val lower = conditionText.lowercase()

        return when {
            // 🌧️ Rainy if "rain" present AND significant rain reported
            "rain" in lower && (precipMm ?: 0.0) > 0.1 -> BackgroundStyle(
                cardColor = R.color.card_rainy,
                gradientStart = R.color.bg_gradient_start_rainy,
                gradientEnd = R.color.bg_gradient_end_rainy
            )

            // 🌨️ Snow detection (snowy UI uses cloud.png as per your grace’s order)
            "snow" in lower || "snowy" in lower || "blizzard" in lower -> BackgroundStyle(
                cardColor = R.color.card_cloudy,
                gradientStart = R.color.bg_gradient_start_cloudy,
                gradientEnd = R.color.bg_gradient_end_cloudy
            )

            // ☁️ Cloudy if "cloud" or "overcast"
            "cloud" in lower || "overcast" in lower -> BackgroundStyle(
                cardColor = R.color.card_cloudy,
                gradientStart = R.color.bg_gradient_start_cloudy,
                gradientEnd = R.color.bg_gradient_end_cloudy
            )

            // ☀️ Sunny if "sunny" or "clear"
            "sunny" in lower || "clear" in lower -> BackgroundStyle(
                cardColor = R.color.card_sunny,
                gradientStart = R.color.bg_gradient_start_sunny,
                gradientEnd = R.color.bg_gradient_end_sunny
            )

            // 🧪 Fallback: cloudy (for unknown or ambiguous conditions)
            else -> BackgroundStyle(
                cardColor = R.color.card_cloudy,
                gradientStart = R.color.bg_gradient_start_cloudy,
                gradientEnd = R.color.bg_gradient_end_cloudy
            )
        }
    }
}
