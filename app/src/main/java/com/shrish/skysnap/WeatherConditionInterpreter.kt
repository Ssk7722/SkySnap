package com.shrish.skysnap

object WeatherConditionInterpreter {

    fun getRealCondition(condition: Condition, current: Current): String {
        val original = condition.text.lowercase()

        return when {
            original.contains("patchy rain") && current.precip_mm < 0.5 && current.humidity < 70 -> "Clear"
            original.contains("patchy rain") && current.precip_mm < 0.5 -> "Cloudy"
            original.contains("overcast") && current.uv < 6 -> "Partly Cloudy"
            original.contains("mist") && current.wind_kph < 5 -> "Clear"
            current.is_day == 0 && original.contains("clear") -> "Clear Night"
            current.is_day == 0 && original.contains("cloud") -> "Cloudy Night"
            else -> condition.text
        }
    }

    fun getRealConditionHourly(condition: Condition, hour: Hour): String {
        val original = condition.text.lowercase()

        return when {
            original.contains("patchy rain") && hour.precip_mm < 0.5 && hour.humidity < 70 -> "Clear"
            original.contains("patchy rain") && hour.precip_mm < 0.5 -> "Cloudy"
            original.contains("overcast") && hour.uv < 6 -> "Partly Cloudy"
            original.contains("mist") && hour.wind_kph < 5 -> "Clear"
            hour.is_day == 0 && original.contains("clear") -> "Clear Night"
            hour.is_day == 0 && original.contains("cloud") -> "Cloudy Night"
            else -> condition.text
        }
    }

    fun getFixedIconResource(condition: String, isNight: Boolean): Int {
        val lower = condition.trim().lowercase()

        return when {
            // 🌙 Clear night icon (strict match only)
            isNight && (lower == "clear night" || lower == "clear") -> R.drawable.night

            // ☀️ Sunny or clear during day
            !isNight && (lower == "sunny" || lower == "clear") -> R.drawable.sunny

            // ☁️ Cloud-related
            "cloud" in lower || "overcast" in lower -> R.drawable.cloud

            // 🌧️ Rain-related
            "rain" in lower || "shower" in lower || "drizzle" in lower -> R.drawable.rainy

            // ❄️ Snow-related
            "snow" in lower || "blizzard" in lower || "sleet" in lower -> R.drawable.cloud

            // ⚡ Thunderstorm
            "thunder" in lower || "storm" in lower -> R.drawable.rainy

            // 🌫 Fog, mist, haze
            "fog" in lower || "mist" in lower || "haze" in lower -> R.drawable.cloud

            // Default fallback
            else -> R.drawable.cloud
        }
    }
}