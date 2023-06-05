package com.vsu.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.vsu.picstorm.data.model.response.PictureResponse
import com.vsu.picstorm.util.BitmapUtils

fun PictureResponse.mapToDomain(width: Int): Bitmap? {
    var bitmap: Bitmap? = null
    picture?.let {
        val byteData: ByteArray = Base64.decode(picture, Base64.DEFAULT)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(
            byteData,
            0,
            byteData.size,
            options
        )

        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, width)
        options.inJustDecodeBounds = false

        bitmap = BitmapFactory.decodeByteArray(
            byteData,
            0,
            byteData.size,
            options
        )

    }
    return bitmap
}