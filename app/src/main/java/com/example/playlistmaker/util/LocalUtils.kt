package com.example.playlistmaker.util

import android.util.TypedValue
import android.view.View
import java.text.SimpleDateFormat
import java.util.Locale

class LocalUtils {
    fun dpToPx(dp: Float, view: View): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            view.resources.displayMetrics).toInt()
    }

    fun dateFormat (date: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(date)
    }
}