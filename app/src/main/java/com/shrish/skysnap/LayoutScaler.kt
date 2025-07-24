package com.shrish.skysnap

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

object LayoutScaler {

    private const val BASE_WIDTH_DP = 411f  // A32 screen width

    fun applyScalingToRoot(context: Context, rootView: View) {
        val currentWidthDp = context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density
        val scaleFactor = currentWidthDp / BASE_WIDTH_DP
        scaleViewHierarchy(rootView, scaleFactor, context)
    }

    private fun scaleViewHierarchy(view: View, scale: Float, context: Context) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                scaleViewHierarchy(view.getChildAt(i), scale, context)
            }
        }

        view.layoutParams?.let { lp ->
            if (lp.width > 0) lp.width = (lp.width * scale).toInt()
            if (lp.height > 0) lp.height = (lp.height * scale).toInt()
            view.layoutParams = lp
        }

        view.setPadding(
            (view.paddingLeft * scale).toInt(),
            (view.paddingTop * scale).toInt(),
            (view.paddingRight * scale).toInt(),
            (view.paddingBottom * scale).toInt()
        )

        if (view is TextView) {
            view.textSize = view.textSize * scale / context.resources.displayMetrics.scaledDensity
        }
    }
}