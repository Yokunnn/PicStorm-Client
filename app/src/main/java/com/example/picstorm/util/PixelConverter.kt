package com.example.picstorm.util

import android.content.Context
import android.util.TypedValue


class PixelConverter {

    companion object {
        fun fromDP (context: Context, dimenId: Int): Int {
            val resources = context.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId.toFloat(),
                resources.displayMetrics
            ).toInt()
        }
    }
}