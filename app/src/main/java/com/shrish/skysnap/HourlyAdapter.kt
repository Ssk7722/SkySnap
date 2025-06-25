package com.shrish.skysnap

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat

class HourlyAdapter(private val context: Context, private val hourlyList: List<HourlyWeather>) : BaseAdapter() {

    override fun getCount(): Int = hourlyList.size
    override fun getItem(position: Int): Any = hourlyList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false)

        val time = view.findViewById<TextView>(R.id.tvTime)
        val temp = view.findViewById<TextView>(R.id.tvTemp)
        val icon = view.findViewById<ImageView>(R.id.ivCondition)
        val container = view.findViewById<LinearLayout>(R.id.hourlyContainer)

        val weather = hourlyList[position]

        time.text = if (position == 0) "Now" else formatHour(weather.time)
        temp.text = "${weather.temperature}°"
        icon.setImageResource(WeatherStyleProvider.getIcon(weather.condition))

        // Set background and text color dynamically
        container.background = context.getDrawable(WeatherStyleProvider.getHourlyCardDrawable(weather.condition))
        val textColor = ContextCompat.getColor(context, TextColorProvider.getTextColor(weather.condition))
        time.setTextColor(textColor)
        temp.setTextColor(textColor)

        return view
    }

    private fun formatHour(timestamp: Long): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val hour = calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        return if (hour == 0) "12 $amPm" else "$hour $amPm"
    }
}