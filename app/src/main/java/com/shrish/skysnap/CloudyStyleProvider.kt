package com.shrish.skysnap

object CloudyStyleProvider : IWeatherStyleProvider {

    override fun getMainCardDrawable(): Int = R.drawable.card_cloudy

    override fun getBackground(): Int = R.drawable.bg_cloudy

    override fun getHourlyCardBackground(): Int = R.drawable.bg_hourly_cloudy

    override fun getWeeklyCardDrawable(): Int = R.drawable.bg_hourly_cloudy // ✅ Add this line (use correct drawable)

    override fun getTextColor(): Int = R.color.cloudy_text

    override fun getIcon(condition: String): Int {
        val lowered = condition.lowercase()
        return when {
            "fog" in lowered || "mist" in lowered -> R.drawable.cloud
            "overcast" in lowered || "cloud" in lowered -> R.drawable.cloud
            else -> R.drawable.cloud // fallback
        }
    }
}