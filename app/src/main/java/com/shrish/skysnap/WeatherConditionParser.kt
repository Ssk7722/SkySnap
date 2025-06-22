package com.shrish.skysnap

object WeatherConditionParser {

    enum class WeatherType {
        RAIN, CLOUD, SUNNY, SNOW
    }

    fun detectWeatherType(conditionText: String): WeatherType {
        val lower = conditionText.lowercase()

        return when {
            Regex("rain|drizzle|shower|storm").containsMatchIn(lower) -> WeatherType.RAIN
            Regex("snow|sleet|flurries|blizzard").containsMatchIn(lower) -> WeatherType.SNOW
            Regex("cloud|overcast|fog|mist").containsMatchIn(lower) -> WeatherType.CLOUD
            Regex("sun|clear|bright").containsMatchIn(lower) -> WeatherType.SUNNY
            else -> WeatherType.SUNNY // fallback to sunny to avoid black bg
        }
    }
}
