package com.shrish.skysnap

interface BaseStyleProvider {
    fun getBackground(): Int
    fun getMainCardDrawable(): Int
    fun getHourlyCardDrawable(): Int
    fun getWeeklyCardDrawable(): Int
    fun getTextColor(): Int
    fun getWeatherIcon(condition: String): Int
}