package com.shrish.skysnap

import android.graphics.Color

object TextColorProvider {
    fun getTextColor(conditionText: String): Int {
        val condition = conditionText.lowercase()
        return when {
            "rain" in condition -> Color.parseColor("#C9E8E0")
            "cloud" in condition || "overcast" in condition || "partly" in condition -> Color.parseColor("#AED5E4")
            "snow" in condition -> Color.parseColor("#E4F1F9")
            "sun" in condition || "clear" in condition -> Color.parseColor("#EFAA82")
            else -> Color.WHITE // fallback
        }
    }
}
