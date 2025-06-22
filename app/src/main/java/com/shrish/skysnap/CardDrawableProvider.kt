import com.shrish.skysnap.R

object CardDrawableProvider {
    fun getCardDrawable(conditionText: String): Int {
        val condition = conditionText.lowercase()
        return when {
            "rain" in condition -> R.drawable.card_rainy
            "cloud" in condition || "overcast" in condition -> R.drawable.card_cloudy
            "snow" in condition -> R.drawable.card_snowy
            "sun" in condition || "clear" in condition -> R.drawable.card_sunny
            else -> R.drawable.card_cloudy
        }
    }
}
