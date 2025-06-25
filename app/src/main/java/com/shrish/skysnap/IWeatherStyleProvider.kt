package com.shrish.skysnap
interface IWeatherStyleProvider {
    fun getMainCardDrawable(): Int
    fun getBackground(): Int
    fun getHourlyCardBackground(): Int
    fun getWeeklyCardDrawable(): Int // ✅ Required!
    fun getTextColor(): Int
    fun getIcon(condition: String): Int
}