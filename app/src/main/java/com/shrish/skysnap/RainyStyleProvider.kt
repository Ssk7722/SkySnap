package com.shrish.skysnap

object RainyStyleProvider : IWeatherStyleProvider {

    override fun getMainCardDrawable(): Int = R.drawable.card_rainy

    override fun getBackground(): Int = R.drawable.bg_rainy

    override fun getHourlyCardBackground(): Int = R.drawable.bg_hourly_rainy

    override fun getWeeklyCardDrawable(): Int = R.drawable.bg_hourly_rainy // ✅ Add this line (create or fallback)

    override fun getTextColor(): Int = R.color.rainy_text

    override fun getIcon(condition: String): Int {
        val lowered = condition.lowercase()
        return when {
            "thunder" in lowered -> R.drawable.rainy
            "drizzle" in lowered -> R.drawable.rainy
            "rain" in lowered -> R.drawable.rainy
            "cloud" in lowered || "overcast" in lowered -> R.drawable.cloud
            else -> R.drawable.rainy
        }
    }
}