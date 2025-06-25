package com.shrish.skysnap

object SnowyStyleProvider : IWeatherStyleProvider {

    override fun getMainCardDrawable(): Int = R.drawable.card_snowy

    override fun getBackground(): Int = R.drawable.bg_snowy

    override fun getHourlyCardBackground(): Int = R.drawable.bg_hourly_snowy

    override fun getWeeklyCardDrawable(): Int = R.drawable.bg_hourly_snowy // ✅ Add this line or replace if you have a weekly variant

    override fun getTextColor(): Int = R.color.snowy_text

    override fun getIcon(condition: String): Int {
        val lowered = condition.lowercase()
        return when {
            "blizzard" in lowered -> R.drawable.cloud
            "snow" in lowered -> R.drawable.cloud
            "cloud" in lowered -> R.drawable.cloud
            else -> R.drawable.cloud
        }
    }
}