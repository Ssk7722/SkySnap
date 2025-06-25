package com.shrish.skysnap
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private lateinit var mainCard: CardView
    private lateinit var hourRow1: LinearLayout
    private lateinit var hourRow2: LinearLayout
    private lateinit var hourlyCardContent: LinearLayout
    private lateinit var weeklyContent: LinearLayout
    private lateinit var weeklyCard: CardView

    private val apiKey = "d189a34eefc94e11b1b120814252006"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        hourlyAdapter = HourlyAdapter()

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
            if (location != null) {
                fetchWeather(location)
            } else {
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(location: Location) {
        RetrofitInstance.api.getWeatherData(apiKey, "${location.latitude},${location.longitude}")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            WeatherHolder.weatherResponse = it
                            updateUI(it)
                        }
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

        val condition = current.condition.text.lowercase(Locale.getDefault())
        val style = WeatherStyleProvider.getProvider(condition)

        tvCity.text = "${location.name}, ${location.region}"
        tvTemp.text = "${current.temp_c.toInt()}°"
        tvCondition.text = current.condition.text
        tvDay.text = getDayName(location.localtime)
        tvDate.text = formatDate(location.localtime)
        tvExtras.text = "Feels like ${current.feelslike_c.toInt()}° | Sunset ${forecastDay?.astro?.sunset ?: "--:--"}"

        rootLayout.setBackgroundResource(style.getBackground())
        mainCard.setBackgroundResource(style.getMainCardDrawable())
        hourlyCardContent.setBackgroundResource(style.getHourlyCardBackground())
        weeklyCard.setBackgroundResource(style.getMainCardDrawable())
        ivIcon.setImageResource(style.getIcon(condition))

        val textColor = ContextCompat.getColor(this, style.getTextColor())
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

        renderWeeklyForecast(data.forecast.forecastday.map {
            Triple(
                getDayName(it.date),
                it.day.condition.text,
                "${it.day.maxtemp_c.toInt()}° / ${it.day.mintemp_c.toInt()}°"
            )
        })
    }

    private fun renderHourlyForecast(hourlyList: List<HourlyWeather>) {
        hourRow1.removeAllViews()
        hourRow2.removeAllViews()
        for ((index, hourData) in hourlyList.withIndex()) {
            val item = hourlyAdapter.createHourlyItem(this, hourData, index == 0)
            if (index < 5) hourRow1.addView(item)
            else hourRow2.addView(item)
        }
    }

    private fun renderWeeklyForecast(data: List<Triple<String, String, String>>) {
        weeklyContent.removeAllViews()
        for ((dayName, condition, temp) in data) {
            val style = WeatherStyleProvider.getProvider(condition.lowercase())

            val card = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                    bottomMargin = 8
                }
                radius = 16f
                cardElevation = 4f
                setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                background = ContextCompat.getDrawable(context, style.getMainCardDrawable())
            }

            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
            }

            val dayText = TextView(this).apply {
                text = dayName
                setTextColor(ContextCompat.getColor(context, style.getTextColor()))
                textSize = 16f
                gravity = Gravity.CENTER
            }

            val icon = ImageView(this).apply {
                setImageResource(style.getIcon(condition))
                layoutParams = LinearLayout.LayoutParams(64, 64)
            }

            val tempText = TextView(this).apply {
                text = temp
                setTextColor(ContextCompat.getColor(context, style.getTextColor()))
                textSize = 14f
                gravity = Gravity.CENTER
            }

            content.addView(dayText)
            content.addView(icon)
            content.addView(tempText)

            card.addView(content)
            weeklyContent.addView(card)
        }
    }

    private fun parseHour(timestamp: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.parse(timestamp)?.time?.div(1000) ?: 0L
    }

    private fun getDayName(dateString: String): String {
        val formats = listOf(
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd"
        )
        val date = formats.mapNotNull { format ->
            try {
                SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
            } catch (e: Exception) {
                null
            }
        }.firstOrNull() ?: return "Today"

        return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    }

    private fun formatDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return ""
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }

}
