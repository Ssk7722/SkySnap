package com.shrish.skysnap

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter {

    fun createHourlyItem(
        context: Context,
        hourData: HourlyWeather,
        isNow: Boolean = false
    ): LinearLayout {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }

        val timeText = TextView(context).apply {
            text = if (isNow) "Now" else SimpleDateFormat("ha", Locale.getDefault()).format(Date(hourData.time * 1000))
            setTextColor(Color.WHITE)
            textSize = 12f
            typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            gravity = Gravity.CENTER
        }

        val iconView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(48, 48)
            setImageResource(
                when {
                    "rain" in hourData.condition.lowercase() -> R.drawable.rainy
                    "cloud" in hourData.condition.lowercase() || "overcast" in hourData.condition.lowercase() -> R.drawable.cloud
                    "snow" in hourData.condition.lowercase() -> R.drawable.cloud
                    else -> R.drawable.sunny
                }
            )
        }

        val tempText = TextView(context).apply {
            text = "${hourData.temperature}°"
            setTextColor(Color.WHITE)
            textSize = 12f
            typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            gravity = Gravity.CENTER
        }

        itemLayout.addView(timeText)
        itemLayout.addView(iconView)
        itemLayout.addView(tempText)

        return itemLayout
    }
}
