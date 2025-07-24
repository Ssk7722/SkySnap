package com.shrish.skysnap

object WeatherConditionParser {

    enum class WeatherType {
        RAIN, CLOUD, SUNNY, SNOW, NIGHT
    }

    fun detectWeatherType(conditionText: String, current: Current): WeatherType {
        val lower = conditionText.lowercase()

        return when {
            lower.contains("clear") && current.is_day == 0 -> WeatherType.NIGHT
            lower.contains("cloud") && current.is_day == 0 -> WeatherType.NIGHT

            lower.contains("patchy rain") && current.precip_mm < 0.5 && current.humidity < 70 ->
                WeatherType.CLOUD

            lower.contains("overcast") && current.uv > 6 -> WeatherType.SUNNY

            lower.contains("mist") && current.wind_kph < 5 -> WeatherType.SUNNY

            Regex("rain|drizzle|shower|storm").containsMatchIn(lower) -> WeatherType.RAIN
            Regex("snow|sleet|flurries|blizzard").containsMatchIn(lower) -> WeatherType.SNOW
            Regex("cloud|overcast|fog|mist").containsMatchIn(lower) -> WeatherType.CLOUD
            Regex("sun|clear|bright").containsMatchIn(lower) -> WeatherType.SUNNY

            else -> WeatherType.SUNNY
        }
    }
}