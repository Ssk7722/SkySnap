package com.shrish.skysnap

import android.Manifest
import android.R.attr.data
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.shrish.skysnap.WeatherConditionInterpreter.getRealConditionHourly
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var rootLayout: FrameLayout
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvExtras: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDay: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var hourRow1: LinearLayout
    private lateinit var hourRow2: LinearLayout
    private lateinit var weeklyContent: LinearLayout
    private lateinit var blurHourly: BlurView
    private lateinit var blurWeekly: BlurView
    private lateinit var dividerExtras: View
    private lateinit var scrollWeekly: ScrollView
    private var lastVisibleWeeklyIndex = -1

    private val apiKey = "accd151c70a246a1bcd104830252207"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
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
            if (location != null) {
                fetchWeather(location)
            } else {
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(location: android.location.Location) {
        RetrofitInstance.api.getWeatherData(apiKey, "${location.latitude},${location.longitude}", 8)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { weather ->
                            val cleanedCondition = WeatherConditionInterpreter.getRealCondition(
                                condition = weather.current.condition,  // ✅ Correct type: Condition
                                current = weather.current               // ✅ Correct type: WeatherCondition
                            )

                            val weatherType = WeatherConditionParser.detectWeatherType(
                                conditionText = cleanedCondition,
                                current = weather.current
                            )
                            val isNight = weather.current.is_day == 0
                            val layoutId = when {
                                isNight -> R.layout.night
                                weatherType == WeatherConditionParser.WeatherType.RAIN -> R.layout.rainy
                                weatherType == WeatherConditionParser.WeatherType.SNOW -> R.layout.snowy
                                weatherType == WeatherConditionParser.WeatherType.CLOUD -> R.layout.cloudy
                                weatherType == WeatherConditionParser.WeatherType.SUNNY -> R.layout.sunny
                                else -> R.layout.cloudy
                            }

                            setContentView(layoutId)
                            LayoutScaler.applyScalingToRoot(this@MainActivity, findViewById(android.R.id.content))
                            window.decorView.post {
                                bindViews()
                                setupBlur(blurHourly)
                                setupBlur(blurWeekly)
                                setupWeeklyScrollHaptics()
                                updateUI(weather, weatherType)
                            }
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
    private fun scaleView(view: View, baseWidthDp: Float = 411f) {
        val metrics = resources.displayMetrics
        val scaleFactor = metrics.widthPixels / (baseWidthDp * metrics.density)

        view.scaleX = scaleFactor
        view.scaleY = scaleFactor
        view.pivotX = 0f
        view.pivotY = 0f
    }

    private fun bindViews() {
        rootLayout = findViewById(R.id.rootLayout)
        scrollWeekly = findViewById(R.id.scrollWeekly)
        dividerExtras = findViewById(R.id.dividerExtras)
        tvCity = findViewById(R.id.tvCity)
        tvTemp = findViewById(R.id.tvTemp)
        tvCondition = findViewById(R.id.tvCondition)
        tvExtras = findViewById(R.id.tvExtras)
        tvDate = findViewById(R.id.tvDate)
        tvDay = findViewById(R.id.tvDay)
        ivIcon = findViewById(R.id.ivIcon)
        hourRow1 = findViewById(R.id.hourRow1)
        hourRow2 = findViewById(R.id.hourRow2)
        weeklyContent = findViewById(R.id.weeklyContent)
        blurHourly = findViewById(R.id.blurHourly)
        blurWeekly = findViewById(R.id.blurWeekly)
    }

    private fun setupBlur(blurView: BlurView) {
        val rootView = findViewById<ViewGroup>(R.id.rootLayout)
        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)

                val backgroundDrawable = rootView.background
                if (backgroundDrawable !is BitmapDrawable) {
                    Log.e("BlurCheck", "⚠️ Not a BitmapDrawable – blur won't work")
                    return true
                }

                val blurAlgorithm = RenderEffectBlur()
                blurView.setupWith(rootView, blurAlgorithm)
                    .setFrameClearDrawable(backgroundDrawable)
                    .setBlurRadius(20f)
                    .setOverlayColor(Color.TRANSPARENT)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
                return true
            }
        })
    }

    @SuppressLint("ServiceCast")
    private fun setupWeeklyScrollHaptics() {
        scrollWeekly.viewTreeObserver.addOnScrollChangedListener {
            val index = getFirstVisibleChildIndex(weeklyContent, scrollWeekly)
            if (index != -1 && index != lastVisibleWeeklyIndex) {
                lastVisibleWeeklyIndex = index
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = getSystemService(VibratorManager::class.java)
                    manager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(VIBRATOR_SERVICE) as Vibrator
                }
                vibrator?.vibrate(VibrationEffect.createOneShot(50, 255))
            }
        }
    }

    private fun getFirstVisibleChildIndex(container: LinearLayout, scrollView: ScrollView): Int {
        val scrollTop = scrollView.scrollY
        val scrollBottom = scrollTop + scrollView.height
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val visibleHeight = minOf(child.bottom, scrollBottom) - maxOf(child.top, scrollTop)
            if (visibleHeight > child.height / 2) return i
        }
        return -1
    }

    private fun updateUI(data: WeatherResponse, weatherType: WeatherConditionParser.WeatherType) {
        val current = data.current
        val location = data.location
        val forecastDay = data.forecast.forecastday.firstOrNull()
        val isNight = current.is_day == 0
        val allHours = data.forecast.forecastday.flatMap { it.hour }
        tvCity.text = "${location.name}, ${location.region}"
        tvTemp.text = "${current.temp_c.toInt()}°"
        val cleanedCondition = WeatherConditionInterpreter.getRealCondition(current.condition, current)
        tvCondition.text = cleanedCondition

        val style = WeatherStyleHelper.getStyle(weatherType, isNight)

        rootLayout.setBackgroundResource(style.backgroundDrawable)
        val currentIconRes = WeatherConditionInterpreter.getFixedIconResource(cleanedCondition, isNight)
        ivIcon.setImageResource(currentIconRes)

        val textColor = Color.parseColor(style.textColorHex)
        listOf(tvCity, tvTemp, tvCondition, tvDay, tvDate, tvExtras).forEach {
            it.setTextColor(textColor)
        }

        blurHourly.setOverlayColor(Color.parseColor(style.cardColorHex))
        blurWeekly.setOverlayColor(Color.parseColor(style.cardColorHex))
        dividerExtras.setBackgroundColor(textColor)

        tvDay.text = getDayName(location.localtime)
        tvDate.text = formatDate(location.localtime)
        tvExtras.text = "Feels like ${current.feelslike_c.toInt()}°"

        forecastDay?.astro?.sunset?.let {
            tvExtras.text = "${tvExtras.text} | Sunset $it"
        }
        val tz = TimeZone.getTimeZone(location.tz_id)


        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(data.location.tz_id) // e.g., America/Los_Angeles Set to the location's timezone, same as above
        val nowCalendar = Calendar.getInstance(TimeZone.getTimeZone(data.location.tz_id)).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = nowCalendar.timeInMillis

        val upcomingHours = allHours.filter {
            val forecastTime = sdf.parse(it.time)?.time ?: 0L
            forecastTime >= now
        }

        val hourlyList = upcomingHours.take(10).mapIndexed { index, hour ->
            val hourTime = if (index == 0) now / 1000 else parseHour(hour.time, data.location.tz_id)
            val isHourNight = hour.is_day == 0
            HourlyWeather(
                time = hourTime,
                temperature = hour.temp_c.toInt(),
                condition = getRealConditionHourly(hour.condition, hour),
                isNight = isHourNight
            )
        }
        renderHourlyForecast(hourlyList,location.tz_id) // ✅ Now it's List<HourlyWeather>

        val weeklyList = data.forecast.forecastday.take(8).map {
            Triple(getDayName(it.date), it.day.condition.text, "${it.day.maxtemp_c.toInt()}° ${it.day.mintemp_c.toInt()}°")
        }
        renderWeeklyForecast(weeklyList)
    }

    private fun renderHourlyForecast(hourlyList: List<HourlyWeather>, tzId: String){
        hourRow1.removeAllViews()
        hourRow2.removeAllViews()
        for ((index, hourData) in hourlyList.withIndex()) {
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(8, 8, 8, 8)

                addView(TextView(context).apply {
                    text = if (index == 0) "Now" else formatHour(hourData.time) // ✅ fixed
                    textSize = 12f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                })

                addView(ImageView(context).apply {
                    setImageResource(WeatherConditionInterpreter.getFixedIconResource(hourData.condition, hourData.isNight))
                    layoutParams = LinearLayout.LayoutParams(48, 48)
                })

                addView(TextView(context).apply {
                    text = "${hourData.temperature}°"
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                })
            }
            if (index < 5) hourRow1.addView(item) else hourRow2.addView(item)
        }
    }

    private fun renderWeeklyForecast(data: List<Triple<String, String, String>>) {
        weeklyContent.removeAllViews()
        for ((dayName, _, temp) in data) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(24, 12, 24, 12)
            }

            val dayText = TextView(this).apply {
                text = dayName
                setTextColor(Color.WHITE)
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
            }

            val tempText = TextView(this).apply {
                text = temp
                setTextColor(Color.WHITE)
                textSize = 14f
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
            }

            row.addView(dayText)
            row.addView(tempText)
            weeklyContent.addView(row)
        }
    }

    private fun parseHour(timestamp: String, timeZoneId: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(timeZoneId)
        return sdf.parse(timestamp)?.time?.div(1000) ?: 0L
    }

    private fun formatHour(timestamp: Long): String {
        if (timestamp == 0L) return "Now"

        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp * 1000 }
        val hour = calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        return if (hour == 0) "12 $amPm" else "$hour $amPm"
    }

    private fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val inputDate = sdf.parse(dateString)
            val today = Calendar.getInstance()
            val given = Calendar.getInstance().apply { time = inputDate!! }
            if (today.get(Calendar.YEAR) == given.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == given.get(Calendar.DAY_OF_YEAR)
            ) "Today" else SimpleDateFormat("EEEE", Locale.getDefault()).format(inputDate!!)
        } catch (e: Exception) {
            "Today"
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(dateString)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date!!)
        } catch (_: Exception) {
            ""
        }
    }
}