package com.vsu.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.vsu.picstorm.data.model.response.PictureResponse

fun PictureResponse.mapToDomain(): Bitmap? {
    var bitmap: Bitmap? = null
    picture?.let {
        val byteData: ByteArray = Base64.decode(picture, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(
            byteData,
            0,
            byteData.size
        )
    }
    return bitmap
}