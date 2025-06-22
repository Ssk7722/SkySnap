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
    private lateinit var hourlyAdapter: HourlyAdapter

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
    private lateinit var weeklyContent: LinearLayout
    private lateinit var weeklyCard: CardView

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
        weeklyContent = findViewById(R.id.weeklyContent)
        weeklyCard = findViewById(R.id.weeklyCard)

        hourlyAdapter = HourlyAdapter()
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

        val forecastList = data.forecast.forecastday
        renderWeeklyForecast(forecastList)

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
    }

    private fun renderHourlyForecast(hourlyList: List<HourlyWeather>) {
        hourRow1.removeAllViews()
        hourRow2.removeAllViews()

        for ((index, hourData) in hourlyList.withIndex()) {
            val item = hourlyAdapter.createHourlyItem(this, hourData, isNow = index == 0)
            if (index < 5) {
                hourRow1.addView(item)
            } else {
                hourRow2.addView(item)
            }
        }
    }

    private fun renderWeeklyForecast(forecast: List<ForecastDay>) {
        weeklyContent.removeAllViews()

        for ((index, day) in forecast.withIndex()) {
            val dayLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = if (index == 0) 0 else 16
                }
                gravity = Gravity.CENTER_VERTICAL
            }

            val dayName = TextView(this).apply {
                text = if (index == 0) "Today" else if (index == 1) "Tomorrow" else getDayName(day.date)
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val icon = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(48, 48)
                setImageResource(
                    when {
                        "rain" in day.day.condition.text.lowercase() -> R.drawable.rainy
                        "cloud" in day.day.condition.text.lowercase() -> R.drawable.cloud
                        "sun" in day.day.condition.text.lowercase() || "clear" in day.day.condition.text.lowercase() -> R.drawable.sunny
                        else -> R.drawable.cloud
                    }
                )
            }

            val temp = TextView(this).apply {
                text = "${day.day.maxtemp_c.toInt()}° / ${day.day.mintemp_c.toInt()}°"
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            dayLayout.addView(dayName)
            dayLayout.addView(icon)
            dayLayout.addView(temp)
            weeklyContent.addView(dayLayout)
        }

        val condition = forecast.firstOrNull()?.day?.condition?.text ?: ""
        weeklyCard.setBackgroundResource(HourlyBackgroundProvider.getHourlyCardBackground(condition))
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