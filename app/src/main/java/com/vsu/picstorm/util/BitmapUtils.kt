package com.vsu.picstorm.util

import android.graphics.BitmapFactory

class BitmapUtils {
    companion object {
        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            val reqHeight = height * reqWidth / width
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}