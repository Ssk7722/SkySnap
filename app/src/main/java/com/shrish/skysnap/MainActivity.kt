package com.shrish.skysnap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvExtras: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDay: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var rootLayout: View
    private lateinit var mainCard: View
    private lateinit var hourRow1: LinearLayout
    private lateinit var hourRow2: LinearLayout
    private lateinit var hourlyCardContent: LinearLayout
    private lateinit var weeklyCardContent: LinearLayout

    private val apiKey = "d189a34eefc94e11b1b120814252006"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCity = findViewById(R.id.tvCity)
        tvTemp = findViewById(R.id.tvTemp)
        tvCondition = findViewById(R.id.tvCondition)
        tvExtras = findViewById(R.id.tvExtras)
        tvDate = findViewById(R.id.tvDate)
        tvDay = findViewById(R.id.tvDay)
        ivIcon = findViewById(R.id.ivIcon)
        rootLayout = findViewById(R.id.rootLayout)
        mainCard = findViewById(R.id.mainCard)
        hourRow1 = findViewById(R.id.hourRow1)
        hourRow2 = findViewById(R.id.hourRow2)
        hourlyCardContent = findViewById(R.id.hourlyCardContent)
        weeklyCardContent = findViewById(R.id.weeklyContent)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        } else {
            fetchLocation()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
        else Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) fetchWeather(location)
            else Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeather(location: Location) {
        RetrofitInstance.api.getWeatherData(
            apiKey,
            "${location.latitude},${location.longitude}"
        ).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { updateUI(it) }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch weather", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(data: WeatherResponse) {
        val current = data.current
        val location = data.location
        val forecastDay = data.forecast.forecastday.firstOrNull()

        tvCity.text = "${location.name}, ${location.region}"
        tvTemp.text = "${current.temp_c.toInt()}°"
        tvCondition.text = current.condition.text
        tvDay.text = getDayName(location.localtime)
        tvDate.text = formatDate(location.localtime)
        tvExtras.text = "Feels like ${current.feelslike_c.toInt()}° | Sunset ${forecastDay?.astro?.sunset ?: "--:--"}"

        val condition = current.condition.text.lowercase()

        rootLayout.setBackgroundResource(BackgroundProvider.getBackground(condition))
        mainCard.setBackgroundResource(CardDrawableProvider.getCardDrawable(condition))
        hourlyCardContent.setBackgroundResource(HourlyBackgroundProvider.getHourlyCardBackground(condition))

        ivIcon.setImageResource(
            when {
                "rain" in condition -> R.drawable.rainy
                "cloud" in condition || "overcast" in condition || "snow" in condition -> R.drawable.cloud
                "sun" in condition || "clear" in condition -> R.drawable.sunny
                else -> R.drawable.cloud
            }
        )

        val textColor = TextColorProvider.getTextColor(condition)
        listOf(tvCity, tvTemp, tvCondition, tvExtras, tvDate, tvDay).forEach {
            it.setTextColor(textColor)
        }

        val hourlyList = forecastDay?.hour?.take(10)?.mapIndexed { index, hour ->
            HourlyWeather(
                time = parseHour(hour.time),
                temperature = hour.temp_c.toInt(),
                condition = hour.condition.text
            )
        } ?: emptyList()

        renderHourlyForecast(hourlyList)

        val dailyForecasts = data.forecast.forecastday.take(7).map {
            Triple(it.date, it.day.avgtemp_c.toInt(), it.day.condition.text)
        }

        renderWeeklyForecast(dailyForecasts)
    }

    private fun renderHourlyForecast(hourlyList: List<HourlyWeather>) {
        hourRow1.removeAllViews()
        hourRow2.removeAllViews()

        for ((index, hourData) in hourlyList.withIndex()) {
            val item = createHourlyItem(this, hourData, index == 0)
            if (index < 5) {
                hourRow1.addView(item)
            } else {
                hourRow2.addView(item)
            }
        }
    }

    private fun createHourlyItem(context: android.content.Context, hourData: HourlyWeather, isNow: Boolean): LinearLayout {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }

        val sdf = SimpleDateFormat("ha", Locale.getDefault())
        val timeText = TextView(context).apply {
            text = if (isNow) "Now" else sdf.format(Date(hourData.time * 1000))
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

    private fun renderWeeklyForecast(dailyList: List<Triple<String, Int, String>>) {
        weeklyCardContent.removeAllViews()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        dailyList.forEachIndexed { index, (dateStr, temp, condition) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = if (index == 0) 0 else 12
                }
                gravity = Gravity.CENTER_VERTICAL
            }

            val dayLabel = TextView(this).apply {
                text = when (index) {
                    0 -> "Today"
                    1 -> "Tomorrow"
                    else -> {
                        val parsed = sdf.parse(dateStr)
                        SimpleDateFormat("EEE", Locale.getDefault()).format(parsed ?: Date())
                    }
                }
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.poppins_medium)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val iconView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(40, 40)
                setImageResource(
                    when {
                        "rain" in condition.lowercase() -> R.drawable.rainy
                        "cloud" in condition.lowercase() || "overcast" in condition.lowercase() -> R.drawable.cloud
                        "snow" in condition.lowercase() -> R.drawable.cloud
                        else -> R.drawable.sunny
                    }
                )
            }

            val tempView = TextView(this).apply {
                text = "$temp°"
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.poppins_regular)
                setPadding(12, 0, 0, 0)
            }

            row.addView(dayLabel)
            row.addView(iconView)
            row.addView(tempView)
            weeklyCardContent.addView(row)
        }
    }

    private fun parseHour(timestamp: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.parse(timestamp)?.time?.div(1000) ?: 0L
    }

    private fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return "Today"
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    }

    private fun formatDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return ""
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }
}
