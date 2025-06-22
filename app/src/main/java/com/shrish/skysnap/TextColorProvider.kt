package com.shrish.skysnap

import android.graphics.Color

object TextColorProvider {
    fun getTextColor(condition: String): Int {
        return when {
            "snow" in condition.lowercase() -> Color.BLACK
            else -> Color.WHITE
        }
    }
}