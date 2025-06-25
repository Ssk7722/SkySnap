package com.shrish.skysnap

object SunnyStyleProvider : IWeatherStyleProvider {

    override fun getMainCardDrawable(): Int = R.drawable.card_sunny

    override fun getBackground(): Int = R.drawable.bg_sunny

    override fun getHourlyCardBackground(): Int = R.drawable.bg_hourly_sunny

    override fun getWeeklyCardDrawable(): Int = R.drawable.bg_hourly_sunny // ✅ Add this line

    override fun getTextColor(): Int = R.color.sunny_text

    override fun getIcon(condition: String): Int {
        val lowered = condition.lowercase()
        return if ("sun" in lowered || "clear" in lowered) {
            R.drawable.sunny
        } else {
            R.drawable.sunny // fallback still sunny (cloud never wins here)
        }
    }
}