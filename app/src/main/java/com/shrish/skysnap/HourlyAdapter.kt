package com.shrish.skysnap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(private var hourlyList: List<HourlyWeather> = emptyList()) :
    RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val ivCondition: ImageView = view.findViewById(R.id.ivCondition)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val cardView: CardView = view.findViewById(R.id.hourlyCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourData = hourlyList[position]

        val displayTime = if (position == 0) {
            "Now"
        } else {
            val sdf = SimpleDateFormat("ha", Locale.getDefault())
            sdf.format(Date(hourData.time * 1000))
        }
        holder.tvTime.text = displayTime

        holder.tvTemp.text = "${hourData.temperature}°"

        val condition = hourData.condition.lowercase()
        when {
            "rain" in condition -> holder.ivCondition.setImageResource(R.drawable.rainy)
            "cloud" in condition || "overcast" in condition -> holder.ivCondition.setImageResource(R.drawable.cloud)
            "snow" in condition -> holder.ivCondition.setImageResource(R.drawable.cloud)
            else -> holder.ivCondition.setImageResource(R.drawable.sunny)
        }

        val bgRes = when {
            "rain" in condition -> R.drawable.bg_hourly_rainy
            "cloud" in condition || "overcast" in condition -> R.drawable.bg_hourly_cloudy
            "snow" in condition -> R.drawable.bg_hourly_snowy
            else -> R.drawable.bg_hourly_sunny
        }
        holder.cardView.setBackgroundResource(bgRes)
    }

    override fun getItemCount(): Int = hourlyList.size

    fun submitList(newList: List<HourlyWeather>) {
        hourlyList = newList
        notifyDataSetChanged()
    }
}
