package com.shrish.skysnap

object WeatherStyleProvider {

    // ✅ Public function to fetch the correct style provider
    fun getProvider(condition: String): IWeatherStyleProvider {
        val lowered = condition.lowercase()
        return when {
            "snow" in lowered || "blizzard" in lowered -> SnowyStyleProvider
            "rain" in lowered || "drizzle" in lowered || "thunder" in lowered -> RainyStyleProvider
            "sun" in lowered || "clear" in lowered -> SunnyStyleProvider
            "cloud" in lowered || "overcast" in lowered || "mist" in lowered || "fog" in lowered -> CloudyStyleProvider
            else -> CloudyStyleProvider // Fallback for unrecognized conditions
        }
    }

    // ✅ Helper to get icon based on condition
    fun getIcon(condition: String): Int =
        getProvider(condition).getIcon(condition)

    // ✅ Helper to get hourly card background
    fun getHourlyCardDrawable(condition: String): Int =
        getProvider(condition).getHourlyCardBackground()

    // ✅ Helper to get main weather card background
    fun getMainCardDrawable(condition: String): Int =
        getProvider(condition).getMainCardDrawable()

    // ✅ Helper to get background for root layout
    fun getBackgroundDrawable(condition: String): Int =
        getProvider(condition).getBackground()

    // ✅ Helper to get appropriate text color
    fun getTextColorRes(condition: String): Int =
        getProvider(condition).getTextColor()
}